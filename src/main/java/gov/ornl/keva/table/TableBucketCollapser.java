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

/**
 * Collapse a bucket of values into the minimum number of 
 * independent values. 
 *
 * @author James Horey
 */
public class TableBucketCollapser {

    /**
     * Take all the values in the history and collapse to the
     * single, latest value. 
     *
     * Note that this method assumes that the values are sorted from oldest to newest!
     * @param history History of an independent value
     * @return Table value
     */
    public static TableValue collapseHistory(final Iterator<TableValue> history) {
    	while(history.hasNext()) {
    	    TableValue v = history.next();

    	    // Skip all the tentative values.
    	    if(v.getFlags() == TableValue.TENTATIVE) {
    		continue;
    	    }

	    return v;
    	}

	return null;
    }
}