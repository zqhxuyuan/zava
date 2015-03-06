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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Keva libs.
 **/
import gov.ornl.keva.core.StreamIterator;
import gov.ornl.keva.core.PruneOptions;

/**
 * Store a history of values. These values must all belong to a single branch. 
 * This implementation is thread-safe. 
 */
public class SafeTableValueHistory extends TableValueHistory {
    private final ConcurrentLinkedDeque<TableValue> entries = 
    	new ConcurrentLinkedDeque<>();

    public SafeTableValueHistory() {
	super();
    }

    public SafeTableValueHistory(TableValueHistory oldHistory,
				 PruneOptions pruneOptions) {
	super(oldHistory, pruneOptions);
    }

    /**
     *Add a value. 
     **/
    @Override public void add(final TableValue value) {
	entries.add(value);
    }

    /**
     * Get the latest value. 
     **/
    @Override public TableValue getCollapsedValue() {
	return TableBucketCollapser.collapseHistory(entries.descendingIterator());
    }

    /**
     * Iterate over the entries.
     **/
    @Override public StreamIterator<TableValue> iterator() {
	if(oldHistory != null) {
	    return new FilterBucketIterator(oldHistory, 
					    pruneOptions);
	}
	else {
	    return new StreamIterator.SimpleIterator<TableValue>(entries.iterator(), entries.size());
	}
    }

    /**
     * Get the number of elements in the history. 
     **/
    @Override public int size() {
	if(oldHistory != null) {
	    return oldHistory.size();
	}

	return entries.size();
    }

    /**
     * Used for memory estimation.
     **/
    @Override public long memory() {
	long s = 0;

	for(TableValue v : entries) {
	    s += v.memory();
	}

	return s;
    }
}