/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Array<T> implements Iterable<T>
{
    // Iterable interface

    public Iterator<T> iterator()
    {
        return new ArrayIterator();
    }

    // Array interface

    public T at(int position)
    {
        try {
            return arrays.get(position / ARRAY_CAPACITY)[position % ARRAY_CAPACITY];
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException(String.format("arrays.size: %s, position: %s",
                                                              arrays == null ? null : arrays.size(),
                                                              position));
        }
    }

    public void at(int position, T value)
    {
        try {
            arrays.get(position / ARRAY_CAPACITY)[position % ARRAY_CAPACITY] = value;
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException(String.format("arrays.size: %s, position: %s",
                                                              arrays == null ? null : arrays.size(),
                                                              position));
        }
    }

    public void append(T x)
    {
        ensureArray();
        currentArray[currentArrayPosition++] = x;
        count++;
    }

    public int size()
    {
        return count;
    }

    // For use by this class

    private void ensureArray()
    {
        if (currentArrayPosition == ARRAY_CAPACITY) {
            currentArray = null;
        }
        if (currentArray == null) {
            currentArray = (T[]) new Object[ARRAY_CAPACITY];
            arrays.add(currentArray);
            currentArrayPosition = 0;
        }
    }

    // Class state

    private final static int ARRAY_CAPACITY = 1000;

    // Object state

    private final List<T[]> arrays = new ArrayList<T[]>();
    private T[] currentArray; // Last element of arrays, and the arrays array currently being loaded.
    private int currentArrayPosition;
    private int count = 0;

    // Inner classes

    private class ArrayIterator implements Iterator<T>
    {
        public boolean hasNext()
        {
            return position < count;
        }

        public T next()
        {
            T next;
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
