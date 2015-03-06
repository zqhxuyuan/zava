/**
 * Copyright 2013 Oak Ridge National Laboratory
 * Author: James Horey <horeyjl@ornl.gov>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package gov.ornl.keva.node;


/**
 * Configuration libs.
 **/
import gov.ornl.config.ConfigFactory;
import gov.ornl.config.Configuration;
import gov.ornl.config.ConfigEntry;

/**
 * Java libraries.
 **/
import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
import java.io.IOException;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.DirectoryStream;
import java.nio.file.StandardOpenOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * Compression libraries. 
 **/
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Decompressor;
import net.jpountz.lz4.LZ4Factory;

/**
 * Keva libs.
 **/
import gov.ornl.keva.table.TableKey;
import gov.ornl.keva.table.TableValue;
import gov.ornl.keva.table.TableValueFactory;
import gov.ornl.keva.sstable.SSTable;
import gov.ornl.keva.core.WriteOptions;
import gov.ornl.keva.core.OptionsSerializer;
import gov.ornl.keva.core.KevaDBException;

/**
 * The write ahead log persists all "put" operations (the key and value) onto
 * disk sequentially. The log is used to ensure that all values are safely persisted
 * since memtables only store the values in memory (and hence may become lost if the
 * machine is rebooted). Our WAL implementation has different flushing policies depending
 * on the safety guarantees the user expects.
 * 
 * @author James Horey
 */
public class WriteAheadLog {
    private static final short MAGIC_NUMBER = 79;

    /**
     * Maximum memory mapped buffer size. Set to a reasonable
     * size so that we don't continually re-map, but not too large. 
     *
     * Currently about 4MB. In the future, probably should be a parameter.
     */
    private static final long MAX_MAP_SIZE = 1024 * 4096;

    /**
     * Flushing policies. 
     **/
    public enum Flush {
	SAFE, UNSAFE
    };

    /**
     * The LZ4 compression classes.
     */
    private static LZ4Factory lz4Factory = LZ4Factory.fastestInstance();
    private static LZ4Compressor compressor = lz4Factory.fastCompressor();
    private static LZ4Decompressor decompressor = lz4Factory.decompressor();

    private KevaDB db; // Used to replay the log. 
    private Flush flushPolicy; // Immediate, time based, or size based
    private long param; // Parameter used by either time or size policy.
    private Path logPath; // Parent directory where logs are stored
    private FileChannel logChannel; // Open channel to current log. 
    private MappedByteBuffer logBuffer; // Buffer to log channel. 
    private long mapStart; // Where we are in the WAL. 

    /**
     * @param db The database to associate with this log
     * @param configFile Path to the configuration file
     */
    public WriteAheadLog(KevaDB db, 
			 String configFile) {
	this.db = db;

	flushPolicy = Flush.UNSAFE; 
	param = 0;
	mapStart = 0;

	// Load the configuration
	loadConfig(configFile);
    }

    /**
     * Start the logging system. We must define a new
     * log to store everything. 
     **/
    public synchronized void createLog() {
	if(logPath == null) {
	    String id = String.format("log_%d", System.currentTimeMillis());
	    try {
		// Try to close the old channel.
		if(logChannel != null) {
		    logChannel.close();
		}

		logPath = Paths.get(db.getLogPath() + 
				    System.getProperty("file.separator") + 
				    id).toAbsolutePath();
		if(!Files.exists(logPath)) {
		    // Create the necessary parent directories.
		    Files.createDirectories(Paths.get(db.getLogPath()).toAbsolutePath());

		    // Make sure that our WAL is created before we return. 
		    logPath = Files.createFile(logPath);

		    // Open up the line of communication. 
		    String flag = "rw";
		    if(flushPolicy == Flush.SAFE) {
			flag += "d";
		    }
		    logChannel = new RandomAccessFile(logPath.toString(), flag).getChannel();

		    // Map the file. 
		    logBuffer = mapWAL();
		}
	    } catch(IOException e) {
		e.printStackTrace();
	    }
	    catch(Exception e) {
		e.printStackTrace();
	    }
	}
    }

    /**
     * Remap the WAL buffer. 
     */
    private MappedByteBuffer mapWAL() {
	MappedByteBuffer buf = null;

	try {
	    buf = 
		logChannel.map(FileChannel.MapMode.READ_WRITE, mapStart, MAX_MAP_SIZE);
	} catch(IOException e) {
	    e.printStackTrace();
	}

	if(buf != null) {
	    mapStart += MAX_MAP_SIZE;
	}

	return buf;
    }

