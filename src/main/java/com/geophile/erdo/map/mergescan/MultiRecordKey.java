/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.mergescan;

import com.geophile.erdo.AbstractKey;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

// A MultiRecordKey R represents keys between R.lo inclusive and R.hi exclusive.
// If R.lo is null, the MultiRecordKey represents keys < R.hi.
// If R.hi is null, the MultiRecordKey represents keys >= R.lo.
// If R.lo and R.hi are both null, the MultiRecordKey represents all keys.

public class MultiRecordKey extends AbstractKey
{
    // Object interface

    @Override
    public String toString()
    {
        return String.format("%s : %s", lo, hi);
    }

    // Comparable interface

    @Override
    public int compareTo(AbstractKey key)
    {
        int c = super.compareTo(key);
        if (c == 0) {
            MultiRecordKey that = (MultiRecordKey) key;
            if (this.hi != null && that.lo != null && this.hi.compareTo(that.lo) <= 0) {
                c = -1;
            } else if (this.lo != null && that.hi != null && this.lo.compareTo(that.hi) >= 0) {
                c = 1;
            }
        }
        return c;
    }

    // Transferrable interface

    @Override
    public void readFrom(ByteBuffer buffer) throws BufferUnderflowException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeTo(ByteBuffer buffer) throws BufferOverflowException
    {
        throw new UnsupportedOperationException();
    }

    // AbstractKey interface

    @Override
    public int estimatedSizeBytes()
    {
        assert false;
        return -1;
    }

    @Override
    public AbstractKey copy()
    {
        throw new UnsupportedOperationException();
    }

    // MultiRecordKey interface

    public MultiRecordKey(AbstractKey lo, AbstractKey hi)
    {
        assert lo != null;
        assert hi == null || lo.compareTo(hi) < 0 : String.format("%s : %s", lo, hi);
        erdoId(lo.erdoId());
        this.lo = lo;
        this.hi = hi;
    }

    public AbstractKey lo()
    {
        return lo;
    }

    public AbstractKey hi()
    {
        return hi;
    }

    // Object state

    private AbstractKey lo;
    private AbstractKey hi;
}
