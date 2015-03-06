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

package gov.ornl.keva.sstable;

/**
 * Java libs. 
 **/
import java.util.Set;
import java.util.Map;
import java.util.NavigableMap;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Comparator;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.channels.FileChannel;

/**
 * Keva libs.
 **/
import gov.ornl.keva.node.SSTableService;
import gov.ornl.keva.mem.MemTable;
import gov.ornl.keva.table.TableValueFactory;
import gov.ornl.keva.table.TableAttributes;
import gov.ornl.keva.table.TableKey;
import gov.ornl.keva.table.TableValue;
import gov.ornl.keva.table.TableBucket;
import gov.ornl.keva.table.TableBucketFactory;
import gov.ornl.keva.core.BloomFilter;
import gov.ornl.keva.core.StreamIterator;
import gov.ornl.keva.core.TreeUnionIterator;

/**
 * A simple SSTable implementation that writes all data one record at
 * a time without using compression. 
 * 
 * @author James Horey
 */
public class DefaultSSTable extends SSTable {
    /**
     * Default bloom filter false positive rate.
     */
    private static final double FILTER_FP_RATE = 0.005;

    /**
     * Keep track of the running key and data sizes. 
     */
    private long dataSize;
    // private long keySize;

    /**
     * Actual classes that do the work. 
     */
    protected DefaultSSTableReader reader;
    protected DefaultSSTableMerger merger;
    protected DefaultSSTableFlusher flusher;
    protected DefaultSSTableDeleter deleter;

    /**
     * Cache the bloom filter. 
     */
    private BloomFilter filterCache = null;

    /**
     * @param dp Data path
     * @param id Index path
     * @param bp Bloom filter path
     */
    public DefaultSSTable(String dp, String id, String bp) {
	super(dp, id, bp);

	reader = new DefaultSSTableReader();
	merger = new DefaultSSTableMerger();
	flusher = new DefaultSSTableFlusher();
	deleter = new DefaultSSTableDeleter();
	dataSize = 0;
	// keySize = 0;
    }

    /**
     * Get the helper classes. 
     **/
    protected DefaultSSTableReader getReader() {
	return reader;
    }
    protected DefaultSSTableMerger getMerger() {
	return merger;
    }
    protected DefaultSSTableFlusher getFlusher() {
	return flusher;
    }

    /**
     * Initialize this sstable from disk.
     */
    @Override public void init() {
	String dPath = dataPath + 
	    System.getProperty("file.separator") + uuid;
	String iPath = dataPath + 
	    System.getProperty("file.separator") + uuid;

	// Read the data size.
	dataSize = reader.readSizeInfo(dPath);

	// // Red the key size. 
	// keySize = reader.readSizeInfo(iPath);
    }

    /**
     * Indicate how large the data portion of this sstable is.
     *
     * @return Memory used in bytes
     */
    @Override public long getDataSize() {
	return dataSize;
    }

    // /**
    //  * Get total amount of memory used by the keys (excluding data)
    //  * 
    //  * @return Memory used in bytes
    //  */
    // @Override public long getKeySize() {
    // 	return keySize;
    // }

    /**
     * Return the bloom filter. The bloom filter is used to efficiently
     * identify values that are stored in the table.
     *
     * @return Bloom filter
     */
    @Override public BloomFilter getFilter() {
	if(filterCache == null) {
	    filterCache = reader.readFilter();
	}

	return filterCache;
    }

