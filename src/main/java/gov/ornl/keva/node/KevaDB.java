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
import java.util.Map;
import java.util.HashMap;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Comparator;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * SEDA libs.
 **/
import gov.ornl.seda.SEDAFuture;

/**
 * Configuration libs.
 **/
import gov.ornl.config.ConfigFactory;
import gov.ornl.config.Configuration;
import gov.ornl.config.ConfigEntry;

/**
 * Keva libs.
 **/
import gov.ornl.keva.sstable.SSTable;
import gov.ornl.keva.mem.MemTable;
import gov.ornl.keva.mem.MemTableAllocator;
import gov.ornl.keva.table.TableValueFactory;
import gov.ornl.keva.table.TableKey;
import gov.ornl.keva.table.TableValue;
import gov.ornl.keva.core.KevaDBException;
import gov.ornl.keva.core.VectorClock;
import gov.ornl.keva.core.PruneOptions;
import gov.ornl.keva.core.ReadOptions;
import gov.ornl.keva.core.WriteOptions;
import gov.ornl.keva.core.OpenOptions;
import gov.ornl.keva.core.StreamIterator;
import gov.ornl.keva.core.TreeUnionIterator;
import gov.ornl.keva.loader.JobLoader;

/**
 * KevaDB is the primary mechanism to interact with databases. 
 * Each database is represented and mediated by a KevaDB instance, and
 * has a simple API to interact with the underlying data. The data model
 * used by KevaDB is based on a history of values defined by vector clocks. 
 * This means that values may persist for a long time (even if users write to
 * the same key), and that values may fork (depending on the vector clock
 * assigned to the value). 
 * 
 * @author James Horey
 */
public class KevaDB {
    /**
     * Identify this database. 
     **/
    private String db;

    /**
     * Client ID clock to use for system operations (like delete). 
     **/
    private volatile int systemClock = 0;

    /**
     * The pruning options are used to filter results
     * while reading. It also serves as a way to remove
     * unnecessary items from the sstables. 
     **/
    private PruneOptions pruneOptions;

    /**
     *  Used to sort independent values.
     **/
    private Comparator<TableValue> comparator;

    /**
     * Memtable is where all the data actually resides. 
     **/
    private MemTable table;
    private MemTableAllocator memAllocator;
    private long memTableFlushSize;

    /**
     * Durable storage.
     **/
    private SSTableService diskService;
    private WriteAheadLog wal;

    /**
     * Configuration information.
     **/
    private String dataPath;
    private String logPath;
    private String configFile; 
    private ConfigFactory configFactory;

    /**
     * This is not a public constructor. To instantiate
     * KevaDB objects, use the factory. 
     **/
    protected KevaDB(String db, String configFile) {
	this.db = db;

	// These are where the configuration files live. 
	dataPath = null;
	logPath = null;

	// Set up default pruning options. 
	// This is overriden by the config options. 
	pruneOptions = new PruneOptions();

	// Dfault comparator sorts values by wall time. 
	comparator =
	    new Comparator<TableValue>() {
	    public int compare(TableValue v1, TableValue v2) {
		long d = 
		v1.getClock().getLocalTime() - 
		v2.getClock().getLocalTime();

		return (int)d;
	    }
	};

	// Set up the SSTable. 
	diskService = SSTableService.newInstance();

	this.configFile = configFile;
	configFactory = new ConfigFactory();
	loadConfig(configFile); // Load configuration

	// Set up the MemTable. 
	memAllocator = new MemTableAllocator();
	table =	memAllocator.newMemTable(memTableFlushSize,
					 comparator);
	table.setPruneOptions(pruneOptions);
    }

    /**
     * Start the database. Used by the factory to finish
     * initialization of the DB.
     */
    protected void start() {
	// Configure the write-ahead log.
	wal = new WriteAheadLog(this, configFile);
	wal.createLog();
    }

    /**
     * Load up all the configuration files.
     **/
    private void loadConfig(String c) {
	Configuration conf;
	ConfigEntry entry;

	if(c != null) {
	    Path p = Paths.get(c);
	    conf = configFactory.getConfig(p.toAbsolutePath().toString());
	    if(conf != null) {
		setStorage(conf); // Set the storage directories. 
	    }
	}
    }

