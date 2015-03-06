/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap;

// An array of longs >= 0. Keeps track of min value present, and the number of bytes required to represent deltas
// from this minimum, (0, 1, 2 or 4). This is necessary so that, as the page is filled, we know how much data
// is required to store this array. When closed, the deltas are stored, in an array of 1, 2, 4, or 8-byte integers.
// (Or if all the timestamps are identical, no array is needed.)

import com.geophile.erdo.util.Transferrable;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

public class CompressibleLongArray implements Transferrable
{
    // Transferrable interface

    public void writeTo(ByteBuffer buffer) throws BufferOverflowException
    {
        assert closed();
        // User is expected to know the number of array elements
        switch (deltaBytes) {
            case 0:
                break;
            case 1:
                for (int i = 0; i < size; i++) {
                    buffer.put(deltas1Byte[i]);
                }
                break;
            case 2:
                for (int i = 0; i < size; i++) {
                    buffer.putShort(deltas2Byte[i]);
                }
                break;
            case 4:
                for (int i = 0; i < size; i++) {
                    buffer.putInt(deltas4Byte[i]);
                }
                break;
            case 8:
                for (int i = 0; i < size; i++) {
                    buffer.putLong(deltas8Byte[i]);
                }
                break;
            default:
                assert false;
                break;
        }
    }

    public void readFrom(ByteBuffer buffer)
    {
        // size and deltaBytes must be set before reading
        assert size != -1;
        assert deltaBytes != -1;
        switch (deltaBytes) {
            case 0:
                break;
            case 1:
                deltas1Byte = new byte[size];
                for (int i = 0; i < size; i++) {
                    deltas1Byte[i] = buffer.get();
                }
                break;
            case 2:
                deltas2Byte = new short[size];
                for (int i = 0; i < size; i++) {
                    deltas2Byte[i] = buffer.getShort();
                }
                break;
            case 4:
                deltas4Byte = new int[size];
                for (int i = 0; i < size; i++) {
                    deltas4Byte[i] = buffer.getInt();
                }
                break;
            case 8:
                deltas8Byte = new long[size];
                for (int i = 0; i < size; i++) {
                    deltas8Byte[i] = buffer.getLong();
                }
                break;
            default:
                assert false;
                break;
        }
    }

    public int recordCount()
    {
        return size;
    }

    // CompressibleLongArray interface

    public void close()
    {
        if (!closed()) {
            // Convert to deltas
            switch (deltaBytes) {
                case 0:
                    break;
                case 1:
                    deltas1Byte = new byte[size];
                    for (int i = 0; i < size; i++) {
                        deltas1Byte[i] = (byte) ((array[i] - min) & 0xff);
                    }
                    break;
                case 2:
                    deltas2Byte = new short[size];
                    for (int i = 0; i < size; i++) {
                        deltas2Byte[i] = (short) ((array[i] - min) & 0xffff);
                    }
                    break;
                case 4:
                    deltas4Byte = new int[size];
                    for (int i = 0; i < size; i++) {
                        deltas4Byte[i] = (int) (array[i] - min);
                    }
                    break;
                case 8:
                    deltas8Byte = array;
                    for (int i = 0; i < size; i++) {
                        deltas8Byte[i] -= min;
                    }
            }
            array = null;
        }
    }

    public long at(int position)
    {
        assert closed();
        switch (deltaBytes) {
            case 0: return min;
            case 1: return min + (deltas1Byte[position] & 0xffL);
            case 2: return min + (deltas2Byte[position] & 0xffffL);
            case 4: return min + (deltas4Byte[position] & 0xffffffffL);
            case 8: return min + deltas8Byte[position];
            default:
                assert false;
                return -1;
        }
    }

    public long min()
    {
        return min;
    }

    public int deltaBytes()
    {
        return deltaBytes == -1 ? 0 : deltaBytes;
    }

    public byte[] deltas1Byte()
    {
        assert deltas1Byte != null;
        return deltas1Byte;
    }

    public short[] deltas2Byte()
    {
        assert deltas2Byte != null;
        return deltas2Byte;
    }

    public int[] deltas4Byte()
    {
        assert deltas4Byte != null;
        return deltas4Byte;
    }

    public long[] deltas8Byte()
    {
        assert deltas8Byte != null;
        return deltas8Byte;
    }

    public int size()
    {
        return size;
    }

    public void append(long x)
    {
        // Store the new value
        if (size == array.length) {
            long[] newArray = new long[size * 2];
            System.arraycopy(array, 0, newArray, 0, size);
            array = newArray;
        }
        array[size++] = x;
        // Adjust min, max, deltaBytes
        if (size == 1) {
            min = x;
            max = x;
            deltaBytes = 0;
        } else if (x < min) {
            min = x;
            adjustDeltaBytes();
        } else if (x > max) {
            max = x;
            adjustDeltaBytes();
        }
        // else: min, max, deltaBytes don't change
    }

    public void removeLast()
    {
        assert !closed();
        size--;
        // Recompute min, max, deltaBytes
        min = array[0];
        max = min;
        for (int i = 1; i < size; i++) {
            min = Math.min(min, array[i]);
            max = Math.max(max, array[i]);
        }
        deltaBytes = 0;
        adjustDeltaBytes();
    }

    public CompressibleLongArray(int capacity)
    {
        size = 0;
        array = new long[Math.max(capacity, MIN_INITIAL_SIZE)];
    }

    // Setting up for deserialization
    public CompressibleLongArray(long min, int size, int deltaBytes)
    {
        this.min = min;
        this.size = size;
        this.deltaBytes = deltaBytes;
    }

    // For use by this class

    private void adjustDeltaBytes()
    {
        long delta = max - min;
        int newDeltaBytes = 0;
        if ((delta & MASK_4_BYTES) != 0) {
            newDeltaBytes = 8;
        } else if ((delta & MASK_2_BYTES) != 0) {
            newDeltaBytes = 4;
        } else if ((delta & MASK_1_BYTE) != 0) {
            newDeltaBytes = 2;
        } else if (delta != 0) {
            newDeltaBytes = 1;
        }
        deltaBytes = Math.max(deltaBytes, newDeltaBytes);
    }
    
    private boolean closed()
    {
        return array == null;
    }

    // Class state

    private static final int MIN_INITIAL_SIZE = 10;
    private static final long MASK_1_BYTE = ~0xffL;
    private static final long MASK_2_BYTES = ~0xffffL;
    private static final long MASK_4_BYTES = ~0xffffffffL;

    // Object state

    private int size = -1;
    private long[] array;
    private long min = -1L;
    private long max = -1L;
    private int deltaBytes = -1;
    private byte[] deltas1Byte;
    private short[] deltas2Byte;
    private int[] deltas4Byte;
    private long[] deltas8Byte;
}
