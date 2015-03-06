/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.apiimpl;

import com.geophile.erdo.AbstractKey;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

// Used to specify the lower bound of an unrestricted OrderedMap cursor. Will never be serialized.

public class ErdoId extends AbstractKey
{
    // AbstractKey interface

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

    @Override
    public int estimatedSizeBytes()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public AbstractKey copy()
    {
        throw new UnsupportedOperationException();
    }

    // ErdoId interface

    public boolean lowest()
    {
        return lowest;
    }

    public static ErdoId lowest(int erdoId)
    {
        return new ErdoId(erdoId, true);
    }

    public static ErdoId highest(int erdoId)
    {
        return new ErdoId(erdoId, false);
    }

    // For use by this class

    private ErdoId(int erdoId, boolean lowest)
    {
        erdoId(erdoId);
        this.lowest = lowest;
    }

    // Object state

    private final boolean lowest; // false: lower than all keys. true: higher than all keys
}
