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
 * Java libs. 
 **/
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.Comparator;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.channels.FileChannel;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Keva libs.
 **/
import gov.ornl.keva.sstable.SSTable;
import gov.ornl.keva.sstable.DefaultSSTable;
import gov.ornl.keva.mem.MemTable;
import gov.ornl.keva.table.TableKey;
import gov.ornl.keva.table.TableValue;
import gov.ornl.keva.core.MinimumIterator;
import gov.ornl.keva.core.StreamIterator;
import gov.ornl.keva.core.MergeSortedIterator;
import gov.ornl.keva.loader.JobLoader;

/**
 * SEDA libs.
 **/
import gov.ornl.seda.SEDAService;
import gov.ornl.seda.SEDAJob;
import gov.ornl.seda.SEDAFuture;

/**
 * SSTable service performs all the work of creating and maintaining sstables. 
 * This class is a singleton, which means that only one service will be
 * instantiated for multiple databases. 
 * 
 * @author James Horey
 */
public class SSTableService extends SEDAService {
    /**
     * Job function names. 
     **/
    public static final int FLUSH_JOB       = 0;
    public static final int FORCE_MERGE_JOB = 1;
    public static final int MERGE_JOB       = 2;

    /**
     * This class is a singleton. 
     **/ 
    private static SSTableService service;

    /**
     * Number of sstables in a level before we start merging. 
     **/
    public static int MAX_SSTABLES = 5;

    /**
     * Max number of sstable levels.
     **/
    public static int MAX_LEVELS = 6;

    /**
     * SSTable information. 
     **/
    private Map<String, String> sstableClasses =
	new HashMap<>(); 
    private Map<String, Path> sstableJars = 
	new HashMap<>(); 

    /**
     * Used to control access to the manifest file and for merging. 
     **/
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    /**
     * Manifest cache. 
     */
    private Map<String, Integer> manifestCache = null;

    /**
     * All block IDs have a fixed length. Makes it easier to parse.  
     */
    private final static int UUID_LENGTH;

    static { // Static initializers
	service = new SSTableService();

	String uuid = UUID.randomUUID().toString();
	UUID_LENGTH = uuid.length(); // Assume that all UUIDs are same length.
    }

    /**
     * Private constructur makes sure that clients cannot
     * directly instantiate the service. 
     **/
    private SSTableService() {
    }

    /**
     * Public facing initializer. 
     **/
    public static SSTableService newInstance() {
	return service;
    }

    /**
     * Add a new sstable implementation. 
     * @param db The database being added
     * @param tableClass Name of the sstable class used by this database
     * @param jar Name of the jar file containing the sstable implementation
     **/
    public void addDB(KevaDB db, String tableClass, String jar) {
	sstableClasses.put(db.getID(), tableClass);

	if(jar != null) {
	    sstableJars.put(db.getID(), Paths.get(jar).toAbsolutePath());
	}
    }

    /**
     * Where all the Level SSTables live. 
     **/
    private String getLevelPath(String path, int level) {
	return path + System.getProperty("file.separator") + String.format("level%d", level);
    }

    /**
     * Flush the data into the merge buffer. 
     **/
    protected void flushMergeBuffer(final ByteBuffer mergeBuffer,
				    final ByteBuffer data) {
	// Just add the data into the merge buffer. 
	mergeBuffer.put(data);
    }

    /**
     * Where the sstable data lives.
     **/
    private String getDataPath(String path, int level) throws IOException {
	String p = getLevelPath(path, level) + 
	    System.getProperty("file.separator") + "data";

	Files.createDirectories(Paths.get(p).toAbsolutePath());
	return p;
    }

    /**
     * Where the sstable metadata lives.
     **/
    private String getIndexPath(String path, int level) throws IOException {
	String p = getLevelPath(path, level) + 
	    System.getProperty("file.separator") + "index";

	Files.createDirectories(Paths.get(p).toAbsolutePath());
	return p;
    }

    /**
     * Where the sstable bloom filter lives.
     **/
    private String getFilterPath(String path, int level) throws IOException {
	String p = getLevelPath(path, level) + 
	    System.getProperty("file.separator") + "filter";

	Files.createDirectories(Paths.get(p).toAbsolutePath());
	return p;
    }

    /**
     * Where the manifest file lives.
     **/
    private String getManifestPath(String path, int level) throws IOException {
	Files.createDirectories(Paths.get(getLevelPath(path, level)).toAbsolutePath());
	return getLevelPath(path, level) + System.getProperty("file.separator") + "manifest";
    }

