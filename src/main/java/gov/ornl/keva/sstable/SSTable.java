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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Comparator;
import java.util.UUID;

/**
 * Keva libs.
 **/
import gov.ornl.keva.mem.MemTable;
import gov.ornl.keva.table.TableKey;
import gov.ornl.keva.table.TableValue;
import gov.ornl.keva.core.BloomFilter;
import gov.ornl.keva.core.StreamIterator;

/**
 * Base class for all sstables. An sstable is a persistant, on-disk version of a memtable. 
 * There may be many implementations depending on the specific application. 
 *
 * @author James Horey
 */
public abstract class SSTable {
    protected final String bloomPath; // Path of where the bloom filter is stored. 
    protected final String indexPath; // Path of where the metadata is stored. 
    protected final String dataPath; // Path of where the data is stored. 
    protected SSTableBufferAllocator service; // Used for actual writing. 
    protected String uuid =  null;
    protected long mergeIndex; // Used during an sstable merge. 
    private static int numBlocks = 0;

    /**
     * @param dp Data path
     * @param id Index path
     * @param bp Bloom filter path
     */
    public SSTable(String dp, String id, String bp) {
	dataPath = dp;
	indexPath = id;
	bloomPath = bp;
	mergeIndex = 0;
	service = SSTableBufferAllocator.newInstance();
    }

    /**
     * The data path stores where the actual data is stored.
     *
     * @return Data path
     */
    public String getDataPath() {
	return dataPath;
    }

    /**
     * The index path stores where the key index is stored.
     *
     * @return Index path
     */
    public String getIndexPath() {
	return indexPath;
    }

    /**
     * The filter path stores where the bloom filter is stored.
     *
     * @return Bloom filter path
     */
    public String getFilterPath() {
	return bloomPath;
    }

    /**
     * Set the UUID for this table. The UUID uniquely identifies
     * an sstable on disk. 
     *
     * @param id SStable ID
     */
    public void setUUID(String id) {
	uuid = id;
    }

    /**
     * Get the UUID for this table. 
     *
     * @return SStable ID
     */
    public String getUUID() {
	return uuid;
    }

    /**
     * Initialize this sstable from disk.
     */
    public abstract void init();

    /**
     * Return the bloom filter. The bloom filter is used to efficiently
     * identify values that are stored in the table.
     *
     * @return Bloom filter
     */
    public abstract BloomFilter getFilter();

    /**
     * Delete the sstable from disk.
     */
    public abstract void delete();

    /**
     * Determine if a value for the table key is stored on the sstable.
     *
     * @param key Table key
     * @return True if the element is found in the sstable. False otherwise.
     */
    public abstract boolean contains(final TableKey key);

    /**
     * Get all the historical table values along a single branch associated with
     * the supplied key. The branch is identified using the branch name. 
     *
     * @param key The table key used to identify the value
     * @param branch The branch to store the value
     * @return An iterator over all the historical values associated with the key along a specific branch. 
     */
    public abstract Map<String,StreamIterator<TableValue>> getUncollapsed(final TableKey key,
								      final String branch);

    /**
     * Get all the latest, independent values associated with this key. 
     * We will need to go through the bucket to reconstruct the latest values. 
     * 
     * @param key The table key used to identify the value
     * @return An iterator over the final, independent values associated with the key
     */
    public abstract Map<String,StreamIterator<TableValue>> getCollapsed(final TableKey key);

    /**
     * Get all the latest, independent values associated with this key. 
     * We will need to go through the bucket to reconstruct the latest values. 
     *
     * @param key The table key used to identify the value
     * @param time Prune all values with a wall time less than this time
     * @return An iterator over the final, independent values associated with the key
     */
    public abstract Map<String,StreamIterator<TableValue>> getCollapsed(final TableKey key,
					   final long time);
    /**
     * Get all the latest, independent values associated with this key on
     * the specified branch. Since this is a collapsed value, we should
     * only return a single value. 
     * 
     * @param key The table key used to identify the value
     * @return An iterator over the final, independent values associated with the key
     */
    public abstract Map<String,StreamIterator<TableValue>> getCollapsed(final TableKey key,
								    final String branch);
    /**
     * Get all the values associated with this key across all branches. 
     *
     * @param key The table key used to identify the value
     * @return An iterator over all the values associated with the key
     */
    public abstract Map<String,StreamIterator<TableValue>> getComplete(final TableKey key);

