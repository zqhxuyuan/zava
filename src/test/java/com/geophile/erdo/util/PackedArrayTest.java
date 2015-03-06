/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.util;

import com.geophile.erdo.memorymonitor.MemoryMonitor;
import com.geophile.erdo.memorymonitor.MemoryTracker;
import org.junit.Test;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class PackedArrayTest
{
    @Test
    public void testEmpty()
    {
        try {
            PackedArray a = packedArray();
            Element t = new Element();
            a.at(0, t);
            assertTrue(false);
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    public void testNonEmpty()
    {
        final int N = 2000;
        PackedArray a = packedArray();
        for (int size = 1; size <= N; size++) {
            Element e = new Element(size);
            a.append(e);
        }
        for (int size = 1; size <= N; size++) {
            Element e = new Element();
            a.at(size - 1, e);
            assertEquals(size, e.size());
        }
    }

    @Test
    public void testRandom()
    {
        final int N = 10000;
        Random random = new Random();
        for (int t = 0; t < 100; t++) {
            PackedArray a = packedArray();
            int[] sizes = new int[N];
            for (int i = 0; i < N; i++) {
                int size = 10 + random.nextInt(500);
                Element e = new Element(size);
                a.append(e);
                sizes[i] = size;
            }
            for (int i = 0; i < N; i++) {
                int size = sizes[i];
                Element e = new Element();
                a.at(i, e);
                assertEquals(size, e.size());
            }
        }
    }

    private PackedArray packedArray()
    {
        MemoryTracker<PackedArray> tracker =
            new MemoryTracker<PackedArray>(new MemoryMonitor(), MemoryMonitor.KEY_ARRAY_KEYS);
        PackedArray packedArray = new PackedArray(tracker);
        tracker.object(packedArray);
        return packedArray;
    }

    private static class Element implements Transferrable
    {
        public void writeTo(ByteBuffer buffer) throws BufferOverflowException
        {
            buffer.put(bytes);
        }

        public void readFrom(ByteBuffer buffer)
        {
            int n = buffer.remaining();
            bytes = new byte[n];
            buffer.get(bytes);
            for (int i = 0; i < n; i++) {
                assertEquals((byte) (i & 0xff), bytes[i]);
            }
        }

        public Element(int size)
        {
            bytes = new byte[size];
            for (int i = 0; i < size; i++) {
                bytes[i] = (byte) (i & 0xff);
            }
        }

        public int recordCount()
        {
            return 1;
        }

        public int size()
        {
            return bytes.length;
        }

        public Element()
        {
        }

        private byte[] bytes;
    }
}