    /**
     * Set the various storage directories. 
     **/
    @SuppressWarnings("unchecked")
    private void setStorage(Configuration conf) {
	ConfigEntry entry = null;

	entry = conf.get("keva.data.dir");
	if(entry != null) { // Try to use the values set. 
	    List<String> vv = entry.getEntry("value").getValues();
	    if(vv.size() > 0) {
	    	dataPath = vv.get(0).trim() +
		    System.getProperty("file.separator") + db;
	    }
	}

	// Set the WAL directory. 
	entry = conf.get("keva.wal.dir");
	if(entry != null) {
	    List<String> vv = entry.getEntry("value").getValues();
	    if(vv.size() > 0) {
		logPath = vv.get(0).trim() +
		    System.getProperty("file.separator") + db;
	    }
	}

	// The sstable implementation. 
	entry = conf.get("keva.sstable.impl");
	if(entry != null) {
	    List<String> vv = entry.getEntry("value").getValues();
	    for(String v : vv) {
		String[] s = v.split(":");
		if(s.length == 1) {
		    diskService.addDB(this, s[0], null);
		}
		else {
		    diskService.addDB(this, s[0], s[1]);
		}
	    }
	}

	// Set up the sorting. 
	entry = conf.get("keva.sort");
	if(entry != null) {
	    List<String> vv = entry.getEntry("value").getValues();
	    if(vv.size() == 1) {
		// Just the name of the class. Assume that we can find
		// the class using the system loader.
		String clazz = vv.get(0).trim();
		Object obj = JobLoader.load(clazz, null, null);
		if(obj != null && obj instanceof Comparator) {
		    comparator = (Comparator<TableValue>)obj;
		}
	    }		
	    else if(vv.size() == 2) {
		// Name of the comparator jar & class. 
		String jar = vv.get(0).trim();
		String clazz = vv.get(1).trim();
		Path p = Paths.get(jar);

		Object obj = JobLoader.load(clazz, p.toAbsolutePath().toString(), null);
		if(obj != null && obj instanceof Comparator) {
		    comparator = (Comparator<TableValue>)obj;
		}
	    }
	}

	// Should we prune the deleted items from the history? 
	entry = conf.get("keva.prune.delete");
	if(entry != null) {
	    List<String> vv = entry.getEntry("value").getValues();
	    if(vv.size() > 0) {
		pruneOptions.delete = Boolean.valueOf(vv.get(0).trim()).booleanValue();
	    }
	}

	// Should we prune older values? 
	entry = conf.get("keva.prune.history");
	if(entry != null) {
	    List<String> vv = entry.getEntry("value").getValues();
	    if(vv.size() > 0) {
		pruneOptions.newest = Integer.parseInt(vv.get(0).trim());
	    }
	}

	// how large before flushing memtables.
	memTableFlushSize = MemTable.RECOMMENDED_THRESHOLD;
	entry = conf.get("keva.memtable.threshold");
	if(entry != null) {
	    List<String> vv = entry.getEntry("value").getValues();
	    if(vv.size() > 0) {
		memTableFlushSize = Integer.parseInt(vv.get(0).trim());
	    }
	}
    }

    /**
     * Set the sstable implementation class. 
     *
     * @param className Name of the sstable class
     * @param jar Name of the jar file containing the implementation (optional)
     */
    public void setSSTableImplementation(String className, String jar) {
	diskService.addDB(this, className, jar);
    }

    /**
     * ID that uniquely identifies this database. 
     *
     * @return The string representation of the ID. 
     */
    public String getID() {
	return db;
    }

    /**
     * Define the path where the sstables are stored. This value is also
     * defined by the configuration parameter "keva.data.dir". 
     *
     * @param path The path where the data is stored. 
     */
    public void setDataPath(String path) {
	dataPath = path;
    }

    /**
     * Return the path where the sstables are stored. 
     * 
     * @return The path where the data is stored. 
     */
    public String getDataPath() {
	return dataPath;
    }

    /**
     * Define the log directory. The log directory is where all the WAL logs live.
     * 
     * @param path The log directory path. 
     */
    public void setLogPath(String path) {
	logPath = path;
    }

    /**
     * Return the log directory path. 
     * 
     * @return The log path in string representation. 
     */
    public String getLogPath() {
	return logPath;
    }

    /**
     * Get the SSTable service.
     **/
    protected SSTableService getDiskService() {
	return diskService;
    }

    /**
     * Create a new empty memtable. 
     **/
    protected void format() {
	diskService.format(this);

	if(wal != null) {
	    wal.clear();
	}
    }

