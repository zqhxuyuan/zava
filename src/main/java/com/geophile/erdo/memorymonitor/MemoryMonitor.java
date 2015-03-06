/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.memorymonitor;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MemoryMonitor
{
    public void track(int id, long oldSizeBytes, long newSizeBytes)
    {
        records[id].track(oldSizeBytes, newSizeBytes);
    }

    public void log(Logger log, Level level)
    {
        log.log(level, "KeyArray.erdoIds: {0}", records[KEY_ARRAY_ERDO_IDS]);
        log.log(level, "KeyArray.timestamps: {0}", records[KEY_ARRAY_TIMESTAMPS]);
        log.log(level, "KeyArray.keys: {0}", records[KEY_ARRAY_KEYS]);
        log.log(level, "TreeSegment.bloomFilter: {0}", records[TREE_SEGMENT_FILTER]);
        log.log(level, "TreeSegment.keyHashes: {0}", records[TREE_SEGMENT_KEY_HASHES]);
        log.log(level, "CacheFileSystem: {0}", records[PAGE_CACHE]);
    }

    public static final int KEY_ARRAY_ERDO_IDS = 0;      // KeyArray.erdoIds IntArray
    public static final int KEY_ARRAY_TIMESTAMPS = 1;    // KeyArray.timestamps CompressibleLongArray
    public static final int KEY_ARRAY_KEYS = 2;          // KeyArray.array PackedArray
    public static final int TREE_SEGMENT_FILTER = 3;     // TreeSegment.bloomFilter BloomFilter
    public static final int TREE_SEGMENT_KEY_HASHES = 4; // WriteableTreeSegment.keyHashes IntArray
    public static final int PAGE_CACHE = 5;              // CacheFileSystem

    private static final int RECORDS = 6;

    private final Record[] records = new Record[RECORDS];

    {
        for (int r = 0; r < RECORDS; r++) {
            records[r] = new Record();
        }
    }

    public static class Record
    {
        public synchronized String toString()
        {
            return String.format("(%s, %s)", count, sizeBytes);
        }

        synchronized void track(long oldSizeBytes, long newSizeBytes)
        {
            if (oldSizeBytes > 0 && newSizeBytes > 0) {
                sizeBytes += newSizeBytes - oldSizeBytes;
            } else if (newSizeBytes > 0) {
                // oldSizeBytes = 0
                count++;
                sizeBytes += newSizeBytes;
            } else if (oldSizeBytes > 0) {
                // oldSizeBytes > 0, newSizeBytes = 0
                count--;
                sizeBytes -= oldSizeBytes;
            }
            assert count >= 0;
            assert sizeBytes >= 0;
        }

        Record()
        {}

        private Record(int count, long sizeBytes)
        {
            this.count = count;
            this.sizeBytes = sizeBytes;
        }

        private int count = 0;
        private long sizeBytes = 0;
    }
}
