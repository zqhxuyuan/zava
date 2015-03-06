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

package gov.ornl.keva.comparators;

/**
 * Java libs.
 **/
import java.util.Comparator;

/**
 * Keva libs.
 **/
import gov.ornl.keva.table.TableValue;

/**
 * Simple implementation of a alpha-numeric comparison method. 
 */
public class AlphaNumComparator implements Comparator<TableValue> {
    /**
     * Compare the two values as embedded strings. 
     *
     * @param v1 First value to compare
     * @param v2 Second value to compare
     * @return Positive if the first value is greater, negative
     * if the second value is greater.
     */
    public int compare(TableValue v1, TableValue v2) {
	String s1 = new String(v1.getData());
	String s2 = new String(v2.getData());

	return s1.compareTo(s2);
    }
}