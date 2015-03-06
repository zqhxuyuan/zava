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
import java.util.Comparator;

/**
 * Selects a single item from a set of choices. 
 */
public class MinimumIterator<E> extends StreamIterator<E> {
    private boolean decisionMade;
    protected List<E> choices;

    /**
     * Construct an empty iterator. 
     */
    public MinimumIterator(Comparator<E> comparator) {
	super(comparator);
	decisionMade = false;
	choices = new ArrayList<>();
    }

    /**
     * Get the number of elements being processed. 
     */
    @Override public int size() {
	return 1;
    }

    /**
     * Indicate whether we have another element. 
     */
    @Override public boolean hasNext() {
	return 
	    choices.size() > 0 &&
	    !decisionMade;
    }

    /**
     * Fetch the next element. 
     */
    @Override public E next() {
	E e = Collections.max(choices, comparator);
	decisionMade = true;

	return e;
    }

    /**
     * Add a new iterator to the list of choices. 
     */
    public void addAll(List<StreamIterator<E>> iters) {
	for(StreamIterator<E> iter : iters) {
	    if(iter.hasNext()) {
		choices.add(iter.next());
	    }
	}
    }
}