    /**
     * Close a database. 
     */
    public void close() 
	throws KevaDBException {
	// Set the memtable to null so that nothing
	// else can write to it. 
	MemTable oldTable = table;
	table = null;

	// We need to wait for any existing writes to finish. 
	// Easiest way to do this is just try locking each key.
	for(Iterator<TableKey> keys = oldTable.getKeys();
	    keys.hasNext(); ) {
	    TableKey key = keys.next(); 
	    oldTable.lock(key);
	    oldTable.unlock(key);
	}

	// Flush the table to disk. 
	if(oldTable.getNumKeys() > 0) {
	    diskService.flush(this, oldTable);
	}

	// Probably a bug, since we don't know when the table is actually free!
	memAllocator.freeMemTable(oldTable); 

	// Now get rid of old entries in the WAL, including
	// any in-memory buffers. 
	wal.clear();
    }

    /**
     * Recover the database from logs. 
     */
    public void recover() 
	throws KevaDBException {
	// Disable the current WAL so that we don't
	// record the playback. 
	WriteAheadLog temp = wal;
	wal = null;

	// Replay the old WAL. 
	WriteAheadLog oldLog = new WriteAheadLog(this, configFile);
	oldLog.replay();

	// Now set up our wal again. 
	wal = temp;
    }

    /**
     * Force all the sstables in the first level to be merged. 
     */
    public void forceMerge() {
	SEDAFuture future = 
	    diskService.forceMerge(this);

	future.get();
    }

    /**
     * Flush the current memtable to disk and make an sstable. 
     * This normally happens when the memtable grows too large, but
     * the user can force the issue if necessary. 
     */
    public void flush() { 
	MemTable oldTable = table;

	// Do not flush an empty table. 
	if(oldTable.getNumKeys() > 0) {
	    // Replace with new memtable.
	    table = memAllocator.newMemTable(memTableFlushSize,
					     comparator);

	    // Wait for all the writers to be complete
	    // on the old table. 
	    oldTable.flush();

	    // Flush the table to disk. 
	    SEDAFuture future = 
		diskService.flush(this, oldTable);

	    // Wait for the job to complete, before freeing
	    // the old memtable. 
	    future.get();
	    memAllocator.freeMemTable(oldTable); 

	    // Now get rid of old entries in the WAL. 
	    if(wal != null) {
		diskService.lockForRead();
		wal.recycle(System.currentTimeMillis());
		diskService.unlockForRead();
	    }

	    // Finaly check if we need to merge any of the
	    // tables in any level.
	    diskService.mergeIfNecessary(this);
	}
    }

    /**
     * Commit a tentative value to memory. A tentative value
     * is a value that is already in the memtable, but is not
     * visible. Since the value isn't visible it won't be flushed
     * to an sstable until the value is commited. This is useful
     * when we need to atomically commit multiple values. 
     *
     * @param key The key of the value to commit.
     * @param value The value to commit. We actually just need the vector clock.
     * @param options Write options associated with this value. 
     */
    public void commit(final TableKey key, 
		       final TableValue value,
		       final WriteOptions options) {
	table.lock(key);
	if(options != null) {
	    table.commit(key, value, options.branch);
	}
	else {
	    table.commit(key, value, null);
	}
	table.unlock(key);
    }

    /**
     * Delete the value from the database. This does not actually
     * remove the value from the database, but simply marks it for deletion.
     * The user must define a pruning option to actually get rid of the value. 
     * 
     * @param client Unique ID representing the client. Normally the vector clock
     * associated with a value is sufficient to define the client, but since the
     * delete operation does not have a value parameter, we must supply another client ID. 
     * @param key The key of the value to delete. 
     */
    public void delete(final TableKey key) {
	// We must construct a new "delete" table value, and then
	// place this value along every single branch. In order
	// to implement this properly, we must first get the collapsed
	// values on every branch, and then perform a write on each
	// branch with the right vector clock. To do this atomically, we
	// must lock this specific key. 
	table.lock(key);

	// Get all the value histories. 
	Map<String,StreamIterator<TableValue>> memValues = 
	    table.getCollapsed(key);
	for(String branch : memValues.keySet()) {
	    // Create a new "delete" value. The delete value will
	    // also need a vector clock that is new enough. 
	    TableValue delete = TableValueFactory.newValue(TableValue.DELETE);
	    delete.setClock(new VectorClock("sys".getBytes(), systemClock++));

	    // Log the write into the WAL.
	    if(wal != null) {
	        wal.put(key, delete, null);
	    }

	    // Place the delete operatation into the memtable. 
	    table.put(key, delete, branch, false);
	}

	table.unlock(key);

	// Check if we need to flush. 
	if(table.shouldFlush()) { 
	    flush();
	}
    }

