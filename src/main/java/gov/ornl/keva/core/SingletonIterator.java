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
 * Used when we need an iterator, but there is only a single item.
 * 
 * @author James Horey
 */
public class SingletonIterator<E> implements Iterator<E> {
    private E value;

    /**
     * @param value Sole value to iterate over. 
     */
    public SingletonIterator(E value) {
	this.value = value;
    }

    /**
     * Indicate whether we have another element. 
     * 
     * @return True if there is another value. False otherwise. 
     */
    @Override public boolean hasNext() {
	return value != null;
    }    

    /**
     * Fetch the next element. 
     *
     * @return Next value in the iterator. 
     */
    @Override public E next() {
	E temp = value;	
	value = null;

	return temp;
    }

    /**
     * Remove current element (not implemented). 
     */
    @Override public void remove() {
	// Do not implement.
    }
}