    /**
     * Find the latest log file for the database. 
     **/
    private List<Path> getLogs() {
	List<Path> logs = new ArrayList<>();

	// Compare the modified times. We want to read the
	// oldest logs first. 
	Comparator<Path> comp = 
	    new Comparator<Path>() {
	    public int compare(Path p1, Path p2) {
		try {
		    BasicFileAttributes v1
		    = Files.getFileAttributeView(p1, BasicFileAttributeView.class)
		    .readAttributes();

		    BasicFileAttributes v2
		    = Files.getFileAttributeView(p2, BasicFileAttributeView.class)
		    .readAttributes();

		    return (int)(v2.lastModifiedTime().toMillis() - v1.lastModifiedTime().toMillis());
		}
		catch(IOException e) {
		    e.printStackTrace();
		}

		return 0;
	    }
	};

	try {
	    DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(db.getLogPath()).toAbsolutePath(), "log_*");
	    for (Path entry: stream) {
		logs.add(entry);
	    }
	}
	catch(NoSuchFileException e) {
	}
	catch (IOException e) {
	    e.printStackTrace();
	}

	// Sort the logs. 
	Collections.sort(logs, comp);
	return logs;
    }

    /**
     * Configure the log. 
     **/
    private void loadConfig(String c) {
	ConfigFactory configFactory;
	Configuration conf;

	configFactory = new ConfigFactory();
	if(c != null) {
	    Path p = Paths.get(c);
	    conf = configFactory.getConfig(p.toAbsolutePath().toString());

	    if(conf != null) {
		ConfigEntry entry = conf.get("keva.wal.flush");
		if(entry != null) {
		    ConfigEntry v = entry.getEntry("value");
		    String flush = v.getValues().get(0);

		    if(flush.equals("immediate")) {
			setPolicy(Flush.SAFE);
		    }

		    v = entry.getEntry("param");
		    if(v != null) {
			setPolicyParam(Long.parseLong(v.getValues().get(0)));
		    }
		}
	    }
	}
    }

    /**
     * Set the flushing policy. 
     * FLUSH_IMMEDIATE will flush all values to disk immediately 
     * (equivalent to an OS synch operatiation). FLUSH_TIME will
     * only flush values after a certain period of time has passed. 
     * FLUSH_SIZE will only flush values after the in-memory buffer 
     * grows too large. 
     *
     * @param policy The flushing policy
     */
    public void setPolicy(Flush policy) {
	flushPolicy = policy;
    }

    /**
     * Get the flushing policy.
     *
     * @return The flushing policy
     */
    public Flush getPolicy() {
	return flushPolicy;
    }

    /**
     * The size and time-based flushing policies require an additional
     * parameter to control when to flush. 
     *
     * @param param The flushing policy parameter
     */
    public void setPolicyParam(long param) {
	this.param = param;
    }

    /**
     * Get the flushing policy parameter.
     *
     * @return The flushing policy parameter
     */
    public long getPolicyParam() {
	return param;
    }

    /**
     * Serialize the write. 
     **/
    private ByteBuffer serialize(final TableKey key, 
				 final TableValue value,
				 final WriteOptions options) {
	int size;
	int optionSize = 0;
	byte[] optionBuf = null;
	byte[] keyData = key.serialize();	
	byte[] valueData = value.getBytes();

	int maxCompressed = compressor.maxCompressedLength(valueData.length);
	byte[] compressed = new byte[maxCompressed];
	int actualCompressed = compressor.compress(valueData, 0, valueData.length, 
						   compressed, 0, maxCompressed);
	size = 
	    (Short.SIZE / 8) + 
	    4 * (Integer.SIZE / 8) +
	    (Long.SIZE / 8) +
	    keyData.length + 
	    actualCompressed;

	if(options != null) {
	    optionBuf = OptionsSerializer.getBytes(options);
	    if(optionBuf != null) {
		optionSize = optionBuf.length;
	    }
	    else {
		optionSize = 0;
	    }
	    size += optionSize;
	}

	ByteBuffer buf = ByteBuffer.allocateDirect(size);
	buf.putLong(System.currentTimeMillis());
	buf.putShort(MAGIC_NUMBER); // Magic number.
	
	buf.putInt(keyData.length);
	buf.putInt(valueData.length);
	buf.putInt(actualCompressed);
	buf.putInt(optionSize);

	buf.put(keyData);
	buf.put(compressed, 0, actualCompressed);

	if(optionBuf != null) {
	    buf.put(optionBuf);
	}

	return buf;
    }

    /**
     * Clear the entire WAL history.
     */
    public void clear() {
	// Delete the current block. 
	try {
	    if(logPath != null) {
		Files.delete(logPath);
		logPath = null;
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}

	// Recycle all the old blocks. 
	recycle(Long.MAX_VALUE);
    }

    /**
     * Delete older logs from disk. 
     *
     * @param time The minimum age of the logs. 
     */
    public void recycle(long time) {
	List<Path> toRecycle = new ArrayList<>();

	// Check if the log directory exists. If it doesn't
	// then there isn't anything to recycle. 
	Path logDir = Paths.get(db.getLogPath()).toAbsolutePath();
	if(!Files.exists(logDir)) {
	    return;
	}

	try {	    
	    DirectoryStream<Path> stream = 
		Files.newDirectoryStream(logDir, "log_*");

	    for (Path entry: stream) {
		// System.out.printf("investigating log %s\n", entry);
		// Do not include the current block. 
		if(logPath == null ||
		   !Files.isSameFile(entry, logPath)) {
		    BasicFileAttributes view
			= Files.getFileAttributeView(entry, BasicFileAttributeView.class)
			.readAttributes();

		    if(view.lastModifiedTime().toMillis() < time) {
			// We no longer need this entry. Add to our
			// recycling list. 
			toRecycle.add(entry);
		    }
		}
	    }

	    // Now delete all the values. 
	    for(Path p : toRecycle) {
		Files.delete(p);
	    }
	} catch(IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Log a series of writes. These writes are committed "atomically".
     * Since all writes in the log are serialized, we just need to make
     * sure that all these writes are contiguous. 
     *
     * @param ops Set of write operations
     */
    public void put(final WriteBatch ops) {
	// Lock the log so that we can commit several at a time.
	synchronized(this) {
	    Iterator<TableKey> iter = ops.iterator();
	    while(iter.hasNext()) {
		TableKey key = iter.next();
		for(WriteBatch.TableWrite write : ops.getValues(key)) {
		    ByteBuffer data = serialize(key, write.value, write.options);
		    if(logBuffer.remaining() < data.position()) {
			logBuffer = mapWAL();
		    }

		    data.flip();
		    logBuffer.put(data);
		}
	    }
	}
    }

    /**
     * Log a put operation. 
     *
     * @param key The key of the value. 
     * @param value The value to place into the database. 
     * @param options Write options that define how the 
     * value is written to the memtable. 
     */
    public void put(final TableKey key, 
		    final TableValue value,
		    final WriteOptions options) {
	ByteBuffer data = serialize(key, value, options);
	data.flip();

	synchronized(this) {
	    if(logBuffer.remaining() < data.capacity()) {
		logBuffer = mapWAL();
	    }
	    logBuffer.put(data);
	}
    }

    /**
     * Parse the command from the byte buffer. 
     **/
    private boolean unroll(ByteBuffer b,
			   Map<String, Integer> manifest) throws KevaDBException {
	long time = b.getLong();
			 
	// Check if there are any sstables that are older than
	// this WAL entry. If so, we can safely skip this entry since
	// we know the results have already been persisted. 
	if(manifest != null) {
	    for(String uuid : manifest.keySet()) {
		SSTable table = db.getDiskService().getSSTable(db, uuid, manifest.get(uuid));
		if(table.getModificationTime() <= time) {
		    return true;
		}
	    }
	}

	// Check if this is a valid entry. 
	short magic = b.getShort();
	if(magic != MAGIC_NUMBER) {
	    return false;
	}

	// Read in the various lengths. 
	int keyLength = b.getInt();
	int valueLength = b.getInt();
	int compressedLength = b.getInt();
	int optionLength = b.getInt();

	// Instantiate the various buffers. 
	byte[] keyBuffer = new byte[keyLength];
	byte[] valueBuffer = new byte[valueLength];
	byte[] compressedBuffer = new byte[compressedLength];

	b.get(keyBuffer);
	b.get(compressedBuffer);

	// Now we need to decompress the value buffer.
	decompressor.decompress(compressedBuffer, 0, valueBuffer, 0, valueLength);

	// Now get the write options. 
	WriteOptions options = null;
	if(optionLength > 0) {
	    byte[] optionBuffer = new byte[optionLength];
	    b.get(optionBuffer);
	    options = OptionsSerializer.writeOptionsFromBytes(optionBuffer);
	}

	TableKey key = TableKey.fromBytes(ByteBuffer.wrap(keyBuffer));
	TableValue value = TableValueFactory.fromBytes(valueBuffer, valueLength);

	// Now put the value into the DB. 
	db.put(key, value, options);
	return true;
    }

    /**
     * Reply a specific WAL. 
     **/
    private void replayLog(Path logPath) 
	throws KevaDBException {
	// Now look at the disk. 
	if(logPath != null) {
	    // Get a list of the sstables already in place. 
	    Map<String, Integer> manifest =
		db.getDiskService().getDataManifests(db, 0, SSTableService.MAX_LEVELS);

	    FileLock fileLock = null;
	    try {
		// Must open in "rw" mode so that we can use filelock. 
		FileChannel fc = FileChannel.open(logPath, 
						  StandardOpenOption.READ,
						  StandardOpenOption.WRITE);
		MappedByteBuffer in = 
		    fc.map(FileChannel.MapMode.READ_ONLY, 0, (int)fc.size());

		fileLock = fc.tryLock();
		if(fileLock != null) {
		    for(;;) {
			if(!unroll(in, manifest)) {
			    break;
			}
		    }
		}

		if(fileLock != null) {
		    fileLock.release();
		}

		fc.close();
	    } catch(IOException e) {
		e.printStackTrace();
	    }
	}
    }

    /**
     * Replay this log. 
     *
     * @throws KevaDBException is used to indicate any errors. 
     */
    public void replay() throws KevaDBException {
	List<Path> logs = getLogs();
	for (Path log: logs) {
	    replayLog(log);
	}
    }
}