    /**
     * Remove this table from the manifest. 
     **/
    private void removeFromManifest(final String path,
				    final Map<String,SSTable> tables,
				    final int level) {
	/**
	 * Remove the sstable by:
	 * (1) Scanning all the old items while excluding the supplied sstables
	 * (2) Deleting the old manifest
	 * (3) Writing out a new manifest with the list
	 **/
	try {
	    // Where is the manifest located? 
	    String pathName = getManifestPath(path, level);

	    // Check if this file exists. 
	    Path p = Paths.get(pathName).toAbsolutePath();
	    if(Files.exists(p)) {
		List<String> manifestList = new ArrayList<>();
		    
		// Create a file channel and map the buffer for reading. 
		FileChannel chan = FileChannel.open(p, StandardOpenOption.READ);
		MappedByteBuffer in = 
		    chan.map(FileChannel.MapMode.READ_ONLY, 0, (int)chan.size());

		// Read in one SSTable UUID at a time. Assume that all the IDs are
		// the exact same length. 
		for(int i = 0; i < (int)chan.size(); i += UUID_LENGTH) {
		    byte[] buffer = new byte[UUID_LENGTH];
		    in.get(buffer, 0, UUID_LENGTH);
		    
		    String tableName = new String(buffer);
		    if(!tables.containsKey(tableName)) {
			// Add this sstable to our new manifest.
			manifestList.add(tableName);
		    }
		}
		chan.close();

		// Now delete the old manifest file. 
		Files.delete(p);
		Files.createFile(p);

		// Now write out the new manifest file. 
		chan = FileChannel.open(p,
					StandardOpenOption.CREATE,
					StandardOpenOption.APPEND);
		for(String m : manifestList) {
		    byte[] data = m.getBytes();
		    ByteBuffer buffer = ByteBuffer.allocate(data.length);
		    buffer.put(data, 0, UUID_LENGTH); // Transfer to the buffer.
		    buffer.flip(); // Flip the buffer so we can write. 
		    chan.write(buffer); // Write to the channel.
		}
		chan.close(); // Close the channel.
	    }
	} catch(Exception e) {
	    e.printStackTrace();
	}

	// Invalidate the manifest cache. 
	manifestCache = null;
    }

    /**
     * Record the new sstable ID so that we can easily
     * maintain a list of all current sstables. 
     **/
    private void updateDataManifest(final String path,
				    final SSTable table,
				    final int level) {
	try {
	    // Get the manifest path. 
	    String pathName = getManifestPath(path, level);

	    Path p = Paths.get(pathName).toAbsolutePath();
	    if(!Files.exists(p)) {
		// Manifest doesn't exist yet. Most likely we are merging 
		// manifests and creating a new upper-level manifest.
		Files.createFile(p);
	    }

	    // Create a file channel for appending. 
	    FileChannel chan = FileChannel.open(p, 
						StandardOpenOption.CREATE,
						StandardOpenOption.APPEND);
	    byte[] data = table.getUUID().getBytes();
	    ByteBuffer buffer = ByteBuffer.allocate(data.length);
	    buffer.put(data, 0, UUID_LENGTH); // Transfer to the buffer.
	    buffer.flip(); // Flip the buffer so we can write. 
	    chan.write(buffer); // Write to the channel.

	    chan.close(); // Close the channel.
	} catch(IOException e) { 
	    e.printStackTrace();
	}	

	// Invalidate the manifest cache. 
	manifestCache = null;
    }

    /**
     * Lock reading the sstables. There may be multiple concurrent
     * readers, but only a single writer. 
     */
    public void lockForRead() {
	readLock.lock();
    }
    /**
     * Unlock reading the sstables. There may be multiple concurrent
     * readers, but only a single writer. 
     */
    public void unlockForRead() {
	readLock.unlock();
    }
    /**
     * Lock writing the sstables. There may be multiple concurrent
     * readers, but only a single writer. 
     */
    public void lockForWrite() {
	writeLock.lock();
    }
    /**
     * Unlock writing the sstables. There may be multiple concurrent
     * readers, but only a single writer. 
     */
    public void unlockForWrite() {
	writeLock.unlock();
    }

