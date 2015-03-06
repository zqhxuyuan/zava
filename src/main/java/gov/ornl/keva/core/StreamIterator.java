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
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Comparator;

/**
 * Iterators that contain some extra options.
 */
public abstract class StreamIterator<E> implements Iterator<E> {
    protected Comparator<E> comparator;
    // protected List<E> choices;

    /**
     * Construct an empty iterator. 
     */
    public StreamIterator(Comparator<E> comparator) {
	this.comparator = comparator;
	// choices = new ArrayList<>();
    }

    /**
    //  * Add a history value. 
    //  */
    // public abstract void add(Iterator<E> choice);

    /**
     * Get the number of elements being processed. 
     */
    public abstract int size();

    /**
     * Indicate whether we have another element. 
     **/
    public abstract boolean hasNext();

    /**
     * Fetch the next element. 
     **/
    public abstract E next();

    /**
     * Remove current element. 
     **/
    public void remove() {
	// Do not implement.
    }

    /**
     * Use a reverse iterator.
     */
    public static class ReverseIterator<E> extends StreamIterator<E> {
	private ListIterator<E> iter;
	private int size;

	/**
	 * Construct an empty iterator. 
	 */
	public ReverseIterator(ListIterator<E> iter, int size) {
	    super(null);
	    this.iter = iter;
	    this.size = size;
	}

	/**
	 * Get the number of elements being processed. 
	 */
	public int size() {
	    return size;
	}

	/**
	 * Indicate whether we have another element. 
	 **/
	public boolean hasNext() {
	    return iter.hasPrevious();
	}

	/**
	 * Fetch the next element. 
	 **/
	public E next() {
	    return iter.previous();
	}
    }

    /**
     * Wrap this iterator and add stream-specific 
     * capabilities
     */
    public static class SimpleIterator<E> extends StreamIterator<E> {
	private Iterator<E> iter;
	private int size;
	private int i = 0;

	/**
	 * Construct an empty iterator. 
	 */
	public SimpleIterator(Iterator<E> iter, int size) {
	    super(null);
	    this.iter = iter;
	    this.size = size;
	}

	/**
	 * Get the number of elements being processed. 
	 */
	public int size() {
	    return size;
	}

	/**
	 * Indicate whether we have another element. 
	 **/
	public boolean hasNext() {
	    return iter.hasNext();
	}

	/**
	 * Fetch the next element. 
	 **/
	public E next() {
	    return iter.next();
	}
    }
}