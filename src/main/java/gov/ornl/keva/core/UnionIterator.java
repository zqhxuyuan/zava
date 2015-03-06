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
import java.util.ArrayList;

/**
 * Merge multiple, sorted iterators into a single iterator. This iterator
 * returns values in sorted order across iterators assuming that the user
 * supplies a comparator. This iterator is also special in that users can
 * define equivalence classes. Values within a single equivalence class can
 * be "merged" so that only a single value is returned. 
 * 
 * @author James Horey
 */
public class UnionIterator<E> implements Iterator<E> {
    private List<Iterator<? extends E>> iters;
    private List<E> index;
    private Comparator<E> comparator;

    /**
     * @param iterators The iterators containing all the values. Assumes that these
     * values are already sorted within a single iterator.
     * @param comparator Compare two values
     * @param equivOp Define an equivalence between values
     */
    public UnionIterator(List<Iterator<? extends E>> iterators,
			 Comparator<E> comparator) {
	this.iters = iterators;
	this.comparator = comparator;

	// Populate the index with null values for each slot. 
	index = new ArrayList<>(iters.size());
	for(int i = 0; i < iters.size(); ++i) {
	    index.add(null);
	}

	// Now populate the index with initial iterator values. 
	int i = 0;
	for(Iterator<? extends E> iter : iters) {
	    placeIntoSlot(iter, index, i++);
	}
    }

    /**
     * Helper method to place the next value from the iterator
     * into the appropriate slot. 
     **/
    private void placeIntoSlot(Iterator<? extends E> iter,
			       List<E> index,
			       int mySlot) {
	if(iter.hasNext()) {
	    boolean alreadyThere = false;
	    E v = iter.next();
	    for(int i = 0; i < iters.size(); ++i) {
		if(i != mySlot) {
		    if(index.get(i) != null) {
			int c = comparator.compare(v, index.get(i));
			if(c == 0) {
			    // We do not want to keep duplicate items. 
			    alreadyThere = true;
			    break;
			}
		    }
		}
	    }

	    if(alreadyThere) {
		// This value has been placed one of the other 
		// prior slots. We should fetch the next value
		// and try to place that into a slot. 
		placeIntoSlot(iter, index, mySlot);
	    }
	    else {
		// This is a new, independent value. Place into
		// the index and return the new size.
		index.set(mySlot, v);
	    }
	}
	else {
	    // There are no more values in this iterator. 
	    index.set(mySlot,null);
	}
    }

    /**
     * Indicate whether we have another element. 
     *
     * @return True if there is another value. False otherwise. 
     */
    @Override public boolean hasNext() {
	// Check if there are any values left.
	for(int i = 0; i < iters.size(); ++i) {
	    if(index.get(i) != null) {
		return true;
	    }
	}

	return false;
    }

    /**
     * Fetch the next element. 
     *
     * @return Next value in the iterator. 
     */
    @Override public E next() {
	// Go through all the values in the current set, and
	// choose the one with the least value (according to whatever
	// comparator is used). Afterwards, re-populate that slot. 
	E least = null;
	int leastIndex = 0;

	for(int i = 0; i < iters.size(); ++i) {
	    if(index.get(i) != null) {
		if(least == null) {
		    least = index.get(i);
		    leastIndex = i;
		}
		else if(comparator.compare(least, index.get(i)) > 0) {
		    // Only choose the least value. 
		    least = index.get(i);
		    leastIndex = i;
		}
	    }
	}

	// Fill up the used slot. 
	placeIntoSlot(iters.get(leastIndex), index, leastIndex);

	// Return the least value. 
	return least;
    }	

    /**
     * Remove current element (not implemented).
     */
    @Override public void remove() {
	// Do not implement.
    }
}