    /**
     * Get the list of sstables.
     *
     * @param db The database we are searching 
     * @param startLevel Start looking at this level
     * @param maxLevel Stop looking at this level
     * @return Manifest mapping the name of the sstable block to the level at which the sstable is found.
     */
    public Map<String, Integer> getDataManifests(final KevaDB db,
						 final int startLevel, 
						 final int maxLevel) {
	// We have all the manifest cached, so use it. 
	if(manifestCache != null &&
	   startLevel == 0 &&
	   maxLevel == MAX_SSTABLES) {
	    return manifestCache;
	}

	// Otherwise we need to re-read the manifest. 
	Map<String, Integer> manifest = new HashMap<>();
	try {	   
	    for(int i = startLevel; i <= maxLevel; ++i) {
		// Get the manifest path. 
		String pathName = getManifestPath(db.getDataPath(), i);

		// Check if this file exists. 
		Path path = Paths.get(pathName).toAbsolutePath();
		if(Files.exists(path)) {
		    // Create a file channel and map the buffer for reading. 
		    FileChannel chan = FileChannel.open(path, StandardOpenOption.READ);
		    MappedByteBuffer in = 
			chan.map(FileChannel.MapMode.READ_ONLY, 0, (int)chan.size());

		    // Read in one SSTable UUID at a time. Assume that all the IDs are
		    // the exact same length. 
		    for(int j = 0; j < (int)chan.size(); j += UUID_LENGTH) {
			byte[] buffer = new byte[UUID_LENGTH];
			in.get(buffer, 0, UUID_LENGTH);
			manifest.put(new String(buffer), i);
		    }

		    // Clsoe the channel.
		    chan.close();
		}
	    }
	} catch(Exception e) {
	    e.printStackTrace();
	}

	// Should we cache these results? 
	if(startLevel == 0 && maxLevel == MAX_SSTABLES) {
	    manifestCache = manifest;
	}

	return manifest;
    }

    /**
     * Retrieve the SSTable associated with the path.
     * 
     * @param db The database we are searching
     * @param uuid The block ID of the sstable
     * @param level The level to search for the sstables
     * @return SSTable identified by the parameters
     */
    public SSTable getSSTable(final KevaDB db,
			      final String uuid,
			      final int level) {
	String dataPath = null;
	String indexPath = null;
	String filterPath = null;

	try {
	    // Get the data paths.
	    dataPath = getDataPath(db.getDataPath(), level);
	    indexPath = getIndexPath(db.getDataPath(), level); 
	    filterPath = getFilterPath(db.getDataPath(), level);
	} catch(IOException e) {
	    return null;
	}

	if(dataPath == null ||
	   indexPath == null ||
	   filterPath == null) {
	    return null;
	}

	// Create both the sstable & metadata table.
	SSTable table = instantiateTable(db, dataPath, indexPath, filterPath);
	table.setUUID(uuid);
	table.init(); 

	return table;
    }

    /**
     * Indicate whether any sstables that are older than or equal
     * to the the time supplied. This is useful when replaying the 
     * write-ahead log so that we don't replay commands already in
     * some sstables.
     *
     * @param db The database we are searching
     * @param time The modification time
     * @return Indicate whether any sstable matches the parameter
     */
    public boolean hasOlderSSTable(final KevaDB db,
				   final long time) {
	lockForRead();
	Map<String, Integer> manifest =
	    getDataManifests(db, 0, MAX_LEVELS);
	unlockForRead();

	for(String uuid : manifest.keySet()) {
	    SSTable table = getSSTable(db, uuid, manifest.get(uuid));
	    if(table.getModificationTime() <= time) {
		return true;
	    }
	}

	return false;
    }

    /**
     * Collate the histories. 
     */
    public static NavigableMap<String,StreamIterator<TableValue>> collateBranches(List<Map<String,StreamIterator<TableValue>>> values,
										  boolean minimize,
										  Comparator<TableValue> independentComparator) {
	NavigableMap<String, List<StreamIterator<TableValue>>> histories = 
	    new TreeMap<>();
	NavigableMap<String, StreamIterator<TableValue>> collated = 
	    new TreeMap<>();

	// We need to take all the related table histories (those that
	// belong to the same branch), and store them in a list. 
	for(Map<String,StreamIterator<TableValue>> fm : values) {
	    for(String branch : fm.keySet()) {
		List<StreamIterator<TableValue>> iters = histories.get(branch);

		if(iters == null) {
		    iters = new ArrayList<>();
		    histories.put(branch, iters);
		}

		iters.add(fm.get(branch));
	    }
	}

	for(String branch : histories.keySet()) {
	    List<StreamIterator<TableValue>> iters = histories.get(branch);

	    // Specify how values should be sorted. 
	    Comparator<TableValue> tvc = new Comparator<TableValue>() {
		public int compare(TableValue v1, TableValue v2) {
		    if(v1 == v2) {
			return 0;
		    }
		    else {
			return 
			(int)v1.getClock().getLocalTime() - 
			(int)v2.getClock().getLocalTime();
		    }
		}
	    };

	    if(minimize) {
		// We want to collapse these iterators. 
		MinimumIterator<TableValue> iter = new MinimumIterator<>(tvc);
		iter.addAll(iters);
		collated.put(branch, iter);
	    }
	    else {
		// We want to return all the values, but in
		// sorted order. 
		MergeSortedIterator<TableValue> iter = new MergeSortedIterator<>(tvc);
		iter.addAll(iters);
		collated.put(branch, iter);
	    }
	}

	return collated;
    }

