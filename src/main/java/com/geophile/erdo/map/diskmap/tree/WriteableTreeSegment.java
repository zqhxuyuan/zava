/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap.tree;

import com.geophile.erdo.Configuration;
import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.diskmap.DiskPage;
import com.geophile.erdo.map.diskmap.IndexRecord;
import com.geophile.erdo.map.diskmap.PageId;
import com.geophile.erdo.segmentfilemanager.AbstractSegmentFileManager;
import com.geophile.erdo.util.AbstractPool;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

class WriteableTreeSegment extends TreeSegment
{
    public IndexRecord append(LazyRecord record)
        throws IOException, InterruptedException
    {
        IndexRecord indexRecord = null;
        boolean appended = page.append(record);
        if (!appended) {
            closePage();
            indexRecord = new IndexRecord(page.firstKey(), page.pageAddress());
            if (lastPageOfSegment()) {
                closeSegment(); // Drops page
                // Can't start a new page to hold the record being appended because the segment is full.
                // The caller will detect this and put the record in a new segment.
            } else {
                // Start a new page and put the record being appended into it.
                createNewPage();
                appended = page.append(record);
                assert appended : record;
            }
        }
        if (appended && summary != null) {
            summary.append(record.key());
        }
        return indexRecord;
    }

    public IndexRecord finalizeRightEdge() throws IOException, InterruptedException
    {
        IndexRecord indexRecord = null;
        if (closePage()) {
            indexRecord = new IndexRecord(page.firstKey(), page.pageAddress());
        }
        closeSegment(); // Drops page
        return indexRecord;
    }

    @Override
    public void checkClosed()
    {
        assert segmentBuffer == null : this;
        assert erdoIdsBuffer == null : this;
        assert timestampsBuffer == null : this;
        assert keysBuffer == null : this;
        assert recordsBuffer == null : this;
        assert zeros == null : this;
    }

    public WriteableTreeSegment(TreeLevel level, int segmentNumber, long segmentId)
    {
        super(level, segmentNumber, segmentId);
        treeCreationBuffers = buffers();
        Configuration configuration = level.tree().factory().configuration();
        pageSizeBytes = configuration.diskPageSizeBytes();
        segmentBuffer = treeCreationBuffers.segmentBuffer();
        erdoIdsBuffer = treeCreationBuffers.erdoIdsBuffer();
        timestampsBuffer = treeCreationBuffers.timestampsBuffer();
        keysBuffer = treeCreationBuffers.keysBuffer();
        recordsBuffer = treeCreationBuffers.recordsBuffer();
        zeros = treeCreationBuffers.zeros();
        segmentBuffer.clear();
        createNewPage();
    }

    boolean isLastPage(int pageAddress)
    {
        return level.tree().pageNumber(pageAddress) == maxPagesInSegment - 1;
    }

    // For use by this class

    private void createNewPage()
    {
        erdoIdsBuffer.clear();
        if (timestampsBuffer != null) {
            timestampsBuffer.clear();
        }
        keysBuffer.clear();
        recordsBuffer.clear();
        Tree tree = level.tree();
        page = new DiskPage(tree.factory(),
                            new PageId(segmentId, pageCount),
                            level.levelNumber(),
                            tree.pageAddress(segmentNumber, pageCount),
                            erdoIdsBuffer,
                            level.isLeaf() ? timestampsBuffer : null,
                            keysBuffer,
                            recordsBuffer);
        pageCount++;
    }

