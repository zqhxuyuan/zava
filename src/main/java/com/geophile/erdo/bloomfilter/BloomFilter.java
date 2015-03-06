/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.bloomfilter;

import com.geophile.erdo.memorymonitor.MemoryTracker;

import java.nio.ByteBuffer;

import static java.lang.Math.*;

/*
 * error rate      bits/key        hash functions
 * 
 * 0.0001          19.2            13
 * 0.0005          15.8            10
 * 0.0010          14.4            10
 * 0.0050          11.0             8
 * 0.0100           9.6             6
 * 0.0500           6.2             4
 * 
 */

public class BloomFilter implements MemoryTracker.Trackable<BloomFilter>
{
    // MemoryTracker.Trackable interface

    public long sizeBytes()
    {
        return map.length;
    }

    // BloomFilter interface

    public boolean maybePresent(int hashCode)
    {
        for (int h = 0; h < nHashes; h++) {
            int position = position(hashCode, h);
            if ((map[position / 8] & (1 << (position % 8))) == 0) {
                return false;
            }
        }
        return true;
    }

    public void add(int hashCode)
    {
        for (int h = 0; h < nHashes; h++) {
            int position = position(hashCode, h);
            map[position / 8] |= (1 << (position % 8));
        }
    }

    public void write(ByteBuffer buffer)
    {
        buffer.putInt(nBits);
        buffer.putInt(nHashes);
        buffer.putInt(map.length);
        buffer.put(map);
    }

    /**
     * @param nRecords  Number of records to accomodate.
     * @param errorRate Allowable false positive rate.
     */
    public BloomFilter(int nRecords, double errorRate)
    {
        // Formulas from http://en.wikipedia.org/wiki/Bloom_filter
        this.nBits = (int) round(-nRecords * log(errorRate) / pow(log(2), 2));
        this.nHashes = (int) round((nBits / nRecords) * log(2));
        this.map = new byte[(nBits + 7) / 8];
    }

    public static BloomFilter read(ByteBuffer buffer)
    {
        int nBits = buffer.getInt();
        int nHashes = buffer.getInt();
        int size = buffer.getInt();
        byte[] map = new byte[size];
        buffer.get(map);
        return new BloomFilter(nBits, nHashes, map);
    }

    private int position(int hash, int h)
    {
        hash *= Primes.PRIMES[h];
        if (hash < 0) {
            hash = -hash;
        }
        if (hash < 0) {
            // Integer.MIN_VALUE!
            hash = 0;
        }
        return hash % nBits;
    }

    private BloomFilter(int nBits,
                        int nHashes,
                        byte[] map)
    {
        this.nBits = nBits;
        this.nHashes = nHashes;
        this.map = map;
    }

    public static final boolean USE_BLOOM_FILTER = true;

    private final int nBits;
    private final int nHashes;
    private final byte[] map;
}