    /**
     * Return the keys in sorted order. 
     *
     * @return Iterator over the table keys
     */
    public abstract Iterator<TableKey> getKeys();

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
    public abstract SSTable merge(final List<SSTable> tables,
				  final String dataPath,
				  final String indexPath,
				  final String filterPath);

    /**
     * Create a new SSTable from the MemTable.
     *
     * @return The UUID of the new sstable
     */
    public abstract String flush(final MemTable mem);

    /**
     * Indicate how large the data portion of this sstable is.
     *
     * @return Memory used in bytes
     */
    public abstract long getDataSize();

    // /**
    //  * Get total amount of memory used by the keys (excluding data)
    //  * 
    //  * @return Memory used in bytes
    //  */
    // public abstract long getKeySize();

    /**
     * Get time of the last modification.
     *
     * @return Time in milliseconds
     */
    public abstract long getModificationTime();

    /**
     * Generate a new block ID. 
     */
    public static synchronized String newBlockID() {
	return UUID.randomUUID().toString();
	// return "block" + numBlocks++;
    }

    /**
     * Simple class to keep track of size and offset. 
     */
    static class FilePosition {
	public final static int SIZE = 2 * (Long.SIZE / 8);
	private long offset;
	private long size;

	public FilePosition(long o, long s) {
	    offset = o;
	    size = s;
	}

	public long getOffset() {
	    return offset;
	}

	public long getSize() {
	    return size;
	}
    }

    /**
     * Like FilePosition, except for compressed blocks. 
     */
    static class BlockPosition {
	public final static int SIZE = 3 * (Long.SIZE / 8);
	private int blockID;
	private long offset;
	private long size;

	public BlockPosition(int b, long o, long s) {
	    blockID = b;
	    offset = o;
	    size = s;
	}

	public int getBlockID() {
	    return blockID;
	}

	public long getOffset() {
	    return offset;
	}

	public long getSize() {
	    return size;
	}
    }

    /**
     * Tree map this also keeps track of the total size
     */
    static class ExtendedTreeMap<K, V> extends TreeMap<K, V> {
	private long keySize;
	// private long valueSize;
	private boolean completed;
	private long fp;
	private long offset;
	private Map<SSTable, Integer> fileOffsets;

	public ExtendedTreeMap(Comparator<K> comparator) {
	    super(comparator);
	    keySize = 0;
	    // valueSize = 0;
	    completed = true;
	    fp = Long.SIZE / 8;
	    offset = 0;
	    fileOffsets = new HashMap<>();
	}

	/**
	 * Indicates whether the map contains all the elements
	 * it's supposed to have. 
	 */
	public void setCompleted(boolean c) {
	    completed = c;
	}
	public boolean isCompleted() {
	    return completed;
	}

	/**
	 * The index keeps track of the current file position
	 * of the key we are inserting. 
	 */
	public void setKeyFilePosition(long i) {
	    fp = i;
	}
	public long getKeyFilePosition() {
	    return fp;
	}

	/**
	 * This is the key index. Probably need to re-name these things. 
	 */
	public void setOffset(long offset) {
	    this.offset = offset;
	}
	public long getOffset() {
	    return offset;
	}

	/**
	 * Used to keep track of where we are in a particular sstable. 
	 */
	public Map<SSTable, Integer> getFileOffsets() {
	    return fileOffsets;
	}

	public void setKeySize(long size) {
	    keySize = size;
	}
	public long getKeySize() {
	    return keySize;
	}

	// public void setValueSize(long size) {
	//     valueSize += size;
	// }
	// public long getValueSize() {
	//     return valueSize;
	// }
    }
}