    /**
     * Place a new value into the database. This method assumes that the
     * client has defined the value vector clock already, and does not
     * have any specific writing options. 
     * 
     * @param key The key of the value. 
     * @param value The value to place into the database. 
     */
    public void put(final TableKey key, 
		    final TableValue value) {
	put(key, value, null);
    }

    /**
     * Place a new value into the database.
     * 
     * @param key The key of the value. 
     * @param value The value to place into the database. 
     * @param options Write options that define how the 
     * value is written to the memtable. 
     */
    public void put(final TableKey key, 
		    final TableValue value,
		    final WriteOptions options) {
	// Log the write into the WAL.
	if(wal != null) {
	    wal.put(key, value, options);
	}

	if(options != null) {
	    // Check if we need to insert into a specific branch. 
	    if(options.branch != null) {
		table.put(key, value, options.branch, options.tentative);
	    }
	    else {
		table.put(key, value, options.tentative);
	    }
	}
	else {
	    table.put(key, value, false);
	}

	// Check if we need to flush. 
	if(table.shouldFlush()) { 
	    flush();
	}
    }

    /**
     * Apply multiple writes atomically. 
     *
     * @param ops The batch write operations.
     */
    public boolean put(final WriteBatch ops) {
	// Lock all the keys associated with this batch. 
	// This makes sure that we do not insert other
	// items while inserting the batch. 
    	Iterator<TableKey> iter = ops.iterator();
    	while(iter.hasNext()) {
    	    TableKey key = iter.next();

	    // We might have to create the bucket before
	    // locking it (otherwise it causes a lock error).
	    table.create(key);
    	    table.lock(key);
    	}

	// Perform all the actual writes. The memtable has a special
	// "commit" method that is similar to "put" except that it doesn't
	// perform any special locking. 
	iter = ops.iterator();
	while(iter.hasNext()) {
	    TableKey key = iter.next();
	    for(WriteBatch.TableWrite write : ops.getValues(key)) {
		if(write.options == null) {
		    table.commit(key, write.value, null);
		}
		else {
		    table.commit(key, write.value, write.options.branch);
		}
	    }
	}

    	// We are all done so unlock all the keys. 
    	iter = ops.iterator();
    	while(iter.hasNext()) {
    	    TableKey key = iter.next();
    	    table.unlock(key);
    	}
    	return true;
    }

    /**
     * Help retrieve latest data. 
     */
    private NavigableMap<String, StreamIterator<TableValue>> getHelper(final TableKey key,
								       final ReadOptions options) { 
	Map<String,StreamIterator<TableValue>> memValues = null;
	List<Map<String,StreamIterator<TableValue>>> ssValues;

	// Check if there are any valid options. 
	if(options == null ||
	   (options.branch == null && options.time == -1)) {
	    // Get from all the sstables. 
	    ssValues = getFromSSTable(key, null, -1);

	    // Get all the independent values associated with this key. 
	    memValues = table.getCollapsed(key);
	}
	else {
	    if(options.branch != null) {
		// Get only the value associated with the branch.
		// There is only one value in this iterator. 
		ssValues = getFromSSTable(key, options, 0);
		memValues = table.getCollapsed(key, options.branch);
	    }
	    else {
		// Then find all the values associated with that wall time. 
		ssValues = getFromSSTable(key, null, options.time);
		memValues = table.getCollapsed(key, options.time);
	    }
	}

	// Collect all the iterators. 
	if(memValues != null) {
	    ssValues.add(memValues);
	}

	// Now merge the independent branches. 
	return SSTableService.collateBranches(ssValues, true, comparator);
    }

    /**
     * Get the latest independent values.
     * 
     * @param key The key identifying the value. 
     * @return An iterator over the latest independent values. 
     */
    public Map<String, StreamIterator<TableValue>> get(final TableKey key) { 
	return getHelper(key, null);
    }

