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

package gov.ornl.keva.mem;

/**
 * Java libs. 
 **/
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentHashMap;
import java.io.OutputStream;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Keva libs.
 **/
import gov.ornl.keva.table.TableKey;
import gov.ornl.keva.table.TableValue;
import gov.ornl.keva.table.TableValueHistory;
import gov.ornl.keva.table.TableDeleteOp;
import gov.ornl.keva.table.TableBucket;
import gov.ornl.keva.table.TableBucketFactory;
import gov.ornl.keva.core.VectorClock;
import gov.ornl.keva.core.EmptyIterator;
import gov.ornl.keva.core.StreamIterator;
import gov.ornl.keva.core.PruneOptions;

/**
 * A mem table is the datastructure that holds actual key-value data in memory.
 * All data is stored in sorted, navigable order to simplify data flushing. 
 * After the memtable reaches a certain threshold, the table is flushed onto
 * disk as an sstable.
 *
 * @author James Horey
 */
public class MemTable {
    /**
     * Memory threshold before flushing to disk. Normally
     * set to 1/3 the amount of free memory. However, the
     * can override this value during memtable creation. 
     */
    public static final long RECOMMENDED_THRESHOLD = 
	Runtime.getRuntime().freeMemory() / 3;

    /**
     * Used to make sure that memtables are completely
     * empty of writers before flushing. All operations 
     * normally use a "read" lock so that multiple writers
     * can co-exist. However, when performing a "flush", we
     * use a "write" lock to gain exclusitivity. 
     */
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock writes = rwLock.readLock();
    private final Lock flushes = rwLock.writeLock();

    private long memThreshold; // Max. size of table (in B). 
    private Comparator<TableValue> comp; // For sorting comparisons. 
    private final ConcurrentSkipListMap<TableKey, TableBucket> map; // Our values. 
    private volatile long runningKeyTotal; // Running total memory usage for keys.
    private volatile long runningDataTotal; // Running total memory usage for data.
    private PruneOptions pruneOptions;

    /**
     * The memtable must know its own threshold values.
     *
     * @param memThreshold How large the memtable should be before flushing to disk
     */
    public MemTable(long memThreshold) {
	// Figure out the memory threshold. 
	this.memThreshold = memThreshold == RECOMMENDED_THRESHOLD? 
	    RECOMMENDED_THRESHOLD : memThreshold; 

	runningKeyTotal = 0; // Keep track of memory usage. 
	runningDataTotal = 0; // Keep track of memory usage. 

	comp = null;

	map = new ConcurrentSkipListMap<TableKey, TableBucket>(new Comparator<TableKey>() {
		public int compare(TableKey k1, TableKey k2) {
		    return k1.compareTo(k2);
		}});
    }

    /**
     * Prune options are used to control which values get
     * read and flushed. Used to prune out old values, etc. 
     *
     * @param pruneOptions Prune options
     */
    public void setPruneOptions(PruneOptions pruneOptions) {
	this.pruneOptions = pruneOptions;
    }

    /**
     * Set the independent value comparator. Used to sort the independent values.
     *
     * @param comp The table value comparator 
     */
    public void setComparator(Comparator<TableValue> comp) {
	this.comp = comp;
    }

    /**
     * Get the independent value comparator.
     *
     * @return The table value comparator
     */
    public Comparator<TableValue> getComparator() {
	return comp;
    }

    /**
     * The memory threshold refers to the amount of memory the table can
     * use before it is flushed onto disk. 
     *
     * @param threshold Memory threshold
     */
    public void setMemThreshold(long threshold) {
	memThreshold = threshold;
    }

    /**
     * Get the current memory threshold. 
     *
     * @return Flushing memory threshold
     */
    public long getMemThreshold() {
	return memThreshold;
    }

    /**
     * Estimate current memory usage and check whether we are over the memory threshold. 
     *
     * @return boolean True if the table is over the threshold. False otherwise. 
     */
    public boolean shouldFlush() {
	if(runningKeyTotal + runningDataTotal > memThreshold) {
	    return true;
	}

	return false;
    }

    /**
     * Indicates whether a value for the table key exists. 
     *
     * @param key The table key used to identify the value
     * @return True if the key is in the memtable. False otherwise.
     */
    public boolean contains(final TableKey key) {
	return map.containsKey(key);
    }

    /**
     * Lock the row for writing. This is used during commit operations. 
     * 
     * @param key The table key used to identify the value
     */
    public void lock(final TableKey key) {
	TableBucket bucket = map.get(key);
	if(bucket != null) {
	    bucket.lockBucket();
	}
    }

    /**
     * Unlock the row for writing. This is used during commit operations. 
     *
     * @param key The table key used to identify the value
     */
    public void unlock(final TableKey key) {
	TableBucket bucket = map.get(key);
	if(bucket != null) {
	    bucket.unlockBucket();
	}
    }

    /**
     * Create the bucket if it doesn't exist. 
     */
    public void create(final TableKey key) {
	writes.lock();
	map.putIfAbsent(key, TableBucketFactory.newBucket(TableBucket.Constraint.SAFE, comp));
	writes.unlock();
    }

