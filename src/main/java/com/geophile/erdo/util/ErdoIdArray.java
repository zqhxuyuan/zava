/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.util;

/*
 * Stores erdo ids in order (because a KeyArray is in key order). Compressed using run-length encoding.
 * E.g. 0 0 0 0 0 1 1 2 3 3 3 3 ->
 * erdoId    startPosision
 *    0           0
 *    1           5
 *    2           7
 *    3           8
 *  count = 12
 */

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ErdoIdArray implements Transferrable
{
    // Object interface

    @Override
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append('[');
        for (int i = 0; i < nErdoIds; i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(startPosition[i]);
            buffer.append(": ");
            buffer.append(erdoIds[i]);
        }
        buffer.append(", ");
        buffer.append(count);
        buffer.append(": --");
        buffer.append(']');
        return buffer.toString();
    }

    // Transferrable interface

    public void writeTo(ByteBuffer buffer) throws BufferOverflowException
    {
        buffer.putInt(count);
        buffer.putInt(nErdoIds);
        for (int i = 0; i < nErdoIds; i++) {
            buffer.putInt(erdoIds[i]);
            buffer.putInt(startPosition[i]);
        }
    }

    public void readFrom(ByteBuffer buffer)
    {
        count = buffer.getInt();
        nErdoIds = buffer.getInt();
        erdoIds = new int[nErdoIds];
        startPosition = new int[nErdoIds];
        for (int i = 0; i < nErdoIds; i++) {
            erdoIds[i] = buffer.getInt();
            startPosition[i] = buffer.getInt();
        }
    }

    public int recordCount()
    {
        // The number of INT_SIZEd records in the serialized representation
        return nErdoIds * 2 + 2;
    }


    // ErdoIdArray interface

    public void append(int erdoId)
    {
        if (nErdoIds == 0 || erdoIds[nErdoIds - 1] != erdoId) {
            if (nErdoIds == erdoIds.length) {
                int newLength = erdoIds.length * 2;
                erdoIds = Arrays.copyOf(erdoIds, newLength);
                startPosition = Arrays.copyOf(startPosition, newLength);
            }
            erdoIds[nErdoIds] = erdoId;
            startPosition[nErdoIds] = count;
            nErdoIds++;
        }
        count++;
    }

    public int at(int position)
    {
        if (position < 0 || position >= count) {
            throw new IndexOutOfBoundsException(Integer.toString(position));
        }
        if (nErdoIds == 1) {
            return erdoIds[0];
        } else {
            for (int i = nErdoIds - 1; i >= 0; i--) {
                if (position >= startPosition[i]) {
                    return erdoIds[i];
                }
            }
            assert false;
            return -1;
        }
    }

    public void removeLast()
    {
        assert count > 0;
        count--;
        if (startPosition[nErdoIds - 1] == count) {
            nErdoIds--;
        }
    }

    public int size()
    {
        return count;
    }

    public int serializedSize()
    {
        return INT_SIZE * recordCount();
    }

    // Class state

    private static final int INITIAL_SIZE = 10;
    private static final int INT_SIZE = 4;

    // Object state

    // erdoId[i] runs from startPosition[i] to startPosition[i+1], (or to the end of the key array for the last erdoId).
    private int[] erdoIds = new int[INITIAL_SIZE];
    private int[] startPosition = new int[INITIAL_SIZE];
    private int nErdoIds = 0;
    private int count = 0;
}