    /**
     * Read the latest independent values while applying the read options. 
     * 
     * @param key The key identifying the value. 
     * @param options Read options that specify clock constraints, ordering, etc. 
     * @return An iterator over the latest independent values. 
     **/
    public StreamIterator<TableValue> get(final TableKey key,
					  final String branch) {
	Map<String,StreamIterator<TableValue>> values = null;

	// First try the memtable. If it is found here, then
	// we can stop searching since the memtable always has
	// the latest value. 
	values = table.getCollapsed(key, branch);
	if(values != null) {
	    return values.get(branch);
	}

	// Now search for the data in the sstables. However
	// we should search in level order. 
	values = getLatestByLevel(key, branch);
	if(values != null) {
	    return values.get(branch);
	}

	// Couldn't find it in the memtable or sstables. That
	// means it doesn't exist!
	return null;
    }

    /**
     * Read the latest independent values while applying the read options. 
     * 
     * @param key The key identifying the value. 
     * @param time The wall time 
     * @return An iterator over the latest independent values. 
     **/
    public Map<String, StreamIterator<TableValue>> get(final TableKey key,
						       final long time) {
	// Create a new read option.
	ReadOptions options = new ReadOptions();
	options.time = time;

	return getHelper(key, options);
    }

    /**
     * Read the values associated with the list of keys. If the
     * user supplies a read option, then we use those options to
     * synchronize the reads. Otherwise, we will use the latest values. 
     * 
     * @param keys List of keys identifying the values. 
     * @param options Read options that specify clock constraints, ordering, etc. 
     * @return A map associating the latest independent values for each key. 
     */
    public Map<TableKey, Map<String, StreamIterator<TableValue>>> get(final List<TableKey> keys,
								      final ReadOptions options) {

	Map<TableKey, Map<String, StreamIterator<TableValue>>> iters = 
	    new HashMap<>();

	// Apply the read option in the following manner:
	// 
	// (0) If there are no options, then just run over the latest
	//     values from the keys. 
	// (1) If the user has specified a branch, then we apply
	//     that branch to every key.
	// (2) If the user has specified a vector clock, then only
	//     apply that clock to the first key. Then use the
	//     wall time for the other keys. 
	if(options == null) {
	    for(TableKey k : keys) {
		iters.put(k, getHelper(k, null));
	    }
	}
	else if(options.branch != null) {
	    for(TableKey k : keys) {
		iters.put(k, getHelper(k, options));
	    }
	}
	else if(options.time != -1) {
	    for(TableKey k : keys) {
		iters.put(k, getHelper(k, options));
	    }
	}

	return iters;
    }

    /**
     * Help retrieve historical values
     *
     * @param key Table key
     * @param options Read options
     */
    private NavigableMap<String, StreamIterator<TableValue>> getHistoryHelper(final TableKey key,
									      final String branch) {
	List<Map<String,StreamIterator<TableValue>>> values =
	    new ArrayList<>();

	// Get the data from the memtable. 
	Map<String,StreamIterator<TableValue>> value = null;
	if(branch != null) {
	    value = table.getUncollapsed(key, branch);
	}
	else {
	    value = table.getAll(key);
	}

	if(value != null) {
	    values.add(value);
	}

	// Get the data from the sstables.
	diskService.lockForRead();
	Map<String, Integer> tables = 
	    diskService.getDataManifests(this, 0, SSTableService.MAX_LEVELS);
	for(String t : tables.keySet()) {
	    SSTable ss = diskService.getSSTable(this, t, tables.get(t));
	    if(ss != null) {
		value = null;
		if(ss.contains(key) && branch != null) {
		    value = ss.getUncollapsed(key, branch);
		}

		if(value != null) {
		    values.add(value);
		}
	    }
	}
	diskService.unlockForRead();

	// Now merge all the histories.
	return SSTableService.collateBranches(values, false, comparator);
    }

    /**
     * Get the history of the values associated with the key. The
     * option is used to control which specific values are returned. 
     * 
     * @param key The key identifying the value. 
     * @return An iterator over the history of values along the branch specified by the options. 
     **/
    public Map<String, StreamIterator<TableValue>> getHistory(final TableKey key) {
	return getHistoryHelper(key, null);
    }

    /**
     * Get the history of the values associated with the key. The
     * option is used to control which specific values are returned. 
     * 
     * @param key The key identifying the value. 
     * @param options Read options that specify a specific branch of values. 
     * @return An iterator over the history of values along the branch specified by the options. 
     **/
    public Iterator<TableValue> getHistory(final TableKey key,
					   final String branch) {
	NavigableMap<String,StreamIterator<TableValue>> histories = 
	    getHistoryHelper(key, branch);

	return histories.get(branch);
    }

