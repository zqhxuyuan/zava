/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap.tree;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.bloomfilter.BloomFilter;
import com.geophile.erdo.map.diskmap.DBStructure;
import com.geophile.erdo.map.diskmap.Manifest;
import com.geophile.erdo.segmentfilemanager.AbstractSegmentFileManager;
import com.geophile.erdo.util.FileUtil;

import java.io.IOException;

public class TreeSegment
{
    // Object interface

    @Override
    public String toString()
    {
        return String.format("%s/S%s(%s)", level, segmentNumber, segmentId);
    }

    // TreeSegment interface

    public long segmentId()
    {
        return segmentId;
    }

    public int segmentNumber()
    {
        return segmentNumber;
    }

    public int pages()
    {
        return pageCount;
    }

    public int leafRecords()
    {
        assert level.isLeaf();
        return summary.keyCount();
    }

    public void destroy()
    {
        Tree tree = level.tree();
        AbstractSegmentFileManager segmentFileManager = tree.factory().segmentFileManager();
        boolean deleted =
            segmentFileManager.delete(tree.dbStructure().segmentFile(segmentId), tree.treeId(), segmentId);
        // deleted is false if the segment is linked to another tree
        if (deleted) {
            FileUtil.deleteFile(tree.dbStructure().summaryFile(segmentId));
        }
    }

    public static TreeSegment recover(TreeLevel level, int segmentNumber, Manifest manifest)
        throws IOException, InterruptedException
    {
        Tree tree = level.tree();
        AbstractSegmentFileManager segmentFileManager = tree.factory().segmentFileManager();
        DBStructure dbStructure = tree.dbStructure();
        long segmentId = manifest.segmentIds(level.levelNumber()).at(segmentNumber);
        TreeSegment segment = new TreeSegment(level, segmentNumber, segmentId);
        if (level.isLeaf()) {
            segment.summary.read();
            segmentFileManager.register(dbStructure.segmentFile(segmentId), tree.treeId(), segmentId);
        }
        segment.pageCount = (int) (tree.dbStructure().segmentFile(segmentId).length() / tree.pageSizeBytes());
        return segment;
    }

    public AbstractKey leafLastKey()
    {
        assert level.isLeaf();
        return summary.lastKey();
    }

    public boolean keyPossiblyPresent(AbstractKey key)
    {
        assert level.isLeaf();
        checkClosed();
        return !BloomFilter.USE_BLOOM_FILTER || summary.maybePresent(key);
    }

    public boolean isOpen()
    {
        return this instanceof WriteableTreeSegment;
    }

    public void checkClosed()
    {
    }

    public static TreeSegment share(TreeLevel level, int segmentNumber, TreeSegment original)
    {
        TreeSegment copy = new TreeSegment(level, segmentNumber, original.segmentId);
        copy.pageCount = original.pageCount;
        // Sharing the summary will share the bloom filter. Memory tracking will read too high, but let's not worry
        // about it.
        copy.summary = original.summary;
        return copy;
    }

    // For use by subclasses

    protected TreeSegment(TreeLevel level, int segmentNumber, long segmentId)
    {
        Tree tree = level.tree();
        this.level = level;
        this.segmentNumber = segmentNumber;
        this.segmentId = segmentId;
        this.maxPagesInSegment = (int) (tree.maxFileSizeBytes() / tree.pageSizeBytes());
        this.summary = level.isLeaf() ? new TreeSegmentSummary(this) : null;
    }

    // For use by this package

    final TreeLevel treeLevel()
    {
        return level;
    }

    final TreeSegmentSummary summary()
    {
        return summary;
    }

    // Object state

    protected final TreeLevel level;
    protected final int segmentNumber; // position within level
    protected final long segmentId; // unique within a database
    protected final int maxPagesInSegment;
    protected int pageCount = 0; // Counted as segment is created. Recovered from file size.
    protected TreeSegmentSummary summary; // for leaf segments only
}
