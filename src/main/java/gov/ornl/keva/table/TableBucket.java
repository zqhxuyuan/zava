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
 * Keva libs.
 **/
import gov.ornl.keva.core.VectorClock;
import gov.ornl.keva.core.PruneOptions;
import gov.ornl.keva.core.StreamIterator;

/**
 * Java libs. 
 **/
import java.util.Iterator;
import java.util.Collection;
import java.util.Map;

/**
 * A table bucket keeps track of a history of independent values. A table key
 * is associated with a single bucket. Users may choose from different implementations of
 * buckets, depending on the constraints the user wishes to place on the values.
 * 
 * @author James Horey
 */
public abstract class TableBucket extends TableValue {
    /**
     * Bucket implementation constraints. 
     */
    public enum Constraint {
	SAFE, UNSAFE
    };

    /**
     * Indicate whether this bucket has a delete operation. 
     **/
    protected boolean hasDeleteOp;

    public TableBucket() {
	super(TableValue.BUCKET);
	hasDeleteOp = false;
	setAttributes(new TableAttributes("Collection"));
    }

    /**
     * Set the delete indicator. 
     *
     * isDelete Delete indicator
     */
    public void setDeleteOp(boolean isDelete) {
	hasDeleteOp = isDelete;
    }

    /**
     * Commit the value identified by vector clock and branch. 
     *
     * @param value Table value to commit
     * @param branch Specific branch where the value resides (optional)
     */
    public abstract boolean commit(final TableValue value,
				   final String branch);

    /**
     * Add a new value to this bucket to a specific branch.
     * This means that the vector clock associated with the
     * value should be appended to the branch vector clock. 
     * 
     * @param value Table value to add to bucket
     * @param branch Specific branch where the value resides (optional)
     */
    public abstract void add(final TableValue value,
			     final String branch);

    /**
     * Add a new value to this bucket. The value is added
     * using the vector clock associated with the value. 
     *
     * @param value Table value to add to bucket
     */
    public abstract void add(final TableValue value);

    /**
     * Bulk add the values supplied into this bucket. 
     *
     * @param values Tables values to add to bucket
     */
    public abstract void addAll(final Collection<TableValue> values);

    /**
     * Lock the bucket to all writes.
     */
    public abstract void lockBucket();

    /**
     * Unlock the bucket to all writes.
     */
    public abstract void unlockBucket();

    /**
     * Return number of entries in the bucket. Useful to check if additional
     * items have been added after compaction/flushing. 
     *
     * @return Number of bucket elements
     */
    public abstract long size();

    /**
     * Get the memory usage of the entire bucket.
     *
     * @return Number of bytes used by the bucket
     */
    public abstract long memory();

    /**
     * Return the vector clock for this bucket. The clock is a merger
     * of all the subclocks. 
     *
     * @return Merged vector clock
     */
    public abstract VectorClock getClock();

    /**
     * Return an iterattor of vector clocks from each of the data values
     * in the bucket.
     *
     * @return Iterator over vector clocks 
     */
    public abstract Iterator<VectorClock> getAllClocks();

    /**
     * Used to iterate over all the values across all the branches. 
     *
     * @param pruneOptions Options to restrict viewing particular values (e.g., deletes)
     * @return Iterator over table values
     */
    public abstract Map<String,StreamIterator<TableValue>> getComplete(final PruneOptions pruneOptions);

    /**
     * Return the historical list of values from the supplied branch name. 
     *
     * @param branch Specific branch where the value resides
     * @param pruneOptions Options to restrict viewing particular values (e.g., deletes)
     * @return Iterator over table values
     */
    public abstract Map<String,StreamIterator<TableValue>> getUncollapsed(final String branch, 
								      final PruneOptions pruneOptions);


    /**
     * Iterate over the set of independent values. Each independent value 
     * is collapsed so that only the latest value is returned. 
     * 
     * @return Iterator over table values
     */
    public abstract Map<String,StreamIterator<TableValue>> getCollapsed();

    /**
     * Return the latest value associated with this branch. 
     *
     * @param branch Specific branch where the value resides
     * @return Table value
     */
    public abstract Map<String,StreamIterator<TableValue>> getCollapsed(final String branch);

    /**
     * Return the values that were recorded before or during the supplied time. 
     *
     * @param time Last time recorded
     * @return Iterator over table values
     */
    public abstract Map<String,StreamIterator<TableValue>> getCollapsed(final long time);

    /**
     * Get all the data associated with this bucket. 
     *
     * @return Serialized data
     */
    public abstract byte[] getData();

    /**
     * Normally used by the TableValue to set the clock for a value. However,
     * the clock for buckets are derived, so we do not support this operation. 
     *
     * @param clock Vector clock
     */
    @Override public void setClock(VectorClock clock) {
	// Buckets do not support setting an explicit clock!
    }
}