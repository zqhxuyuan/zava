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

import java.util.Comparator;
import gov.ornl.keva.table.TableValue;

/**
 * Used to allocate MemTables efficiently. This is an alternative to allocating
 * MemTables from the Java heap (i.e., using "new") which can cause way too many 
 * garbage collections. 
 *
 * THIS IMPLEMENTATION IS A SKELETON. WILL NEED TO INVESTIGATE BETTER SLAB
 * ALLOCATION STRATEGY.
 * 
 * @author James Horey
 */
public class MemTableAllocator {
    /**
     * Allocate a new MemTable.
     *
     * @param memThreshold Large large the memtable should get before being flushed to disk
     * @param comp Compare table values
     * @return A new memtable
     */
    public MemTable newMemTable(long memThreshold, 
				Comparator<TableValue> comp) {
	MemTable table = new MemTable(memThreshold);
	if(comp != null) {
	    table.setComparator(comp);
	}

	return table;
    }

    /**
     * Recycle the MemTable. 
     *
     * @param table Memtable to free
     */
    public void freeMemTable(MemTable table) {
	table = null;
    }
}