    /**
     * Iterate over all the keys in sorted order. Be warned that this
     * is an expensive operation since we need to scan all the 
     **/
    public Iterator<TableKey> iterator() {
	List<Iterator<? extends TableKey>> keys = new ArrayList<>();

	// Get the keys from the memtable. 
	keys.add(table.getKeys());

	// Get the keys from the sstables.
	Map<String, Integer> tables = 
	    diskService.getDataManifests(this, 0, SSTableService.MAX_LEVELS);
	for(String t : tables.keySet()) {
	    SSTable ss = diskService.getSSTable(this, t, tables.get(t));
	    if(ss != null) {
		Iterator<TableKey> k = ss.getKeys();
		if(k != null) {
		    keys.add(k);
		}
	    }
	}

	// Specify how to compare table keys. 
	Comparator<TableKey> comp = 
	    new Comparator<TableKey>() {
	    public int compare(TableKey k1, TableKey k2) {
		return k1.compareTo(k2);
	    }
	};

	// Create a new merge iterator that will return all
	// the keys in sorted order. 
	// return new UnionIterator<TableKey>(keys, comp);
	return new TreeUnionIterator<TableKey>(keys, comp);
    }

    /**
     * Fetch a value from a specific branch from the sstables. Because we want
     * the latest value we can search in level order. So it is found in level0
     * then we don't need to look in level1, etc. That is because things age in 
     * level order. 
     */
    private Map<String,StreamIterator<TableValue>> getLatestByLevel(final TableKey key,
								    final String branch) {
	diskService.lockForRead();
	Map<String, Integer> tables = 
	    diskService.getDataManifests(this, 0, SSTableService.MAX_LEVELS);

	Map<Integer,List<String>> manifestByLevel = new TreeMap<>();
	for(String uuid : tables.keySet()) {
	    Integer level = tables.get(uuid);
	    List<String> sstables = manifestByLevel.get(level);
	    if(sstables == null) {
	        sstables = new ArrayList<>();
		manifestByLevel.put(level, sstables);
	    }
	    sstables.add(uuid);
	}

	List<Map<String,StreamIterator<TableValue>>> values = 
	    new ArrayList<>();
	for(Integer level : manifestByLevel.keySet()) {
	    values.clear();
	    for(String uuid : manifestByLevel.get(level)) {
		SSTable ss = diskService.getSSTable(this, uuid, level);
		if(ss != null && ss.contains(key)) {
		    Map<String, StreamIterator<TableValue>> value = 
			ss.getCollapsed(key, branch);
		    if(value != null && value.size() > 0) {
			values.add(value);
		    }
		}
	    }

	    // Now see if we can collate these results.
	    if(values.size() > 0) {
		diskService.unlockForRead();
		return SSTableService.collateBranches(values, false, comparator);
	    }
	}

	diskService.unlockForRead();
	return null;
    }

    /**
     * Fetch a value from the sstables. 
     **/
    private List<Map<String,StreamIterator<TableValue>>> getFromSSTable(final TableKey key,
									final ReadOptions options,
									final long time) {
	List<Map<String,StreamIterator<TableValue>>> bucket = 
	    new ArrayList<>();

	diskService.lockForRead();
	Map<String, Integer> tables = 
	    diskService.getDataManifests(this, 0, SSTableService.MAX_LEVELS);
	for(String t : tables.keySet()) {
	    SSTable ss = diskService.getSSTable(this, t, tables.get(t));
	    if(ss != null) {
		// First check if this sstable has this key. 
		// This might result in a false positive, but we
		// check for improper iterators as well.
		if(ss.contains(key)) {

		    Map<String,StreamIterator<TableValue>> ssValues = null;
		    if(options != null &&
		       options.branch != null) {
			ssValues = ss.getCollapsed(key, options.branch);
		    }
		    else if(options == null && time != -1) {
			ssValues = ss.getCollapsed(key, time);
		    }
		    else {
			ssValues = ss.getCollapsed(key);
		    }

		    if(ssValues != null) {
			bucket.add(ssValues);
		    }
		}
	    }
	}
	diskService.unlockForRead();
	return bucket;
    }
 }