    /**
     * Commit the tentative value to the memtable.
     *
     * @param key The table key used to identify the value
     * @param value The table value to store
     * @param branch The branch to store the value (optional)
     */
    public void commit(final TableKey key, 
		       final TableValue value,
		       final String branch) {
	
	writes.lock();

	// Now add to the bucket. 
	TableBucket bucket = map.get(key);
	bucket.commit(value, branch);

	runningDataTotal += 
	    (Integer.SIZE / 8) +             // Size of the serialized data buffer.
	    value.getClock().memory() +      // Size of the vector clock
	    (Integer.SIZE / 8) +             // Size of compressed data length
	    (Integer.SIZE / 8) +             // Size of data length
	    value.memory();                  // Size of the actual data.

	runningKeyTotal +=
	    (Integer.SIZE / 8) +             // Size of key serialized length. 
	    key.size();                      // Size of the actual key

	writes.unlock();
    }

    /**
     * Add this value to the appropriate table bucket. 
     *
     * @param key The table key used to identify the value
     * @param value The table value to store
     * @param tentative Indicate if this is a tentative value. 
     */
    public void put(final TableKey key, 
		    final TableValue value,
		    final boolean tentative) {
	put(key, value, null, tentative);
    }

    /**
     * Add this value to the appropriate table bucket. 
     *
     * @param key The table key used to identify the value
     * @param value The table value to store
     * @param branch The branch to store the value (optional)
     * @param tentative Indicate if this is a tentative value. 
     */
    public void put(final TableKey key, 
		    final TableValue value,
		    final String branch,
		    final boolean tentative) { 
	TableBucket bucket;
	writes.lock();

	// See if we need to encode the "tentative" status.
	if(tentative) {
	    value.setFlags(TableValue.TENTATIVE);
	}

	// Create a new bucket if necessary & add the value. 
	map.putIfAbsent(key, TableBucketFactory.newBucket(TableBucket.Constraint.SAFE, comp));
	bucket = map.get(key);

	// Is this a delete operation? If so, we need to explicitly
	// tell the bucket so that it knows how to prune later. 
	if(value instanceof TableDeleteOp) {
	    bucket.setDeleteOp(true);
	}

	// Using explicit branching? 
	if(branch != null) {
	    bucket.add(value, branch);
	}
	else {
	    bucket.add(value);
	}

	// Keep track of the total memory used. 
	if(value.getAttributes() != null) {
	    runningDataTotal += value.getAttributes().memory();
	}

	runningDataTotal += 
	    (Integer.SIZE / 8) +             // Size of the serialized data buffer.
	    value.getClock().memory() +      // Size of the vector clock
	    (Integer.SIZE / 8) +             // Size of compressed data length
	    (Integer.SIZE / 8) +             // Size of data length
	    value.memory();                  // Size of the actual data.

	runningKeyTotal +=
	    (Integer.SIZE / 8) +             // Size of key serialized length. 
	    key.size();                      // Size of the actual key

	writes.unlock();
    }

    /**
     * Get all the values associated with this key across all branches. 
     *
     * @param key The table key used to identify the value
     * @return An iterator over all the values associated with the key
     */
    public Map<String,StreamIterator<TableValue>> getAll(final TableKey key) {
	TableBucket bucket = map.get(key);
	if(bucket != null) {
	    return bucket.getComplete(pruneOptions);
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
    public Map<String,StreamIterator<TableValue>> getUncollapsed(final TableKey key,
							     final String branch) {
	TableBucket bucket = map.get(key);
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
    public Map<String,StreamIterator<TableValue>> getCollapsed(final TableKey key) {
	TableBucket b = map.get(key);
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
    public Map<String,StreamIterator<TableValue>> getCollapsed(final TableKey key,
							       final long time) {
	TableBucket b = map.get(key);
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
    public Map<String,StreamIterator<TableValue>> getCollapsed(final TableKey key,
							   final String branch) {
	TableBucket bucket = map.get(key);
	if(bucket != null) {
	    return bucket.getCollapsed(branch);
	}

	return null;
    }

    /**
     * Get the latest vector clocks associated with this key. 
     * 
     * @param key The table key used to identify the value
     * @return An iterator over all the latest vector clocks associated with the key
     */
    public Iterator<VectorClock> getAllClocks(final TableKey key) {
	TableBucket b = map.get(key);

	if(b != null) {
	    return b.getAllClocks();
	}

	return new EmptyIterator<VectorClock>();
    }

    /**
     * Return the keys in sorted order. 
     *
     * @return Iterator over the table keys
     */
    public Iterator<TableKey> getKeys() {
	return map.navigableKeySet().iterator();
    }

    /**
     * Get the number of unique keys in the memtable.
     * 
     * @return Number of keys
     */
    public int getNumKeys() {
	return map.size();
    }

    /**
     * Get the total number of entries associated with the key.
     *
     * @param key The table key used to identify the value
     * @return Number of entries
     */
    public long getNumEntries(final TableKey key) {
	TableBucket b = map.get(key);

	if(b != null) {
	    return b.size();
	}

	return 0;
    }

    /**
     * Flush the memtable of all active writers. 
     */
    public void flush() {
	flushes.lock();
	flushes.unlock();
    }

    /**
     * Get total amount of memory used by the data (excluding keys)
     *
     * @return Memory used in bytes
     */
    public long getDataSize() {
	return runningDataTotal;
    }

    /**
     * Get total amount of memory used by the keys (excluding data)
     *
     * @return Memory used in bytes
     */
    public long getKeySize() {
	return runningKeyTotal;
    }
}