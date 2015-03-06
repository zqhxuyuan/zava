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
import java.util.Comparator;

/**
 * Create table bucket instances. 
 *
 * @author James Horey
 */
public class TableBucketFactory {

    /**
     * Created a new table bucket. If the bucket must be sorted
     * it then uses the SortedBucket implementation. Otherwise it
     * uses the UnsortedBucket implementation. 
     *
     * @param bucketOptions Bucket constraints (sorted, unsorted)
     * @param comparator Table value comparator
     * @return New table bucket
     */
    public static TableBucket newBucket(TableBucket.Constraint bucketOptions, Comparator<TableValue> comparator) {
	switch(bucketOptions) {
	case SAFE:
	    return new SortedBucket(comparator);
	case UNSAFE:
	    return new UnsafeBucket();
	}

	return null;
    }
}