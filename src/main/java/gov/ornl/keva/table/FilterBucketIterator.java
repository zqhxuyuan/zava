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

package gov.ornl.keva.table;

/**
 * Java libs. 
 **/
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Iterator;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.nio.ByteBuffer;
import java.nio.BufferOverflowException;

/**
 * Keva libs.
 **/
import gov.ornl.keva.core.VectorClock;
import gov.ornl.keva.core.EmptyIterator;
import gov.ornl.keva.core.MergeSortedIterator;
import gov.ornl.keva.core.StreamIterator;
import gov.ornl.keva.core.PruneOptions;

/**
 * Iterate through tables values, but filter out "tentative" values, and apply
 * the pruning options. 
 */
public class FilterBucketIterator extends StreamIterator<TableValue> {
    private Iterator<? extends TableValue> iter;
    private TableValue nextValue;
    private PruneOptions pruneOptions;
    private boolean hasSeenDelete; // Used during the pruning process. 
    private int numRemaining;     // Keep track of the number of elements.
    private int totalSize;

    /**
     * Filter out tentative values.
     **/
    public FilterBucketIterator(TableValueHistory history, 
				PruneOptions pruneOptions) {
	super(null);
	this.iter = history.iterator();
	this.pruneOptions = pruneOptions;
	nextValue = null;
	hasSeenDelete = false;
	numRemaining = history.size();
	totalSize = numRemaining;
    }

    /**
     * Help prune values. 
     **/
    private boolean doPrune(TableValue value) {
	if(pruneOptions != null) {
	    // Check if the user only wants the newest elements. 
	    boolean pruneBySize = false;

	    if(pruneOptions.newest != -1 &&
	       numRemaining >= pruneOptions.newest) {
		pruneBySize = true;
	    }

	    // We need to make sure that there
	    // is a delete operation on this history. 
	    // Otherwise, we can safely return all the values. 
	    if(pruneOptions.delete) {
		// Need to prune all values that appear *before* the
		// delete operation. 
		if(value.getStorageType() == TableValue.DELETE) {
		    hasSeenDelete = true;
		    return true; // Do not display the delete. 
		}
		else {
		    return !hasSeenDelete || pruneBySize;
		}
	    }
	    else {
		return pruneBySize;
	    }
	}

	return false;
    }

    /**
     * Get the number of elements being processed. 
     */
    public int size() {
	return totalSize;
    }

    /**
     * Indicate whether we have another element. 
     **/
    @Override public boolean hasNext() {
	nextValue = null;
	while(iter.hasNext()) {
	    nextValue = iter.next();
	    numRemaining--;
	    if(nextValue.getFlags() != TableValue.TENTATIVE) {
		// Check if we need to prune the value. 
		if(doPrune(nextValue)) {
		    continue;
		}

		// Ok found a good value.
		break;
	    }
	    else {
		// We accidently removed an item from the remaining count
		// even though it was tentative. Just add that value back.
		numRemaining++; 
	    }

	    // Reset so that if the last value is tentative, we
	    // get a null value. 
	    nextValue = null;
	}

	return nextValue != null;
    }

    /**
     * Fetch the next element. 
     **/
    @Override public TableValue next() {
	return nextValue;
    }

    /**
     * Remove current element. 
     **/
    @Override public void remove() {
	// Do not implement.
    }
}