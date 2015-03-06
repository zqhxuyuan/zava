package com.ctriposs.bigmap;

import java.io.IOException;

/**
 * Memory mapped file stats
 * 
 * @author bulldog
 *
 */
public interface IMMFStats {
	
	/**
     * Get total size of used back files(index and data files) of the big map
     *
     * @return total size of back files
     * @throws IOException exception thrown if there was any IO error during the getBackFileSize operation
     */
    long getBackFileUsed() throws IOException;
    
    /**
     * Get total number of free entries
     * 
     * @return total number of free entries
     */
    long getFreeEntryCount();
    
    /**
     * Get free entry count array
     * 
     * @return an array
     */
    long[] getFreeEntryCountArray();
    
    /**
     * Get total free slot size array
     * 
     * @return an array
     */
    long[] getTotalFreeSlotSizeArray();
    
    /**
     * Get total number of allocated(free + used) entries
     * 
     * @return total number of entries
     */
    long getTotalEntryCount();
    
    /**
     * Get total free slot size
     * 
     * @return total free slot size
     */
    long getTotalFreeSlotSize();
    
    /**
     * Get total slot size allocated(free + used)
     * 
     * @return total slot size
     */
    long getTotalSlotSize();
    
    /**
     * Get currently used total slot size
     * 
     * @return total slot size
     */
    long getTotalUsedSlotSize();

    /**
     * Get total really used slot size
     * 
     * @return total slot size
     */
    long getTotalRealUsedSlotSize();
    
    /**
     * Get currently wasted total slot size
     * 
     * @return total slot size
     */
    long getTotalWastedSlotSize();
    
    /**
     * Total number of acquire counter
     * 
     * @return counter
     */
    long getTotalAcquireCounter();
    
    /**
     * Total number of release counter
     * 
     * @return counter
     */
    long getTotalReleaseCounter();
    
    /**
     * Total number of exact match reuse counter
     * 
     * @return counter
     */
    long getTotalExatchMatchReuseCounter();
    
    /**
     * Total number of approximate match reuse counter
     * 
     * @return counter
     */
    long getTotalApproximateMatchReuseCounter();
    
    /**
     * Total number of acquire new counter
     * 
     * @return counter
     */
    long getTotalAcquireNewCounter();

}
