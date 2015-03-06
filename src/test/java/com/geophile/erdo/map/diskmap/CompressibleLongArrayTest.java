/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap;

import org.junit.Test;

import static org.junit.Assert.*;

public class CompressibleLongArrayTest
{
    @Test
    public void testEmpty()
    {
        CompressibleLongArray a = new CompressibleLongArray(0);
        a.close();
        assertEquals(-1L, a.min());
        assertEquals(0, a.deltaBytes());
    }

    @Test
    public void testSingleton()
    {
        final long X = 123L;
        CompressibleLongArray a = new CompressibleLongArray(1);
        a.append(X);
        assertEquals(X, a.min());
        assertEquals(0, a.deltaBytes());
        a.close();
        assertEquals(X, a.min());
        assertEquals(0, a.deltaBytes());
    }

    @Test
    public void testAllDuplicates()
    {
        final int N = 10;
        final long X = 123L;
        CompressibleLongArray a = new CompressibleLongArray(N / 2); // To test growth of CULA.array
        for (int i = 0; i < N; i++) {
            assertEquals(i == 0 ? -1L : X, a.min());
            assertEquals(0, a.deltaBytes());
            a.append(X);
        }
        a.close();
        assertEquals(X, a.min());
        assertEquals(0, a.deltaBytes());
        assertEquals(N, a.size());
    }

    @Test
    public void test0Byte()
    {
        final int N = 10;
        final long X = 123L;
        CompressibleLongArray a = new CompressibleLongArray(N);
        for (int i = 0; i < N; i++) {
            a.append(X);
        }
        a.close();
        assertEquals(0, a.deltaBytes());
        try {
            a.deltas1Byte();
        } catch (AssertionError e)
        {}
        try {
            a.deltas2Byte();
        } catch (AssertionError e)
        {}
        try {
            a.deltas4Byte();
        } catch (AssertionError e)
        {}
        try {
            a.deltas8Byte();
        } catch (AssertionError e)
        {}
    }

    @Test
    public void test1Byte()
    {
        final int N = 0xff;
        final long X = 123L;
        CompressibleLongArray a = new CompressibleLongArray(N);
        for (int i = 0; i < N; i++) {
            a.append(X + i);
        }
        a.close();
        assertEquals(1, a.deltaBytes());
        byte[] deltas = a.deltas1Byte();
        for (int i = 0; i < N; i++) {
            assertEquals(i, (deltas[i] & 0xff));
        }
        try {
            a.deltas2Byte();
        } catch (AssertionError e)
        {}
        try {
            a.deltas4Byte();
        } catch (AssertionError e)
        {}
        try {
            a.deltas8Byte();
        } catch (AssertionError e)
        {}
    }

    @Test
    public void test2Byte()
    {
        final int N = 0xffff;
        final long X = 123L;
        CompressibleLongArray a = new CompressibleLongArray(N);
        for (int i = 0; i < N; i++) {
            a.append(X + i);
        }
        a.close();
        assertEquals(2, a.deltaBytes());
        try {
            a.deltas1Byte();
        } catch (AssertionError e)
        {}
        short[] deltas = a.deltas2Byte();
        for (int i = 0; i < N; i++) {
            assertEquals(i, (deltas[i] & 0xffff));
        }
        try {
            a.deltas4Byte();
        } catch (AssertionError e)
        {}
        try {
            a.deltas8Byte();
        } catch (AssertionError e)
        {}
    }

    @Test
    public void test4Byte()
    {
        CompressibleLongArray a = new CompressibleLongArray(32);
        long x = 1;
        for (int i = 0; i < 32; i++) {
            a.append(x);
            x <<= 1;
        }
        a.close();
        assertEquals(4, a.deltaBytes());
        try {
            a.deltas1Byte();
        } catch (AssertionError e)
        {}
        try {
            a.deltas2Byte();
        } catch (AssertionError e)
        {}
        x = 1;
        int[] deltas = a.deltas4Byte();
        for (int i = 0; i < 32; i++) {
            assertEquals(x, 1 + deltas[i] & 0xffffffffL);
            x <<= 1;
        }
        try {
            a.deltas8Byte();
        } catch (AssertionError e)
        {}
    }

