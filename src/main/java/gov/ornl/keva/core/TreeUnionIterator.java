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
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

/**
 * Merge multiple, sorted iterators into a single iterator. This iterator
 * returns values in sorted order across iterators assuming that the user
 * supplies a comparator. This iterator is also special in that users can
 * define equivalence classes. Values within a single equivalence class can
 * be "merged" so that only a single value is returned. 
 * 
 * @author James Horey
 */
public class TreeUnionIterator<E> implements Iterator<E> {
    private List<Iterator<? extends E>> iters;
    private TreeSet<UnionValue<E>> index;
    private Comparator<E> comparator;

    /**
     * @param iterators The iterators containing all the values. Assumes that these
     * values are already sorted within a single iterator.
     * @param comparator Compare two values
     * @param equivOp Define an equivalence between values
     */
    public TreeUnionIterator(final List<Iterator<? extends E>> iterators,
			     final Comparator<E> comparator) {
	this.iters = iterators;
	this.comparator = comparator;

	// Populate the index with null values for each slot. 
	index = new TreeSet<UnionValue<E>>(new Comparator<UnionValue<E>>() {
		public int compare(UnionValue<E> v1, UnionValue<E> v2) {
		    return comparator.compare(v1.e, v2.e);
		}
	    });

	// Now populate the index with initial iterator values. 
	for(Iterator<? extends E> iter : iters) {
	    placeIntoSlot(iter, index);
	}
    }

    /**
     * Helper method to place the next value from the iterator
     * into the appropriate slot. 
     **/
    private void placeIntoSlot(Iterator<? extends E> iter,
			       TreeSet<UnionValue<E>> index) {
	while(iter.hasNext()) {
	    E e = iter.next();
	    // Check if the et contains the next element. If so, then we
	    // can safe skip and proceed to the next element. 
	    UnionValue<E> value = new UnionValue<>(e, iter);
	    if(!index.contains(value)) {
		// Place an item into the tree map. 
		index.add(value);
		break;
	    }
	}
    }

    /**
     * Indicate whether we have another element. 
     *
     * @return True if there is another value. False otherwise. 
     */
    @Override public boolean hasNext() {
	// Does the set still have any items left? 
	return index.size() > 0;
    }

    /**
     * Fetch the next element. 
     *
     * @return Next value in the iterator. 
     */
    @Override public E next() {
	// Get the least value from the index. 
	UnionValue<E> value = index.pollFirst();

	// Fill up the used slot. 
	placeIntoSlot(value.iter, index);

	// Return the least value. 
	return value.e;
    }	

    /**
     * Remove current element (not implemented).
     */
    @Override public void remove() {
	// Do not implement.
    }

    class UnionValue<E> {
	public E e;
	public Iterator<? extends E> iter;

	public UnionValue(E e, Iterator<? extends E> iter) {
	    this.e = e;
	    this.iter = iter;
	}
    }
}