    private boolean closePage()
    {
        boolean empty = true;
        if (page != null) {
            if (page.nRecords() > 0) {
                page.close();
                empty = false;
            }
            // flip buffers
            erdoIdsBuffer.flip();
            if (timestampsBuffer != null) {
                timestampsBuffer.flip();
            }
            keysBuffer.flip();
            recordsBuffer.flip();
            // compute occupied
            int occupied = erdoIdsBuffer.limit() + keysBuffer.limit() + recordsBuffer.limit();
            if (timestampsBuffer != null) {
                occupied += timestampsBuffer.limit();
            }
            assert occupied <= pageSizeBytes : occupied;
            // copy to segment buffer
            segmentBuffer.put(erdoIdsBuffer);
            if (timestampsBuffer != null) {
                segmentBuffer.put(timestampsBuffer);
            }
            segmentBuffer.put(keysBuffer);
            segmentBuffer.put(recordsBuffer);
            segmentBuffer.put(zeros, 0, pageSizeBytes - occupied);
        }
        return !empty;
    }

    private void closeSegment() throws IOException, InterruptedException
    {
        if (!empty()) {
            writeSegmentFile();
            if (level.isLeaf()) {
                summary.write();
            }
        }
        BUFFERS_POOL.get().returnResource(treeCreationBuffers);
        page = null;
        treeCreationBuffers = null;
        segmentBuffer = null;
        recordsBuffer = null;
        keysBuffer = null;
        erdoIdsBuffer = null;
        timestampsBuffer = null;
        zeros = null;
    }

    private boolean lastPageOfSegment()
    {
        return pageCount == maxPagesInSegment;
    }

    private boolean empty()
    {
        return pageCount == 1 && page != null && page.nRecords() == 0;
    }

    private void writeSegmentFile() throws IOException, InterruptedException
    {
        segmentBuffer.flip();
        if (segmentBuffer.limit() > 0) {
            Tree tree = level.tree();
            AbstractSegmentFileManager segmentFileManager = tree.factory().segmentFileManager();
            File file = tree.dbStructure().segmentFile(segmentId);
            segmentFileManager.create(file, tree.treeId(), segmentId);
            segmentFileManager.write(file, 0, segmentBuffer);
        }
    }

    private TreeCreationBuffers buffers()
    {
        TreeCreationBuffersPool buffersPool = BUFFERS_POOL.get();
        if (buffersPool == null) {
            buffersPool = new TreeCreationBuffersPool(treeLevel().tree().factory().configuration());
            BUFFERS_POOL.set(buffersPool);
        }
        return buffersPool.takeResource();
    }

    // Class state

    // A pool of tree creation buffers is needed per thread. While a thread writes only one tree at a time, a
    // tree may have multiple levels, and a set of buffers is needed for each level.
    private static final ThreadLocal<TreeCreationBuffersPool> BUFFERS_POOL = new ThreadLocal<>();
    private static final AtomicInteger BUFFERS_COUNTER = new AtomicInteger(0);

    // Object state

    private DiskPage page;
    private final int pageSizeBytes;
    // Buffers: The segmentBuffer accumulates pages as they are closed, so that the whole segment can be written
    // in one operation. timestampsBuffer, keysBuffer and recordsBuffer are used by each DiskPage, and then when
    // the page is closed, copied to the segmentBuffer. zeros is used to supply zeros when filling out the unused part
    // of a page.
    private TreeCreationBuffers treeCreationBuffers;
    private ByteBuffer segmentBuffer;
    private ByteBuffer erdoIdsBuffer;
    private ByteBuffer timestampsBuffer;
    private ByteBuffer keysBuffer;
    private ByteBuffer recordsBuffer;
    private byte[] zeros;

    // Inner classes

    private class TreeCreationBuffersPool extends AbstractPool<TreeCreationBuffers>
    {
        @Override
        public TreeCreationBuffers newResource()
        {
            return new TreeCreationBuffers(configuration, BUFFERS_COUNTER.getAndIncrement());
        }

        @Override
        public void activate(TreeCreationBuffers buffers)
        {
            buffers.markInUse();
        }

        @Override
        public void deactivate(TreeCreationBuffers buffers)
        {
            buffers.markFree();
        }

        TreeCreationBuffersPool(Configuration configuration)
        {
            this.configuration = configuration;
        }

        private final Configuration configuration;
    }
}