    /**
     * Delete the sstable. This involves the following steps:
     * (1) Deleting the actual data file.
     * (2) Deleting the key index file.
     * (3) Deleting the bloom filter file. 
     */
    @Override public void delete() {
	try {
	    deleter.deleteData();
	    deleter.deleteIndex();
	    deleter.deleteBloomFilter();
	} catch(IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Determine if a value for the table key is stored on the sstable.
     *
     * @param key Table key
     * @return True if the element is found in the sstable. False otherwise.
     */
    @Override public boolean contains(final TableKey key) {
	return reader.tryFilterMembership(key);
    }

    /**
     * Merge this sstable with the other tables and form a single
     * table. This is useful to reduce disk I/O once tables get large.
     *
     * @param tables SStables to merge
     * @param dataPath Data path of new sstable
     * @param indexPath Index path of new sstable
     * @param filterPath Filter path of new sstable
     * @return New sstable
     */
    @Override public SSTable merge(final List<SSTable> tables,
				   final String dataPath,
				   final String indexPath,
				   final String filterPath) {
	DefaultSSTable newTable = new DefaultSSTable(dataPath, indexPath, filterPath);
	newTable.setUUID(SSTable.newBlockID());

       	// Get the merged keys.
	Iterator<TableKey> keyIter = merger.mergeKeys(tables);

	// Create a new bloom filter. 
	BloomFilter filter = 
	    new BloomFilter(FILTER_FP_RATE, 5 * getFilter().getExpected());

	// Create a new buffers map. 
	NavigableMap<Long,ByteBuffer> buffers = 
	    new TreeMap<>();

	// Merge the data and filter. 
	ExtendedTreeMap<TableKey, SSTable.FilePosition> index = null;
	do {
	    index = merger.mergeData(keyIter, tables, newTable, index, buffers);
	    merger.mergeIndex(index, newTable);
	    merger.mergeBloomFilter(index, newTable, filter);
	} while(!index.isCompleted());

	// Are there any buffers that we haven't flushed? 
	service.flushBuffer(buffers.firstEntry().getValue());

	// Return the newly merged table. 
	return newTable;
    }

    /**
     * Create a new SSTable from the MemTable.
     *
     * @return The UUID of the new sstable
     */
    @Override public String flush(final MemTable mem) { 
	if(uuid == null) { // Set the UUID. 
	    // uuid = UUID.randomUUID().toString();
	    uuid = SSTable.newBlockID();
	}

	// Now start flushing data to the buffer.
	ExtendedTreeMap<TableKey, SSTable.FilePosition> index = flusher.flushData(mem);
	Set<TableKey> keys = index.keySet();
	flusher.flushIndex(mem, index);
	flusher.flushBloomFilter(keys.iterator(), keys.size());

	return uuid;
    }

    /**
     * Get all the values associated with this key across all branches. 
     *
     * @param key The table key used to identify the value
     * @return An iterator over all the values associated with the key
     */
    @Override public Map<String,StreamIterator<TableValue>> getComplete(final TableKey key) {
	TableBucket bucket = reader.readBucket(key);
	if(bucket != null) {
	    return bucket.getComplete(null);
	}

	return null;
    }

    /**
     * Get all the historical table values along a single branch associated with
     * the supplied key. The branch is identified using the branch name. 
     *
     * @param key The table key used to identify the value
     * @param branch The branch to store the value
     * @return An iterator over all the historical values associated with the key along a specific branch. 
     */
    @Override public Map<String,StreamIterator<TableValue>> getUncollapsed(final TableKey key,
									   final String branch) {
	TableBucket bucket = reader.readBucket(key);
	if(bucket != null) {
	    return bucket.getUncollapsed(branch, null);
	}

	return null;
    }

    /**
     * Get all the latest, independent values associated with this key. 
     * We will need to go through the bucket to reconstruct the latest values. 
     * 
     * @param key The table key used to identify the value
     * @return An iterator over the final, independent values associated with the key
     */
    @Override public Map<String,StreamIterator<TableValue>> getCollapsed(final TableKey key) {
	TableBucket b = reader.readBucket(key);
	if(b != null) {
	    return b.getCollapsed();
	}

	return null;
    }

    /**
     * Get all the latest, independent values associated with this key. 
     * We will need to go through the bucket to reconstruct the latest values. 
     *
     * @param key The table key used to identify the value
     * @param time Prune all values with a wall time less than this time
     * @return An iterator over the final, independent values associated with the key
     */
    @Override public Map<String,StreamIterator<TableValue>> getCollapsed(final TableKey key,
									 final long time) {
	TableBucket b = reader.readBucket(key);
	if(b != null) {
	    return b.getCollapsed(time);
	}

	return null;
    }

    /**
     * Get all the latest, independent values associated with this key on
     * the specified branch. Since this is a collapsed value, we should
     * only return a single value. 
     * 
     * @param key The table key used to identify the value
     * @return An iterator over the final, independent values associated with the key
     */
    @Override public Map<String,StreamIterator<TableValue>> getCollapsed(final TableKey key,
									 final String branch) {
	TableBucket bucket = reader.readBucket(key);

	if(bucket != null) {
	    return bucket.getCollapsed(branch);
	}

	return null;
    }

    /**
     * Return the keys in sorted order. 
     *
     * @return Iterator over the table keys
     */
    @Override public Iterator<TableKey> getKeys() {
	return reader.readKeys();
    }

    /**
     * Get time of the last modification.
     *
     * @return Time in milliseconds
     */
    @Override public long getModificationTime() {
	return reader.getModificationTime();
    }

    /**
     * Handle the merge logic. 
     */
    class DefaultSSTableMerger {
	/**
	 * Max number of keys to store during the merge process. 
	 */
	private static final int MAX_INDEX_SIZE = 10000;

	/**
	 * Get the merged key iterator.
	 **/
	protected Iterator<TableKey> mergeKeys(final List<SSTable> tables) {
	    List<Iterator<? extends TableKey>> keys = new ArrayList<>();

	    // Specify how to compare table keys. 
	    Comparator<TableKey> comp = 
		new Comparator<TableKey>() {
		public int compare(TableKey k1, TableKey k2) {
		    return k1.compareTo(k2);
		}
	    };

	    // Read in all the keys of the other tables. 
	    for(SSTable t : tables) {
		Iterator<TableKey> iter = t.getKeys();
		if(iter != null) {
		    keys.add(iter);
		}
	    }

	    // Read in the keys of this table. 
	    Iterator<TableKey> iter = getKeys();
	    if(iter != null) {
		keys.add(iter);
	    }

	    // Create a new merge iterator that will return all
	    // the keys in sorted order. 
	    return new TreeUnionIterator<TableKey>(keys, comp);
	}

	/**
	 * Merge the data from two sstables. 
	 **/
	protected ExtendedTreeMap<TableKey, SSTable.FilePosition> mergeData(final Iterator<TableKey> keyIter,
									    final List<SSTable> tables,
									    final DefaultSSTable newTable,
									    ExtendedTreeMap<TableKey, SSTable.FilePosition> index,
									    NavigableMap<Long,ByteBuffer> buffers) {
	    // Create a unique file name. 
	    String path = newTable.getDataPath() + 
		System.getProperty("file.separator") + 
		newTable.getUUID();

	    // Get a mapped byte buffer. 
	    int keySize = 0;
	    long dataSize = getDataSize();
	    for(SSTable t : tables) {
		dataSize += t.getDataSize();
	    }
	    dataSize += Long.SIZE / 8;

	    // Store the locations of the data.
	    if(index == null) {
		index = new ExtendedTreeMap<>(new Comparator<TableKey>() {
			public int compare(TableKey k1, TableKey k2) {
			    return k1.compareTo(k2);
			}}
		    );
	    }
	    else {
		// Clear the index so that we don't repeat the
		// same keys over and over.
		index.clear();

		// Modify the data size so that we just calculate
		// the remaining size. 
		dataSize -= index.getKeyFilePosition();
	    }

	    ByteBuffer buffer = null;
	    if(buffers.size() == 0) {
		// Allocate a write buffer.
		buffer = service.getWriteBuffer(path, dataSize);
		buffers.put(0L, buffer);

		// Since this is a new buffer, store the data size. 
		buffer.putLong(dataSize);
		dataSize -= (Long.SIZE / 8);
	    }
	    else {
		Map.Entry<Long, ByteBuffer> entry = buffers.firstEntry();
		long offset = entry.getKey();
		buffer = entry.getValue();
		offset += buffer.position();

		buffer = service.renewWriteBuffer(buffer, offset, dataSize);
		buffers.remove(entry.getKey());
		buffers.put(offset, buffer);
	    }

	    // Assume that the index will be completed 
	    // unless we are proven otherwise. 
	    index.setCompleted(true);

	    // Keep track of where we are while reading from the sstables.
	    // This lets us skip over values that we've already read. 
	    Map<SSTable, Integer> fileOffsets = index.getFileOffsets();

	    // Read in all the bloom filters. This will let us check if
	    // a particular sstable has a key without doing too much I/O. 
	    Map<SSTable, BloomFilter> filters = 
		new HashMap<>(tables.size() + 1);
	    filters.put(DefaultSSTable.this, getFilter());
	    for(SSTable t : tables) {
		filters.put(t, t.getFilter());
	    }

	    // Store list of all values we must collate. 
	    List<Map<String,StreamIterator<TableValue>>> allValues =
		new ArrayList<>();

	    // For each key, get all the uncollapsed values, 
	    // and place them into a new sstable. 
	    long fp = index.getKeyFilePosition();
	    while(keyIter.hasNext()) {
		TableKey key = keyIter.next();

		// Keep track of the memory used by all the keys.
		// This will be used to allocate a buffer when we
		// write the index out to disk. 
		keySize += key.size();
		    
		// Keep track of the initial bucket. That way if there is
		// only one table that supplies values for this key, we can
		// avoid de-serializing the bucket (since we won't need to
		// perform a merge operation). 
		ByteBuffer initialBuffer = null;
		allValues.clear();

		// First check if the data is even in the filter. 
		// This should hopefully save us a lot unnecessary I/O. 
		if(filters.get(DefaultSSTable.this).contains(key.getValue())) {
		    Integer offset = fileOffsets.get(DefaultSSTable.this);
		    if(offset == null) {
			offset = 0;
			fileOffsets.put(DefaultSSTable.this, offset);
		    }
		    long[] pos = reader.readIndexSorted(key, offset);
		    if(pos != null) {
			fileOffsets.put(DefaultSSTable.this, (int)pos[2]);
			ByteBuffer buf = reader.readData(uuid, pos[0], pos[1]);
			if(initialBuffer == null) {
			    initialBuffer = buf;
			}
			else {
			    TableBucket bucket = reader.readBucketRaw(buf);
			    allValues.add(bucket.getComplete(null));
			}
		    }
		}

		// Get the values associated with the key from other tables. 
		for(SSTable t : tables) {
		    DefaultSSTable table = (DefaultSSTable)t;
		    if(filters.get(t).contains(key.getValue())) {
			Integer offset = fileOffsets.get(t);
			if(offset == null) {
			    offset = 0; // This offset is for reading keys from the manifest. 
			    fileOffsets.put(t, offset);
			}
			long[] pos = table.getReader().readIndexSorted(key, offset);
			if(pos != null) {
			    fileOffsets.put(t, (int)pos[2]);
			    ByteBuffer buf = table.getReader().readData(table.getUUID(), pos[0], pos[1]);
			    if(initialBuffer == null) {
			        initialBuffer = buf;
			    }
			    else {
				TableBucket bucket = table.getReader().readBucketRaw(buf);
				allValues.add(bucket.getComplete(null));
			    }
			}
		    }
		}

		int valueSize;
		if(allValues.size() == 0) {
		    // There is only one bucket for this key, so no need
		    // to de-serialize & re-serialize. We can just dump
		    // the raw byte buffer into the new file. 
		    valueSize = flusher.flushBucketRaw(initialBuffer, buffers, dataSize);
		}
		else {
		    // Place the initial buffer back into the set of values. 
		    TableBucket bucket = reader.readBucketRaw(initialBuffer);
		    allValues.add(bucket.getComplete(null));

		    // Merge all the values from the different tables. 
		    Map<String,StreamIterator<TableValue>> histories = 
			SSTableService.collateBranches(allValues, false, null);

		    // Write out these new values. 
		    valueSize = flusher.flushBucket(histories, buffers, dataSize);
		}

		// Instead of explicitly storing keys in this file, we store
		// it in a separate key index. So we need to know exactly where
		// in the buffer a particular key starts. 
		index.put(key, new SSTable.FilePosition(fp, valueSize));
		fp += valueSize;
		dataSize -= valueSize;

		// Check the index size. If it is too large, we should
		// return early since we don't want to run out of heap space.
		if(index.size() > MAX_INDEX_SIZE) {
		    index.setCompleted(false);
		    break;
		}
	    }
	    index.setKeySize(keySize);
	    index.setKeyFilePosition(fp);

	    return index;
	}

	/**
	 * Merge two bloom filters.
	 **/
	protected void mergeBloomFilter(final ExtendedTreeMap<TableKey,SSTable.FilePosition> index,
					final DefaultSSTable newTable,
					final BloomFilter filter) {
	    // Create a unique file name. 
	    String path = newTable.getFilterPath() + 
		System.getProperty("file.separator") + 
		newTable.getUUID();

	    Iterator<TableKey> keyIter = index.keySet().iterator();
	    while(keyIter.hasNext()) {
		TableKey k = keyIter.next();
		filter.add(k.getValue());
	    }

	    // Write out the new filter only after we are completely
	    // finished iterating over the keys. 
	    if(index.isCompleted()) {
		newTable.getFlusher().writeBloomFilterHelper(filter, path);
	    }
	}

	/**
	 * Merge the two indexes.
	 **/
	protected void mergeIndex(final ExtendedTreeMap<TableKey, SSTable.FilePosition> index,
				  final DefaultSSTable newTable) {
	    // Create a unique file name. 
	    String path = newTable.getIndexPath() + 
		System.getProperty("file.separator") + 
		newTable.getUUID();

	    long keySize = 
		index.getKeySize() + 
		index.size() * SSTable.FilePosition.SIZE;
	    
	    // Write the actual output. 
	    long offset = 
		flusher.writeIndexHelper(index, keySize, index.getOffset(), path);

	    // Record the new offset. 
	    index.setOffset(offset);
	}
    }

    /**
     * Handle the delete logic. 
     */
    class DefaultSSTableDeleter {
	/**
	 * Delete the sstable data. 
	 **/
	protected void deleteData() throws IOException {
	    String pathName = dataPath + System.getProperty("file.separator") + uuid;
	    Path path = Paths.get(pathName).toAbsolutePath();
	    if(Files.exists(path)) {
		Files.delete(path);
	    }
	}

	/**
	 * Delete the sstable index. 
	 **/
	protected void deleteIndex() throws IOException {
	    String pathName = indexPath + System.getProperty("file.separator") + uuid;
	    Path path = Paths.get(pathName).toAbsolutePath();
	    if(Files.exists(path)) {
		Files.delete(path);
	    }
	}

	/**
	 * Delete the sstable filter. 
	 **/
	protected void deleteBloomFilter() throws IOException {
	    String pathName = bloomPath + System.getProperty("file.separator") + uuid;
	    Path path = Paths.get(pathName).toAbsolutePath();
	    if(Files.exists(path)) {
		Files.delete(path);
	    }
	}
    }

    /**
     * Handle the flush logic. 
     **/
    class DefaultSSTableFlusher {
	/**
	 * Write out the table values into the byte buffer.
	 **/
	protected int flushBucket(Map<String,StreamIterator<TableValue>> histories,
				  NavigableMap<Long, ByteBuffer> buffers,
				  long dataRemaining) {
	    Map.Entry<Long, ByteBuffer> entry = buffers.firstEntry();
	    ByteBuffer buffer = entry.getValue();
	    long offset = entry.getKey();
	    int valueSize = 0;

	    // Serialize each branch.
	    for(String branch : histories.keySet()) {
		StreamIterator<TableValue> iter = histories.get(branch);
		byte[] branchBuf = branch.getBytes();
		int branchSize = ( 2 * (Integer.SIZE / 8) ) + branchBuf.length;

		// Not enough space to complete the branch serialization.
		// Allocate a new buffer with enough space. 
		if(buffer.remaining() < branchSize) {
		    offset += buffer.position();
		    buffer = service.renewWriteBuffer(buffer, offset, dataRemaining);
		}

		// Serialize the branch information. 
		buffer.putInt(branchBuf.length);
		buffer.put(branchBuf);
		buffer.putInt(iter.size());
		valueSize += branchSize;
		dataRemaining -= branchSize;

		// NOw serialize all the data values. 
		while(iter.hasNext()) {
		    TableValue v = iter.next();
		    byte[] hBuffer = v.getBytes();
		    int dataSize = ( (Integer.SIZE / 8) ) + hBuffer.length;

		    // Not enough space to complete the data serialization. 
		    // Allocate a new buffer with enough space. 
		    if(buffer.remaining() < dataSize) {
			offset += buffer.position();
		    	buffer = service.renewWriteBuffer(buffer, offset, dataRemaining);
		    }

		    // Place into the buffer.
		    buffer.putInt(hBuffer.length);
		    buffer.put(hBuffer);

		    // Keep track of how many bytes we've written. 
		    valueSize += dataSize;
		    dataRemaining -= dataSize;
		}
	    }

	    // Now update the buffer mapping. 
	    buffers.remove(entry.getKey());
	    buffers.put(offset, buffer);

	    return valueSize;
	}

	/**
	 * Flush the bucket data into the buffer. 
	 */
	protected int flushBucketRaw(ByteBuffer data,
				     NavigableMap<Long,ByteBuffer> buffers,
				     long dataRemaining) {
	    Map.Entry<Long,ByteBuffer> entry = buffers.firstEntry();
	    ByteBuffer buffer = entry.getValue();
	    long offset = entry.getKey();

	    // Need to allocate a new buffer to store the data. 
	    if(buffer.remaining() < data.capacity()) {
		offset += buffer.position();
	    	buffer = service.renewWriteBuffer(buffer, offset, dataRemaining);
	    }

	    // Write the data. 
	    buffer.put(data);

	    // Update the buffers map with the remaining size. 
	    buffers.remove(entry.getKey());
	    buffers.put(offset, buffer);

	    // We wrote the entire data set, so just
	    // return the capacity of the data. 
	    return data.capacity();
	}

	/**
	 * Write out the actual data. 
	 **/
	protected ExtendedTreeMap<TableKey, SSTable.FilePosition> flushData(final MemTable mem) {
	    // Create a unique file name. 
	    String path = dataPath + System.getProperty("file.separator") + uuid;

	    // We need to calculate the size of the data output.
	    // This is a bit tricky to do since the size of a table
	    // value can't really be determined until we serialize the
	    // value (since the value uses message pack). So we just
	    // run two passes to calculate the value.
	    long keySize = 0;
	    long dataSize = 0;
	    for(Iterator<TableKey> keyIter = mem.getKeys();
		keyIter.hasNext(); ) {
		TableKey k = keyIter.next();
		keySize += k.size();

		// Calculate the data size. 
		Map<String,StreamIterator<TableValue>> values = mem.getAll(k);
		for(String branch : values.keySet()) {
		    byte[] branchBuf = branch.getBytes();
		    dataSize += ( 2 * (Integer.SIZE / 8) ) + branchBuf.length;

		    for(Iterator<TableValue> iter = values.get(branch);
			iter.hasNext(); ) {
			TableValue h = iter.next();
			byte[] hBuffer = h.getBytes();

			dataSize +=
			    (Integer.SIZE / 8) + hBuffer.length;
		    }
		}
	    }

	    // Now allocate a buffer to store the sstable data.
	    ByteBuffer buffer = 
		service.getWriteBuffer(path, dataSize + (Long.SIZE / 8));
	    NavigableMap<Long,ByteBuffer> buffers = 
		new TreeMap<>();
	    buffers.put(0L, buffer);

	    // Store the locations of the data.
	    ExtendedTreeMap<TableKey, SSTable.FilePosition> index = 
		new ExtendedTreeMap<>(new Comparator<TableKey>() {
		    public int compare(TableKey k1, TableKey k2) {
			return k1.compareTo(k2);
		    }}
		);

	    // Place the data size so we know how much data we've
	    // written. Useful when calculating sstable size. 
	    buffer.putLong(dataSize);
	    long fp = Long.SIZE / 8; 
	    for(Iterator<TableKey> iter = mem.getKeys();
		iter.hasNext(); ) {
		TableKey k = iter.next();
		
		// We want to iterate over all the uncollapsed histories.
		int valueSize = flushBucket(mem.getAll(k), buffers, dataSize);

		// Instead of explicitly storing keys in this file, we store
		// it in a separate key index. So we need to know exactly where
		// in the buffer a particular key starts. 
		index.put(k, new SSTable.FilePosition(fp, valueSize));
		fp += valueSize;
		dataSize -= valueSize;
	    }
	    index.setKeySize(keySize);
	    index.setKeyFilePosition(fp);
	    
	    // Now try closing the mapped buffer. 
	    service.flushBuffer(buffers.firstEntry().getValue());
	    return index;
	}

	/**
	 * Write out the data index. 
	 **/
	protected void flushIndex(final MemTable mem,
				  final ExtendedTreeMap<TableKey, SSTable.FilePosition> index) { 
	    // Create a unique file name. 
	    String path = indexPath + System.getProperty("file.separator") + uuid;

	    // Calculate the approximate buffer size.
	    long keySize = 
		index.getKeySize() + 
		index.size() * SSTable.FilePosition.SIZE;

	    // Write the actual output. 
	    writeIndexHelper(index, keySize, 0, path);
	}

	/**
	 * Helper method to write out the actual contents of the index. 
	 **/	
	protected long writeIndexHelper(final NavigableMap<TableKey, SSTable.FilePosition> index,
					final long size,
					long offset, 
					final String path) {
	    Iterator<TableKey> iter = index.keySet().iterator();
	    ByteBuffer buffer = service.allocateBuffer(path, offset, size);
	    long dataRemaining = size;

	    while(iter.hasNext()) {
		TableKey k = iter.next();

	        byte[] keyData = k.serialize();
	        SSTable.FilePosition pos = index.get(k);

		int dataSize = (Integer.SIZE / 8) + 2 * (Long.SIZE / 8) + keyData.length;
		if(dataSize > buffer.remaining()) {
		    // We've run out of space in this buffer, so we will need to
		    // renew the buffer. 
		    offset += buffer.position();
		    buffer = service.renewWriteBuffer(buffer, offset, dataRemaining);
		}

		// Place al the numbers first and then the
		// data in the (probably misplaced) hope that
		// it might help with compression. 
		buffer.putLong(pos.getOffset());
		buffer.putLong(pos.getSize());
		buffer.putInt(keyData.length);
		buffer.put(keyData, 0, keyData.length);
		dataRemaining -= dataSize;
	    }

	    // Now try closing the mapped buffer. 
	    offset += buffer.position();
	    service.flushBuffer(buffer);

	    // Return our current file position.
	    return offset;
	}

	/**
	 * Helper write out the bloom filter.
	 **/
	protected void writeBloomFilterHelper(final BloomFilter filter, final String path) {
	    // Serialize the filter and place into file. 
	    ByteBuffer buffer = service.getWriteBuffer(path, filter.memory());
	    filter.serialize(buffer);

	    // Now try closing the mapped buffer. 
	    service.flushBuffer(buffer);
	}

	/**
	 * Write out the bloom filter. 
	 **/
	protected void flushBloomFilter(final Iterator<TableKey> keys, int size) {
	    // Create a unique file name. 
	    String path = bloomPath + System.getProperty("file.separator") + uuid;

	    // Create a new bloom filter and populate. 
	    BloomFilter filter = new BloomFilter(FILTER_FP_RATE, size);
	    while(keys.hasNext()) {
		TableKey k = keys.next();
		filter.add(k.getValue());
	    }

	    // Write out the bloom filter to disk.
	    writeBloomFilterHelper(filter, path);
	}
    }

    /**
     * Handle the read logic. 
     */
    class DefaultSSTableReader {
	private BloomFilter filter = null;

	/**
	 * Get time of the last modification.
	 **/
	public long getModificationTime() {
	    // Just use the file modification time. 
	    String path = dataPath + System.getProperty("file.separator") + uuid;

	    try {
		BasicFileAttributes attr
		    = Files.getFileAttributeView(Paths.get(path).toAbsolutePath(), 
						 BasicFileAttributeView.class)
		    .readAttributes();

		return attr.lastModifiedTime().toMillis();
	    } catch(IOException e) {
		e.printStackTrace();
	    }

	    return 0;
	}

	/**
	 * Read the keys from an sstable. For now store all the keys
	 * in memory, instead of reading one key at a time. This might bite
	 * us if there are too many keys. 
	 **/
	protected Iterator<TableKey> readKeys() {
	    String index = indexPath + 
		System.getProperty("file.separator") + uuid;
	    return new KeyIterator(Paths.get(index).toAbsolutePath());
	}

	/**
	 * Read the file position and size from the index file
	 * for a given key. 
	 **/
	protected long[] readIndex(final TableKey key) {
	    String index = indexPath + 
		System.getProperty("file.separator") + uuid;

	    try {
		// Open the index file for reading. 
		FileChannel fc = FileChannel.open(Paths.get(index).toAbsolutePath(), 
						  StandardOpenOption.READ);

		// Used to store the size & file handle info. 
		int size = (Integer.SIZE / 8) + 2 * (Long.SIZE / 8);
		ByteBuffer handle = ByteBuffer.allocate(size);
		while(fc.position() < fc.size()) {
		    fc.read(handle);
		    
		    handle.flip();
		    long offset = handle.getLong();
		    long dataLength = handle.getLong();
		    int keyLength = handle.getInt();
		    handle.flip();
		    
		    // Allocate a buffer for the key.
		    ByteBuffer keyBuf = ByteBuffer.allocate(keyLength);
		    fc.read(keyBuf); // Read in the key data.
		    keyBuf.flip(); // Go back to the start of the buffer. 

		    // Instantiate a new key. 
		    TableKey newKey = TableKey.fromBytes(keyBuf);
		    if(key.equals(newKey)) {
			// Now that we have a match, we should return
			// the offset and size. 
			long[] position = {offset, dataLength, fc.position()};

			// Don't need the file channel anymore. 
			fc.close();

			return position;
		    }
		}

		fc.close();
	    } catch(IOException e) {
		e.printStackTrace();
	    }

	    // We couldn't find this key in the index file. 
	    return null;
	}

	/**
	 * Read the file position and size from the index file
	 * for a given key. However, this method assumes that
	 * (1) We can safely skip over some bytes
	 * (2) That the keys are inputted in sorted order.
	 * That means if it doesn't find the key, it will give up. 
	 */
	protected long[] readIndexSorted(final TableKey key,
					 final int fileOffset) {
	    String index = indexPath + 
		System.getProperty("file.separator") + uuid;

	    try {
		FileChannel fc = FileChannel.open(Paths.get(index).toAbsolutePath(), 
						  StandardOpenOption.READ);
		// Skip over the key size and the supplied offset. 
		fc.position(fileOffset);

		// Used to store the size & file handle info. 
		int size = (Integer.SIZE / 8) + 2 * (Long.SIZE / 8);
		ByteBuffer handle = ByteBuffer.allocate(size);
		if(fc.position() < fc.size()) {
		    fc.read(handle);
		    
		    handle.flip();
		    long offset = handle.getLong();
		    long dataLength = handle.getLong();
		    int keyLength = handle.getInt();
		    handle.flip();
		    
		    // Allocate a buffer for the key.
		    ByteBuffer keyBuf = ByteBuffer.allocate(keyLength);
		    fc.read(keyBuf); // Read in the key data.
		    keyBuf.flip(); // Go back to the start of the buffer. 

		    TableKey newKey = TableKey.fromBytes(keyBuf);
		    if(key.equals(newKey)) {
			// Now that we have a match, we should return
			// the offset and size. 
			long[] position = {offset, dataLength, fc.position()};

			// Don't need the file channel anymore. 
			fc.close();

			return position;
		    }
		}

		fc.close();
	    } catch(IOException e) {
		e.printStackTrace();
	    }

	    // We couldn't find this key in the index file. 
	    return null;
	}

	/**
	 * Helper method to read the stored data size. 
	 **/
	protected long readSizeInfo(final String path) {
	    long size = 0;
	    try {
		FileChannel fc = FileChannel.open(Paths.get(path).toAbsolutePath(), 
						  StandardOpenOption.READ);

		// Used to store the size & file handle info. 
		ByteBuffer handle = ByteBuffer.allocate( (Long.SIZE / 8) );
		handle.mark();
		fc.read(handle);

		handle.reset();
		size = handle.getLong();

		fc.close();
	    } catch(IOException e) {
		e.printStackTrace();
	    }

	    return size;
	}

	/**
	 * Helper method to read the raw data from the sstable data file. 
	 **/
	protected ByteBuffer readData(final String uuid,
				      final long offset,
				      final long size) {
	    // Get the path to the file. 
	    String path = dataPath + System.getProperty("file.separator") + uuid;

	    try {
	        FileChannel fc = FileChannel.open(Paths.get(path).toAbsolutePath(), 
	    				      StandardOpenOption.READ);

	        ByteBuffer buf = ByteBuffer.allocate((int)size);
	        fc.read(buf, offset); // Fill in the contents.
		buf.flip();

		fc.close();
	        return buf;

	    } catch(IOException e) {
	        e.printStackTrace();
	    }

	    return null;
	}

	/**
	 * Instantiate a table bucket from the byte buffer.
	 */
	protected synchronized TableBucket readBucketRaw(final ByteBuffer data) {
	    // We should have an official bucket for
	    // this sstable, but just create a temporary one for now.
	    TableBucket bucket = 
		TableBucketFactory.newBucket(TableBucket.Constraint.UNSAFE, null);

	    int readData = 0;
	    int length = data.capacity();

	    int branchSize = 0;
	    int numValues = 0;
	    int bufferSize = 0;
	    do {
		// Read in the branch information.
		branchSize = data.getInt();
		byte[] branchBuffer = new byte[branchSize];
		data.get(branchBuffer);
		numValues = data.getInt();

		String branch = new String(branchBuffer);
		readData += ( 2 * (Integer.SIZE / 8) + branchSize );

		// Read in the individual values in the branch. 
		long latestTime = 0;
		for(int i = 0; i < numValues; ++i) {
		    bufferSize = data.getInt();
		    byte[] buffer = new byte[bufferSize];

		    data.get(buffer, 0, bufferSize);
		    readData += ( (Integer.SIZE / 8) + bufferSize );

		    // Instantiate a new value. 
		    TableValue value = TableValueFactory.fromBytes(buffer, bufferSize);

		    // Check if we should adjust the local time. 
		    if(value.getClock().getLocalTime() <= latestTime) {
			value.getClock().setLocalTime(++latestTime);
		    }
		    else {
			latestTime = value.getClock().getLocalTime();
		    }

		    bucket.add(value, branch);
		}
	    } while(readData < length);
	    return bucket;
	}

	/**
	 * Read all the data associated with the key and return the
	 * reconstructed bucket. 
	 **/
	protected TableBucket readBucket(final TableKey key) {
	    // Read the index information. 
	    long[] index = readIndex(key);
	    if(index == null) {
		// This key does not exist in this sstable.
		return null;
	    }
	    
	    // Given the index information, read the actual data. 
	    ByteBuffer data = readData(uuid, index[0], index[1]);
	    return readBucketRaw(data);
	}

	/**
	 * Read the bloom filter from disk. 
	 **/ 
	protected BloomFilter readFilter() {
	    if(filter == null) {
		String path = bloomPath + System.getProperty("file.separator") + uuid;
		ByteBuffer buffer = null;

		try {
		    // Open up a channel and read in the serialized data.
		    FileChannel fc = FileChannel.open(Paths.get(path), 
						      StandardOpenOption.READ);
		    buffer = ByteBuffer.allocate((int)fc.size());
		    fc.read(buffer);
		    buffer.flip();

		    // Close the channel.
		    fc.close();
		} catch(IOException e) {
		    e.printStackTrace();
		}
		// Instantiate a new bloom filte.r 
		if(buffer != null && buffer.capacity() > 0) {
		    filter = new BloomFilter();
		    filter.unSerialize(buffer);
		}
	    }
	    return filter;
	}

	/**
	 * Check for membership in the bloom filter. 
	 **/
	protected boolean tryFilterMembership(final TableKey key) {
	    filter = readFilter();
	    if(filter != null) {
		// return filter.contains(key.getValue().array());
		return filter.contains(key.getValue());
	    }

	    return false;
	}
    }

    /**
     * Iterate over the keys stored in the sstable. Try to balance
     * the number of keys stored in memory and performing contiguous I/O. 
     */
    class KeyIterator implements Iterator<TableKey> {
	/**
	 * Maximum number of keys to read in at once. This value is
	 * currently arbitrary, and has not undergone any performance testing!
	 */
	private static final int MAX_CACHED_KEYS = 32;

	private Path indexPath;
	private long fileOffset;
	private List<TableKey> cached;

	/**
	 * @param indexPath Path to the manifest file 
	 */
	public KeyIterator(Path indexPath) {
	    this.indexPath = indexPath;
	    fileOffset = 0;
	    cached = new LinkedList<>();

	    populateCache();
	}

	/**
	 * Populate the cache. 
	 */
	private void populateCache() {
	    try {
		FileChannel fc = FileChannel.open(indexPath, 
						  StandardOpenOption.READ);

		// Skip over the already read keys. 
		fc.position(fileOffset);

		ByteBuffer handle = 
		    ByteBuffer.allocate((Integer.SIZE / 8) + 2 * (Long.SIZE / 8));
		for(int i = 0; i < MAX_CACHED_KEYS && fc.position() < fc.size(); ++i) {
		    // Used to store the size & file handle info. 
		    fc.read(handle);
		    
		    handle.flip();
		    long offset = handle.getLong();
		    long dataLength = handle.getLong();
		    int keyLength = handle.getInt();
		    handle.flip();
		    
		    // Allocate a buffer for the key.
		    ByteBuffer keyBuf = ByteBuffer.allocate(keyLength);
		    fc.read(keyBuf); // Read in the key data.
		    keyBuf.flip(); // Go back to the start of the buffer. 
		    cached.add(TableKey.fromBytes(keyBuf)); // Reconstruct. 
		}

		// Save the new offset. 
		fileOffset = fc.position();

		fc.close();
	    } catch(IOException e) {
		e.printStackTrace();
	    }
	}

	/**
	 * Indicate if there are any more keys to read. 
	 */
	public boolean hasNext() {
	    return cached.size() > 0;
	}

	/**
	 * Get the next key. 
	 */
	public TableKey next() {
	    TableKey current = cached.remove(0);

	    // Check if we need to repopulate the list
	    // of cached values. 
	    if(cached.size() == 0) {
		populateCache();
	    }

	    return current;
	}

	/**
	 * Remove current element. This method is
	 * not implemented. 
	 */
	public void remove() {
	}
    }

}