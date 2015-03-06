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
import gov.ornl.keva.core.StreamIterator;
import gov.ornl.keva.core.PruneOptions;

/**
 * Store a history of values. These values must all belong to a single branch. 
 */
public abstract class TableValueHistory {
    protected String branchName;

    /**
     * For filtering purposes. 
     */
    protected TableValueHistory oldHistory;
    protected PruneOptions pruneOptions;

    public TableValueHistory() {
	branchName = null;

	this.oldHistory = null;
	this.pruneOptions = null;
    }

    public TableValueHistory(TableValueHistory oldHistory,
			     PruneOptions pruneOptions) {
	branchName = oldHistory.getBranchName();

	this.oldHistory = oldHistory;
	this.pruneOptions = pruneOptions;
    }

    /**
     * Get/set the branch name. 
     **/
    public void setBranchName(String b) {
	branchName = b;
    }
    public String getBranchName() {
	return branchName;
    }

    /**
     *Add a value. 
     **/
    public abstract void add(final TableValue value);

    /**
     * Get the latest value. 
     **/
    public abstract TableValue getCollapsedValue();

    /**
     * Iterate over the entries.
     **/
    public abstract StreamIterator<TableValue> iterator();

   /**
     * Get the number of elements in the history. 
     **/
    public abstract int size();

    /**
     * Used for memory estimation.
     **/
    public abstract long memory();
}