    @Test
    public void test8Byte()
    {
        CompressibleLongArray a = new CompressibleLongArray(63);
        long x = 1;
        for (int i = 0; i < 63; i++) {
            a.append(x);
            x <<= 1;
        }
        a.close();
        assertEquals(8, a.deltaBytes());
        try {
            a.deltas1Byte();
        } catch (AssertionError e)
        {}
        try {
            a.deltas2Byte();
        } catch (AssertionError e)
        {}
        try {
            a.deltas4Byte();
        } catch (AssertionError e)
        {}
        x = 1;
        long[] deltas = a.deltas8Byte();
        for (int i = 0; i < 63; i++) {
            assertEquals(x, 1 + deltas[i]);
            x <<= 1;
        }
    }

    @Test
    public void testTransitions()
    {
        CompressibleLongArray a = new CompressibleLongArray(1);
        // Append 0
        a.append(0L);
        assertEquals(0L, a.min());
        assertEquals(0, a.deltaBytes());
        // Append 1 .. 0xff
        for (long x = 1; x <= 0xff; x++) {
            a.append(x);
            assertEquals(0L, a.min());
            assertEquals(1, a.deltaBytes());
        }
        // Append 0x100
        a.append(0x100L);
        assertEquals(0L, a.min());
        assertEquals(2, a.deltaBytes());
        // Append 0xffff
        a.append(0xffffL);
        assertEquals(0L, a.min());
        assertEquals(2, a.deltaBytes());
        // Append 0x10000
        a.append(0x10000L);
        assertEquals(0L, a.min());
        assertEquals(4, a.deltaBytes());
        // Append 0xffffffff
        a.append(0xffffffffL);
        assertEquals(0L, a.min());
        assertEquals(4, a.deltaBytes());
        // Append 0x100000000
        a.append(0x100000000L);
        assertEquals(0L, a.min());
        assertEquals(8, a.deltaBytes());
        // close and check final state
        a.close();
        assertEquals(0L, a.min());
        assertEquals(8, a.deltaBytes());
        long[] offsets = a.deltas8Byte();
        int i = 0;
        while (i <= 0xff) {
            assertEquals(i, offsets[i]);
            i++;
        }
        assertEquals(0x100L, offsets[i++]);
        assertEquals(0xffffL, offsets[i++]);
        assertEquals(0x10000L, offsets[i++]);
        assertEquals(0xffffffffL, offsets[i++]);
        assertEquals(0x100000000L, offsets[i++]);
        assertEquals(a.size(), i);
    }

    @Test
    public void testRemoval()
    {
        for (int nAppend = 1; nAppend <= 63; nAppend++) {
            for (int nRemove = 1; nRemove <= nAppend; nRemove++) {
                CompressibleLongArray a = new CompressibleLongArray(1);
                long mask = 0x1L;
                for (int i = 0; i < nAppend; i++) {
                    a.append(mask);
                    mask = mask << 1;
                }
                for (int i = 0; i < nRemove; i++) {
                    a.removeLast();
                }
                a.close();
                int n = nAppend - nRemove;
                if (n == 0) {
                    assertEquals(0, a.size());
                } else {
                    // The values in a should be 1 << 0, ..., 1 << (n - 1).
                    mask  = 0x1L;
                    for (int i = 0; i < n; i++) {
                        assertEquals(mask, a.at(i));
                        mask = mask << 1;
                    }
                    int deltaBytes = a.deltaBytes();
                    long maxDelta = a.at(n - 1) - a.at(0);
                    switch (deltaBytes) {
                        case 0:
                            assertEquals(1, n);
                            break;
                        case 1:
                            assertTrue(maxDelta <= 0xffL);
                            break;
                        case 2:
                            assertTrue(maxDelta > 0xffL);
                            assertTrue(maxDelta <= 0xffffL);
                            break;
                        case 4:
                            assertTrue(maxDelta > 0xffffL);
                            assertTrue(maxDelta <= 0xffffffffL);
                            break;
                        case 8:
                            assertTrue(maxDelta > 0xffffffffL);
                            break;
                        default:
                            fail();
                            break;
                    }
                }
            }
        }
    }
}
