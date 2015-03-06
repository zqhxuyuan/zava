/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap.tree;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.bloomfilter.BloomFilter;
import com.geophile.erdo.memorymonitor.MemoryMonitor;
import com.geophile.erdo.memorymonitor.MemoryTracker;
import com.geophile.erdo.util.FileUtil;
import com.geophile.erdo.util.IntArray;

import java.io.File;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

/*
 * Summary file format:
 * - erdoId of last key of segment (int)
 * - last key of segment
 * - number of records in segment (int)
 * - bloom filter
 */

class TreeSegmentSummary
{
    public void append(AbstractKey key)
    {
        if (BloomFilter.USE_BLOOM_FILTER) {
            if (keyHashes.object() == null) {
                keyHashes.object(new IntArray(keyHashes));
            }
            keyHashes.object().append(key.hashCode());
        }
        lastKey = key;
        keyCount++;
    }

    public int keyCount()
    {
        return keyCount;
    }

    public AbstractKey lastKey()
    {
        return lastKey;
    }

    public boolean maybePresent(AbstractKey key)
    {
        return bloomFilter.object().maybePresent(key.hashCode());
    }

    public void read() throws IOException, InterruptedException
    {
        Tree tree = treeSegment.treeLevel().tree();
        File summaryFile = tree.dbStructure().summaryFile(treeSegment.segmentId());
        ByteBuffer buffer = FileUtil.readFile(summaryFile);
        // erdo id of last key of segment
        int erdoId = buffer.getInt();
        // last key of segment
        lastKey = AbstractKey.deserialize(tree.factory(), buffer, erdoId);
        lastKey.erdoId(erdoId);
        // number of records in segment
        keyCount = buffer.getInt();
        // bloom filter
        if (BloomFilter.USE_BLOOM_FILTER) {
            bloomFilter.object(BloomFilter.read(buffer));
        }
    }

    public void write() throws IOException, InterruptedException
    {
        Tree tree = treeSegment.treeLevel().tree();
        File summaryFile = tree.dbStructure().summaryFile(treeSegment.segmentId());
        ByteBuffer buffer = ByteBuffer.allocate(INITIAL_SUMMARY_FILE_BUFFER_SIZE);
        boolean serialized = false;
        while (!serialized) {
            try {
                // erdo id of last key of segment
                buffer.putInt(lastKey.erdoId());
                // last key of segment
                lastKey.writeTo(buffer);
                // number of records in segment
                buffer.putInt(keyCount);
                // bloom filter
                if (BloomFilter.USE_BLOOM_FILTER) {
                    loadBloomFilter();
                    bloomFilter.object().write(buffer);
                }
                // Prepare to write
                buffer.flip();
                serialized = true;
            } catch (BufferOverflowException e) {
                assert buffer.capacity() < MAX_SUMMARY_FILE_BUFFER_SIZE : this;
                int newCapacity = Math.min(10 * buffer.capacity(), MAX_SUMMARY_FILE_BUFFER_SIZE);
                buffer = ByteBuffer.allocate(newCapacity);
            }
        }
        FileUtil.writeFile(summaryFile, buffer);
        keyHashes.object(null);
    }

    public TreeSegmentSummary(TreeSegment treeSegment)
    {
        this.treeSegment = treeSegment;
        MemoryMonitor memoryMonitor = memoryMonitor(treeSegment);
        if (BloomFilter.USE_BLOOM_FILTER) {
            keyHashes = new MemoryTracker<>(memoryMonitor, MemoryMonitor.TREE_SEGMENT_KEY_HASHES);
            bloomFilter = new MemoryTracker<>(memoryMonitor, MemoryMonitor.TREE_SEGMENT_FILTER);
        } else {
            keyHashes = null;
            bloomFilter = null;
        }
    }

    private void loadBloomFilter()
    {
        if (BloomFilter.USE_BLOOM_FILTER) {
            IntArray hashes = keyHashes.object();
            assert hashes.size() == keyCount : this;
            double errorRate = treeSegment.treeLevel().tree().factory().configuration().keysBloomFilterErrorRate();
            BloomFilter filter = new BloomFilter(keyCount, errorRate);
            for (int i = 0; i < keyCount; i++) {
                filter.add(hashes.at(i));
            }
            bloomFilter.object(filter);
        }
    }

    private static MemoryMonitor memoryMonitor(TreeSegment treeSegment)
    {
        return treeSegment.treeLevel().tree().factory().memoryMonitor();
    }

    private static final int INITIAL_SUMMARY_FILE_BUFFER_SIZE = 10000;
    private static final int MAX_SUMMARY_FILE_BUFFER_SIZE = 5000000;

    private final TreeSegment treeSegment;
    private final MemoryTracker<IntArray> keyHashes;
    private final MemoryTracker<BloomFilter> bloomFilter;
    private AbstractKey lastKey;
    private int keyCount;
}