    /**
     * This initiates an actual job request.
     **/
    @SuppressWarnings("unchecked")    
    protected void startJob(SEDAJob job) {
	if(job.getMethod() == FLUSH_JOB) {
	    flushHelper((KevaDB)job.getArgs().get(0),
			(MemTable)job.getArgs().get(1));
	    job.setStatus(SEDAJob.DONE); // All done with this job.
	}
	else if(job.getMethod() == FORCE_MERGE_JOB) {
	    forceMergeHelper((KevaDB)job.getArgs().get(0));
	    job.setStatus(SEDAJob.DONE); // All done with this job.
	}
	else if(job.getMethod() == MERGE_JOB) {
	    mergeIfNecessaryHelper((KevaDB)job.getArgs().get(0));
	    job.setStatus(SEDAJob.DONE); // All done with this job.
	}
    }

    /**
     * Flush the memtable to disk and make it into an sstable.
     *
     * @param db The database we are searching
     * @param mem The memtable to flush 
     **/
    public SEDAFuture flush(final KevaDB db, final MemTable mem) {
	List<Object> args = new ArrayList<Object>(2);
	args.add(db);
	args.add(mem);

	return addJob(new SEDAJob(FLUSH_JOB, args));
    }

    /**
     * Force merge the database. 
     *
     * @param db The database we are searching
     */
    public SEDAFuture forceMerge(final KevaDB db) {
	List<Object> args = new ArrayList<Object>(1);
	args.add(db);

	return addJob(new SEDAJob(FORCE_MERGE_JOB, args));
    }

    /**
     * Merge tables if the merge limitation is reached.
     *
     * @param db The database we are searching
     **/
    public SEDAFuture mergeIfNecessary(final KevaDB db) {
	List<Object> args = new ArrayList<Object>(1);
	args.add(db);

	return addJob(new SEDAJob(MERGE_JOB, args));
    }

