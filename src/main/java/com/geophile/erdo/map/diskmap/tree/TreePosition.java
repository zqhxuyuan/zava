/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap.tree;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.AbstractRecord;
import com.geophile.erdo.apiimpl.CursorImpl;
import com.geophile.erdo.apiimpl.TreePositionTracker;
import com.geophile.erdo.immutableitemcache.ImmutableItemManager;
import com.geophile.erdo.map.Factory;
import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.diskmap.DiskPage;
import com.geophile.erdo.map.diskmap.DiskPageCache;
import com.geophile.erdo.map.diskmap.PageId;
import com.geophile.erdo.util.IdGenerator;
import com.geophile.erdo.util.ThrowableUtil;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

// A TreePosition represents a position in a tree, at all levels of resolution between tree and record.

public class TreePosition
    extends LazyRecord
    implements ImmutableItemManager<PageId, DiskPage>
{
    // Object interface

    @Override
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append('T');
        if (tree == null) {
            buffer.append('?');
        } else {
            buffer.append(tree.treeId());
            if (level != null) {
                buffer.append("/L");
                buffer.append(level.levelNumber());
                if (atEnd) {
                    buffer.append("/END");
                } else if (segment != null) {
                    buffer.append("/S");
                    buffer.append(segment.segmentNumber());
                    buffer.append('(');
                    buffer.append(segment.segmentId());
                    buffer.append(')');
                    if (pageNumber != UNDEFINED) {
                        buffer.append("/P");
                        buffer.append(pageNumber);
                        if (recordNumber != UNDEFINED) {
                            buffer.append("/R");
                            buffer.append(recordNumber);
                        }
                    }
                }
            }
        }
        buffer.append('[');
        buffer.append(id);
        buffer.append(']');
        return buffer.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        boolean equals = false;
        if (o != null && o.getClass() == this.getClass()) {
            TreePosition that = (TreePosition) o;
            // If this and that are resolved to different units (e.g. page and record), then
            // comparisons for the smaller unit will lead to a result of false.
            equals =
                this.tree == that.tree &&
                this.level == that.level &&
                this.segment == that.segment &&
                this.pageNumber == that.pageNumber &&
                // At the end of a page, recordNumber could be a real record number, or LAST_RECORD_ON_PAGE
                (this.recordNumber == that.recordNumber || this.isLastRecordOnPage() && that.isLastRecordOnPage());
        }
        return equals;
    }

    @Override
    public int hashCode()
    {
        throw new UnsupportedOperationException();
    }

    // LazyRecord interface

    @Override
    public AbstractKey key() throws IOException, InterruptedException
    {
        assert inUse : this;
        checkResolvedToRecord();
        // TODO: Don't call readKey unnecessarily, use key if it has the correct value.
        key = ensurePage().readKey(recordNumber, pageAccessBuffers);
        return key;
    }

    @Override
    public ByteBuffer keyBuffer() throws IOException, InterruptedException
    {
        assert inUse : this;
        checkResolvedToRecord();
        ensurePage();
        return pageAccessBuffers.keyBuffer();
    }

    public AbstractRecord materializeRecord() throws IOException, InterruptedException
    {
        assert inUse : this;
        checkResolvedToRecord();
        record = ensurePage().readRecord(recordNumber, pageAccessBuffers);
        return record;
    }

    @Override
    public ByteBuffer recordBuffer() throws IOException, InterruptedException
    {
        assert inUse : this;
        checkResolvedToRecord();
        ensurePage();
        return pageAccessBuffers.recordBuffer();
    }

    @Override
    public long estimatedSizeBytes() throws IOException, InterruptedException
    {
        assert inUse : this;
        return recordBuffer().remaining() + keyBuffer().remaining();
    }

    @Override
    public boolean prefersSerialized()
    {
        assert inUse : this;
        return true;
    }

    // Don't need to override destroyRecordReference, to call deactivateForPool. super.destroyRecordReference
    // returns the TreePosition to its pool, and AbstractPool.returnResource calls deactivateForPool.

    // ImmutableItemManager interface

    @Override
    public DiskPage getItemForCache(PageId id) throws IOException, InterruptedException
    {
        assert inUse : this;
        checkResolvedToPage();
        File file = tree.dbStructure().segmentFile(segment.segmentId());
        long offset = ((long) pageNumber) * tree.pageSizeBytes();
        ByteBuffer pageBuffer = factory.pageMemoryManager().takePageBuffer();
        factory.segmentFileManager().readPage(file, offset, pageBuffer);
        return new DiskPage(factory,
                            id,
                            tree.pageAddress(segment.segmentNumber(), pageNumber),
                            level.levelNumber(),
                            pageBuffer);
    }

    @Override
    public void cleanupItemEvictedFromCache(DiskPage item)
    {
        factory.pageMemoryManager().returnPageBuffer(item.pageBuffer());
    }

    // TreePosition interface - access

    public Tree tree()
    {
        assert inUse : this;
        return tree;
    }

    public TreeLevel level()
    {
        assert inUse : this;
        return level;
    }

    public TreeSegment segment()
    {
        assert inUse : this;
        return segment;
    }

    public DiskPage page() throws IOException, InterruptedException
    {
        assert inUse : this;
        checkResolvedToPage();
        return ensurePage();
    }

    public int pageAddress()
    {
        assert inUse : this;
        checkResolvedToPage();
        return tree.pageAddress(segment.segmentNumber(), pageNumber);
    }

    // TreePosition interface - setting absolute position

    public TreePosition level(int levelNumber)
    {
        assert inUse : this;
        level = tree.level(levelNumber);
        segment = null;
        pageNumber = UNDEFINED;
        page(null);
        recordNumber = UNDEFINED;
        clearCachedRecord();
        return this;
    }

    public TreePosition goToFirstSegmentOfLevel()
    {
        assert inUse : this;
        checkResolvedToLevel();
        segment = level.segment(0);
        return this;
    }

    public TreePosition goToLastSegmentOfLevel()
    {
        assert inUse : this;
        checkResolvedToLevel();
        segment = level.segment(level.segments() - 1);
        return this;
    }

    public TreePosition goToFirstPageOfSegment()
    {
        assert inUse : this;
        checkResolvedToSegment();
        pageNumber = 0;
        randomRead = true;
        // TODO: Assert segment has at least one page
        return this;
    }

    public TreePosition goToLastPageOfSegment()
    {
        assert inUse : this;
        checkResolvedToSegment();
        pageNumber = segment.pages() - 1;
        randomRead = true;
        // TODO: Assert segment has at least one page
        return this;
    }

    public TreePosition goToFirstRecordOfPage() throws IOException, InterruptedException
    {
        assert inUse : this;
        checkResolvedToPage();
        ensurePage();
        // TODO: Assert page has at least one record
        recordNumber = 0;
        clearCachedRecord();
        return this;
    }

    public TreePosition goToLastRecordOfPage() throws IOException, InterruptedException
    {
        assert inUse : this;
        checkResolvedToPage();
        ensurePage();
        // TODO: Assert page has at least one record
        recordNumber = page.nRecords() - 1;
        clearCachedRecord();
        return this;
    }

    public TreePosition pageAddress(int pageAddress)
    {
        assert inUse : this;
        checkResolvedToLevel();
        this.segment = level.segment(tree.segmentNumber(pageAddress));
        this.pageNumber = tree.pageNumber(pageAddress);
        this.page(null);
        this.randomRead = true;
        this.recordNumber = UNDEFINED;
        clearCachedRecord();
        return this;
    }

    public TreePosition recordNumber(int newRecordNumber)
    {
        assert inUse : this;
        checkResolvedToPage();
        assert page != null : this;
        assert newRecordNumber >= 0 : newRecordNumber;
        assert newRecordNumber < page.nRecords() : newRecordNumber;
        recordNumber = newRecordNumber;
        clearCachedRecord();
        return this;
    }

    // TreePosition interface - setting relative position

    public TreePosition goToEnd()
    {
        assert inUse : this;
        if (LOG.isLoggable(Level.INFO)) {
            LOG.log(Level.INFO, "{0}: atEnd = true", name());
        }
        atEnd = true;
        segment = null;
        pageNumber = UNDEFINED;
        page(null);
        randomRead = true;
        recordNumber = UNDEFINED;
        clearCachedRecord();
        return this;
    }

    public TreePosition goToNextSegment()
    {
        assert inUse : this;
        checkResolvedToSegment();
        if (segment.segmentNumber() == level.segments() - 1) {
            atEnd = true;
            segment = null;
        } else {
            segment = level.segment(segment.segmentNumber() + 1);
        }
        pageNumber = UNDEFINED;
        page(null);
        randomRead = true;
        recordNumber = UNDEFINED;
        clearCachedRecord();
        return this;
    }

    public TreePosition goToPreviousSegment()
    {
        assert inUse : this;
        checkResolvedToSegment();
        if (segment.segmentNumber() == 0) {
            atEnd = true;
            segment = null;
        } else {
            segment = level.segment(segment.segmentNumber() - 1);
        }
        pageNumber = UNDEFINED;
        page(null);
        randomRead = true;
        recordNumber = UNDEFINED;
        clearCachedRecord();
        return this;
    }

    public TreePosition goToNextPage()
    {
        assert inUse : this;
        checkResolvedToPage();
        if (++pageNumber < segment.pages()) {
            page(null);
            randomRead = false;
            recordNumber = UNDEFINED;
        } else {
            goToNextSegment();
            pageNumber = segment == null ? UNDEFINED : 0;
        }
        clearCachedRecord();
        return this;
    }

    public TreePosition goToPreviousPage()
    {
        assert inUse : this;
        checkResolvedToPage();
        if (--pageNumber >= 0) {
            page(null);
            randomRead = false;
            recordNumber = UNDEFINED;
        } else {
            goToPreviousSegment();
            pageNumber = segment == null ? UNDEFINED : segment.pages() - 1;
        }
        clearCachedRecord();
        return this;
    }

    public TreePosition goToNextRecord() throws IOException, InterruptedException
    {
        assert inUse : this;
        checkResolvedToRecord();
        ensurePage();
        if (++recordNumber == page.nRecords()) {
            goToNextPage();
            recordNumber = 0;
        }
        clearCachedRecord();
        return this;
    }

    public TreePosition goToPreviousRecord() throws IOException, InterruptedException
    {
        assert inUse : this;
        checkResolvedToRecord();
        ensurePage();
        if (recordNumber-- == 0) {
            goToPreviousPage();
            recordNumber = LAST_RECORD_ON_PAGE;
        }
        clearCachedRecord();
        return this;
    }

    public boolean atEnd()
    {
        assert inUse : this;
        return atEnd;
    }

    // TreePosition interface - lifecycle

    public TreePosition copy()
    {
        assert inUse : this;
        TreePosition copy = (TreePosition) pool.takeResource();
        copyTo(copy);
        return copy;
    }

    public void initialize(Tree tree)
    {
        assert inUse : this;
        if (LOG.isLoggable(Level.INFO)) {
            LOG.log(Level.INFO, "{0}: initialize for {1}", new Object[]{name(), tree});
        }
        this.tree = tree;
        factory = tree.factory();
        diskPageCache = factory.diskPageCache();
        atEnd = false;
        level = null;
        segment = null;
        pageNumber = UNDEFINED;
        page(null);
        pageAccessBuffers = null;
        randomRead = false;
        recordNumber = UNDEFINED;
        record = null;
        key = null;
    }

    // For use by this package

    void copyTo(TreePosition that)
    {
        assert inUse : this;
        assert that.inUse : that;
        if (LOG.isLoggable(Level.INFO)) {
            LOG.log(Level.INFO, "{0}: copy -> {1}", new Object[]{that.name(), that});
        }
        that.tree = this.tree;
        that.factory = this.factory;
        that.diskPageCache = this.diskPageCache;
        that.atEnd = this.atEnd;
        that.level = this.level;
        that.segment = this.segment;
        that.pageNumber = this.pageNumber;
        that.page(this.page); // creates new access buffers
        that.randomRead = this.randomRead;
        that.recordNumber = this.recordNumber;
        that.record = this.record;
        that.key = this.key;
    }

    void activateForPool()
    {
        assert !inUse : this;
        if (LOG.isLoggable(Level.INFO)) {
            LOG.log(Level.INFO, "{0}: activateForPool", name());
        }
        TreePositionTracker.registerTreePosition(CursorImpl.threadContext(), this);
        inUse = true;
        if (RECORD_TREE_POSITION_STACKS) {
            lastActivation = ThrowableUtil.toString(new Exception());
        }
    }

    void deactivateForPool()
    {
        assert inUse : this;
        if (LOG.isLoggable(Level.INFO)) {
            LOG.log(Level.INFO, "{0}: deactivateForPool", name());
        }
        TreePositionTracker.unregisterTreePosition(CursorImpl.threadContext(), this);
        tree = null;
        factory = null;
        diskPageCache = null;
        atEnd = false;
        level = null;
        segment = null;
        pageNumber = UNDEFINED;
        page(null);
        pageAccessBuffers = null;
        randomRead = false;
        recordNumber = UNDEFINED;
        record = null;
        key = null;
        inUse = false;
        if (RECORD_TREE_POSITION_STACKS) {
            lastDeactivation = ThrowableUtil.toString(new Exception());
        }
    }

    TreePosition(TreePositionPool pool)
    {
        super(pool);
    }

    // For use by this class

    private DiskPage ensurePage() throws IOException, InterruptedException
    {
        if (page == null || page.pageAddress() != this.pageAddress()) {
            page(diskPageCache.page(segment, pageNumber, this));
            if (recordNumber == LAST_RECORD_ON_PAGE) {
                recordNumber = page.nRecords() - 1;
            }
            if (recordNumber != UNDEFINED) {
                pageAccessBuffers = page.positionAccessBuffers(recordNumber, pageAccessBuffers);
            }
        }
        return page;
    }

    private void page(DiskPage newPage)
    {
        if (page != null) {
            page.removeReference();
        }
        if (newPage == null) {
            page = null;
            pageAccessBuffers = null;
        } else {
            newPage.addReference(randomRead);
            page = newPage;
            pageAccessBuffers = page.accessBuffers();
        }
    }

    private boolean isLastRecordOnPage()
    {
        return
            page == null && recordNumber == LAST_RECORD_ON_PAGE ||
            page != null && recordNumber == page.nRecords() - 1;
    }

    private void checkResolvedToLevel()
    {
        assert !atEnd && level != null : this;
    }

    private void checkResolvedToSegment()
    {
        assert !atEnd && segment != null : this;
    }

    private void checkResolvedToPage()
    {
        assert !atEnd && pageNumber != UNDEFINED : this;
    }

    private void checkResolvedToRecord()
    {
        assert !atEnd && recordNumber != UNDEFINED : this;
    }

    private void clearCachedRecord()
    {
        record = null;
        key = null;
    }

    private String name()
    {
        return String.format("TP[%s]", id);
    }

    // Class state

    private static final int UNDEFINED = -1;
    private static final int LAST_RECORD_ON_PAGE = Integer.MAX_VALUE;
    private static final IdGenerator idGenerator = new IdGenerator(0);
    private static final Logger LOG = Logger.getLogger(TreePosition.class.getName());
    private static final boolean RECORD_TREE_POSITION_STACKS = Boolean.getBoolean("recordTreePositionStacks");

    // Object state

    // global stuff
    private Factory factory;
    private DiskPageCache diskPageCache;

    // termination
    private boolean atEnd = false;

    // tree
    private Tree tree;

    // level
    private TreeLevel level;

    // segment
    private TreeSegment segment;

    // page
    private int pageNumber;
    private DiskPage page;
    private DiskPage.AccessBuffers pageAccessBuffers;
    private boolean randomRead; // Indicates whether the way in which pageNumber was set will result in a random read.

    // record
    private int recordNumber;
    private AbstractRecord record;
    private AbstractKey key;

    // identification (to help interpret logs)
    private final long id = idGenerator.nextId();

    // Pooling
    private boolean inUse = false;

    // Debugging
    private String lastActivation;
    private String lastDeactivation;
}
