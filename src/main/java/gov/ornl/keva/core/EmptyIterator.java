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

package gov.ornl.keva.core;

/**
 * Java libs.
 **/
import java.util.Iterator;

/**
 * Used when we need an iterator, but there no items to iterate. 
 *
 * @author James Horey
 **/
public class EmptyIterator<E> implements Iterator<E> {
    /**
     * Indicate whether we have another element. 
     * 
     * @return True if there is another value. False otherwise. 
     */
    @Override public boolean hasNext() {
	return false;
    }

    /**
     * Fetch the next element. 
     *
     * @return Next value in the iterator. 
     */
    @Override public E next() {
	return null;
    }

    /**
     * Remove current element  (not implemented). 
     */
    @Override public void remove() {
	// Do not implement.
    }
}