    /**
     * Recursively delete everything. 
     **/
    private void deleteRecursive(String p) throws IOException {
	Files.walkFileTree(Paths.get(p).toAbsolutePath(),
			   new SimpleFileVisitor<Path>() {
				   @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) 
				       throws IOException {
				       Files.delete(file);
				       return FileVisitResult.CONTINUE;
				   }
				   @Override public FileVisitResult postVisitDirectory(Path dir, IOException exc) 
				       throws IOException {
				       if(exc == null) {
					   Files.delete(dir);
					   return FileVisitResult.CONTINUE;
				       }
				       throw exc;
				   }
			   } );
    }


    /**
     * Format the database by deleting all the logs and sstables.
     *
     * @param db The database to format
     **/
    public void format(KevaDB db) {
	try {
	    // Need to delete everything in these paths. 
	    if(db.getDataPath() != null) {
		deleteRecursive(db.getDataPath());
	    }

	} catch(IOException e) {
	    System.out.printf("sstable: could not delete directories\n");
	}
    }

    /**
     * Force a merge in the first level. 
     */
    private void forceMergeHelper(final KevaDB db) {
	lockForWrite();
	mergeHelper(db, 0, getDataManifests(db, 0, 0));
	unlockForWrite();
    }

    /**
     * Help with the merging process. 
     */
    private synchronized void mergeIfNecessaryHelper(final KevaDB db) {
	List<Map<String, Integer>> manifests = 
	    new ArrayList<>(MAX_LEVELS);

	// First collect all the manifests. tThat way we don't
	// block further sstable flushes while we are merging, but
	// still get consistent views into the manifest. 
	lockForWrite();
	for(int i = 0; i <= MAX_LEVELS; ++i) {
	    manifests.add(getDataManifests(db, i, i));
	}
	unlockForWrite();

	// Now merge all the manifests. 
	for(int i = 0; i <= MAX_LEVELS; ++i) {
	    Map<String, Integer> manifest = 
		manifests.get(i);

	    if(manifest.size() >= MAX_SSTABLES) {
	    	mergeHelper(db, i, manifest);
	    }	    
	}
    }

    /**
     * Merge the two sstables to the specified level. 
     **/
    private void mergeHelper(final KevaDB db,
			     final int level,
			     Map<String, Integer> tables) {
	// long start = System.currentTimeMillis();
	// System.out.printf("merging level %d ", level); 

	// Collect all the tables and organize for merging. 
	SSTable table1 = null;
	List<SSTable> otherTables = new ArrayList<>();
	Iterator<String> iter = tables.keySet().iterator();
	while(iter.hasNext()) {
	    SSTable t = getSSTable(db, iter.next(), level);
	    if(table1 == null) {
		table1 = t;
	    }
	    else {
		otherTables.add(t);
	    }
	}

	// Unlikely, but may happen if we try to schedule two
	// concurrent merges. We read the table names, then the
	// first merge finishes, and now the 2nd merge has null data. 
	if(table1 == null ||
	   otherTables == null) {
	    return;
	}

	String dataPath = null;
	String indexPath = null;
	String filterPath = null;
	try {
	    // Get the paths for the merged table. 
	    dataPath = getDataPath(db.getDataPath(), level + 1);
	    indexPath = getIndexPath(db.getDataPath(), level + 1); 
	    filterPath = getFilterPath(db.getDataPath(), level + 1);
	}
	catch(IOException e) {
	    e.printStackTrace();
	}

	if(dataPath == null ||
	   indexPath == null ||
	   filterPath == null) {
	    return;
	}

	// Perform the actual merge.
	SSTable mt = table1.merge(otherTables, dataPath, indexPath, filterPath);

	// Update the directory index so that we can find this table again. 
	writeLock.lock();
	updateDataManifest(db.getDataPath(), mt, level + 1);

	// Remove these items from the manifest. 
	Map<String, SSTable> toRemove = new HashMap<>(otherTables.size() + 1);
	toRemove.put(table1.getUUID(), table1);
	for(SSTable t : otherTables) {
	    toRemove.put(t.getUUID(), t);
	}
	removeFromManifest(db.getDataPath(), toRemove, level);
	writeLock.unlock();

	// Now get rid of the old sstables. 
	table1.delete();
	for(SSTable t : otherTables) {	    
	    t.delete();
	}

	// long end = System.currentTimeMillis();
	// System.out.printf(" (%.3f sec)\n", (double)(end - start) / 1000.00);
    }

    /**
     * Place the table into the index and write the table to disk. 
     * The table knows how to write to disk themselves, so the service
     * just needs to supply the directory. 
     **/
    private void flushHelper(final KevaDB db, 
			     final MemTable mem) {
	// Get the Level 0 paths.
	String dataPath = null;
	String indexPath = null;
	String filterPath = null;

	// long start = System.currentTimeMillis();
	// System.out.printf("flushing sstable "); 
	try {
	    dataPath = getDataPath(db.getDataPath(), 0);
	    indexPath = getIndexPath(db.getDataPath(), 0); 
	    filterPath = getFilterPath(db.getDataPath(), 0);
	} catch(IOException e) {
	    e.printStackTrace();
	}

	if(dataPath == null ||
	   indexPath == null ||
	   filterPath == null) {
	    return;
	}

	// Write out the data. This also flushes the bloom filter and data index. 
	SSTable table = instantiateTable(db, dataPath, indexPath, filterPath);
	table.setUUID(table.flush(mem));
	    
	// Update the directory index so that we can find this table again. 
	writeLock.lock();
	updateDataManifest(db.getDataPath(), table, 0);
	writeLock.unlock();
	// long end = System.currentTimeMillis();
	// System.out.printf(" (%.3f sec)\n", (double)(end - start) / 1000.00);
    }

    /**
     * Instantiate a new SSTable. 
     **/
    @SuppressWarnings("unchecked")    
    private SSTable instantiateTable(KevaDB db, 
				     String data, 
				     String index, 
				     String filter) {
	// Try to see if the user has specified a custom
	// sstable implementation for this DB. 
	String clazz = sstableClasses.get(db.getID());
	if(clazz != null) {
	    Path jar = sstableJars.get(db.getID());
	    String jarPath = null;
	    Object obj = null;

	    if(jar != null) {
		jarPath = jar.toAbsolutePath().toString();
	    }

	    // Construct the parameters. 
	    String[] args = new String[3];
	    args[0] = data;
	    args[1] = index;
	    args[2] = filter;

	    obj = JobLoader.load(clazz, jarPath, args);
	    if(obj != null && obj instanceof SSTable) {
		return (SSTable)obj;
	    }
	}

	// Otherwise, use the default sstable implementation.
	SSTable table = new DefaultSSTable(data, index, filter);
	return table;
    }
}