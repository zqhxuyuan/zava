package com.ctriposs.bigmap;

import java.io.Closeable;
import java.io.IOException;
import java.util.NavigableSet;

/**
 * A Factory managing the creation & recycle of the map entry
 * 
 * @author bulldog
 *
 */
public interface IMapEntryFactory extends Closeable, IMMFStats {
	
	/**
	 * Acquire a new map entry, either new or reused
	 * 
	 * @param length length of the slot
	 * @return a map entry
	 * @throws IOException exception throw during the acquire operation
	 */
	public MapEntry acquire(int length) throws IOException;
	
	/**
	 * Release a map entry into the pool
	 * 
	 * @param me map entry
	 * @throws IOException exception thrown during the release operation
	 */
	public void release(MapEntry me) throws IOException;
	
	/**
	 * Find a map entry by specified index
	 * 
	 * @param index the target index
	 * @return a map entry
	 * @throws IOException exception thrown during the finding operation
	 */
	public MapEntry findMapEntryByIndex(long index) throws IOException;
	
	/**
     * Remove all data in the pool, this will empty the map and delete all back page files.
     *
     */
	public void removeAll() throws IOException;
	
    
    /**
     * Get total number of free entries with specific index
     * 
     * @param index free entry index
     * @return total number of free entries
     */
    long getFreeEntryCountByIndex(int index);
    
    /**
     * Get total size of free entries with specific index
     * 
     * @param index free entry index
     * @return total size of free entries
     */
    long getTotalFreeSlotSizeByIndex(int index);
    
    
    /**
     * For testing only
     * 
     * @return
     */
    NavigableSet<Integer> getFreeEntryIndexSet();
    
    
    /**
     * Persistent in memory cache
     */
    void flush();
}
