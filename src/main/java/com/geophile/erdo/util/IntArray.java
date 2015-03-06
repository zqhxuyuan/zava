/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.util;

import com.geophile.erdo.memorymonitor.MemoryTracker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class IntArray implements Iterable<Integer>, MemoryTracker.Trackable<IntArray>
{
    // MemoryTracker.Trackable interface

    public long sizeBytes()
    {
        return arrays.size() * ARRAY_CAPACITY * 4;
    }

    // Iterable interface

    // TODO: Get rid of this. It forces lots of Integer generation
    public Iterator<Integer> iterator()
    {
        return new IntArrayIterator();
    }

    // IntArray interface

    public int at(int position)
    {
        try {
            return arrays.get(position / ARRAY_CAPACITY)[position % ARRAY_CAPACITY];
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException(String.format("arrays.size: %s, position: %s",
                                                              arrays == null ? null : arrays.size(),
                                                              position));
        }
    }

    public void at(int position, int value)
    {
        try {
            arrays.get(position / ARRAY_CAPACITY)[position % ARRAY_CAPACITY] = value;
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException(String.format("arrays.size: %s, position: %s",
                                                              arrays == null ? null : arrays.size(),
                                                              position));
        }
    }

    public void append(int x)
    {
        ensureArray();
        currentArray[currentArrayPosition++] = x;
        count++;
    }

    public int size()
    {
        return count;
    }

    public IntArray(MemoryTracker<IntArray> memoryTracker)
    {
        this.memoryTracker = memoryTracker;
    }

    // For use by this class

    private void ensureArray()
    {
        if (currentArrayPosition == ARRAY_CAPACITY) {
            currentArray = null;
        }
        if (currentArray == null) {
            currentArray = new int[ARRAY_CAPACITY];
            arrays.add(currentArray);
            currentArrayPosition = 0;
            if (memoryTracker != null) {
                memoryTracker.track(arrays.size() * ARRAY_CAPACITY * 4);
            }
        }
    }

    // Class state

    private final static int ARRAY_CAPACITY = 1000;

    // Object state

    private final MemoryTracker<IntArray> memoryTracker;
    private final List<int[]> arrays = new ArrayList<int[]>();
    private int[] currentArray; // Last element of arrays, and the arrays array currently being loaded.
    private int currentArrayPosition;
    private int count = 0;

    // Inner classes

    private class IntArrayIterator implements Iterator<Integer>
    {
        public boolean hasNext()
        {
            return position < count;
        }

        public Integer next()
        {
            Integer next;
            if (position >= count) {
                throw new NoSuchElementException();
            }
            next = at(position++);
            return next;
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        private int position = 0;
    }
}
