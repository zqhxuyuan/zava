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
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Keva libs.
 **/
import gov.ornl.keva.table.TableKey;
import gov.ornl.keva.table.TableValue;
import gov.ornl.keva.core.WriteOptions;

/**
 * Batch operations list write operations that
 * must be applied atomically. The writes are applied
 * in order for a single key, but are not ordered across
 * different keys. 
 *
 * @author James Horey
 **/
public class WriteBatch {
    private Map<TableKey, List<TableWrite>> writes;

    public WriteBatch() {
	writes = new HashMap<>();
    }

    /**
     * Add a write operation to the batch. 
     *
     * @param key The key of the value. 
     * @param value The value to place into the database. 
     * @param option Write options that define how the 
     */
    public void addWrite(final TableKey key, 
			 final TableValue value,
			 final WriteOptions option) {
	synchronized (this) {
	    List<TableWrite> values = writes.get(key);
	    if(values == null) {
		values = new ArrayList<>();
		writes.put(key, values);
	    }

	    values.add(new TableWrite(value, option));
	}
    }

    /**
     * Get the list of keys in the write batch.
     *
     * @return Iterator over the keys.
     */
    public Iterator<TableKey> iterator() {
	return writes.keySet().iterator();
    }

    /**
     * Get the value and write options associated with the key. 
     *
     * @param key The table key
     * @return List of (TableValue,WriteOption) pairs
     */
    public List<TableWrite> getValues(TableKey key) {
	return writes.get(key);
    }

    /**
     * Organize the value and option togethers. 
     */
    static class TableWrite {
	public TableWrite(TableValue value, WriteOptions options) {
	    this.value = value;
	    this.options = options;
	}

	public TableValue value = null;
	public WriteOptions options = null;
    }
}