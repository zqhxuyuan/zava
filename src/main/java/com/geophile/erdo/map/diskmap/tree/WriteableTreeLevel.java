/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap.tree;

import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.diskmap.IndexRecord;
import com.geophile.erdo.segmentfilemanager.AbstractSegmentFileManager;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

class WriteableTreeLevel extends TreeLevel
{
    IndexRecord append(LazyRecord record) throws IOException, InterruptedException
    {
        WriteableTreeSegment lastSegment = (WriteableTreeSegment) lastSegment();
        IndexRecord indexRecord = lastSegment.append(record);
        if (indexRecord != null && lastSegment.isLastPage(indexRecord.childPageAddress())) {
            lastSegment = newSegment();
            segments.add(lastSegment);
            IndexRecord newSegmentIndexRecord = lastSegment.append(record);
            assert newSegmentIndexRecord == null : record;
        }
        return indexRecord;
    }

    public IndexRecord finalizeRightEdge() throws IOException, InterruptedException
    {
        return finalizeLastSegment();
    }

    public IndexRecord closeCurrentLeafSegment() throws IOException, InterruptedException
    {
        assert level == 0 : this;
        return finalizeLastSegment();
    }

    public void startNewLeafSegment()
    {
        assert level == 0 : this;
        segments.add(newSegment());
    }

    public int linkIn(LevelOneMultiRecord multiRecord)
    {
        LOG.log(Level.INFO, "{0}: Linking in {1}", new Object[]{this, multiRecord});
        TreeSegment lastSegment = super.lastSegment();
        TreeSegment sharedSegmentOriginal = multiRecord.leafSegment();
        int segmentNumber;
        if (lastSegment.leafRecords() == 0) {
            // We're linking in a segment, but the current last segment is empty.
            // Remove the last segment so that it will be replaced by the shared segment.
            segmentNumber = lastSegment.segmentNumber();
            segments.remove(segmentNumber);
        } else {
            segmentNumber = segments();
        }
        TreeSegment sharedSegmentCopy =
            TreeSegment.share(this, segmentNumber, sharedSegmentOriginal);
        segments.add(sharedSegmentCopy);
        AbstractSegmentFileManager segmentFileManager = tree.factory().segmentFileManager();
        long segmentId = sharedSegmentCopy.segmentId();
        long treeId = tree.treeId();
        segmentFileManager.register(tree.dbStructure().segmentFile(segmentId), treeId, segmentId);
        return tree.pageAddress(sharedSegmentCopy.segmentNumber(), 0);
    }

    public static WriteableTreeLevel create(Tree tree, int levelNumber)
    {
        WriteableTreeLevel level = new WriteableTreeLevel(tree, levelNumber);
        level.segments.add(level.newSegment());
        return level;
    }

    // For use by this package

    WriteableTreeSegment newSegment()
    {
        return new WriteableTreeSegment(this, segments.size(), tree.factory().segmentFileManager().newSegmentId());
    }

    // For use by this class

    private IndexRecord finalizeLastSegment() throws IOException, InterruptedException
    {
        TreeSegment lastSegment = lastSegment();
        return
            lastSegment.isOpen()
            ? ((WriteableTreeSegment) lastSegment).finalizeRightEdge()
            : null;
    }

    private WriteableTreeLevel(Tree tree, int level)
    {
        super(tree, level);
    }

    // Class state

    private static final Logger LOG = Logger.getLogger(WriteableTreeLevel.class.getName());
}
