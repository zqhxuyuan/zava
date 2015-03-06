/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap.tree;

import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.MapCursor;
import com.geophile.erdo.map.diskmap.IndexRecord;

import java.io.IOException;

class LevelOneCursorToFindLevelZeroSegments extends MapCursor
{
    // MapCursor interface

    @Override
    public LazyRecord next() throws IOException, InterruptedException
    {
        LevelOneMultiRecord multiRecord = null;
        if (hi != null) {
            IndexRecord lo = hi;
            IndexRecord hiPredecessor;
            do {
                levelOnePosition.goToNextRecord();
                hiPredecessor = hi;
                hi = levelOnePosition.atEnd() ? null : currentLevelOneRecord();
            } while (hi != null && levelZeroSegmentNumber(lo) == levelZeroSegmentNumber(hi));
            multiRecord = new LevelOneMultiRecord(runStart, lo, levelOnePosition, hiPredecessor);
            levelOnePosition.copyTo(runStart); // Get ready for next level-0 file
        }
        return multiRecord;
    }

    @Override
    public LazyRecord previous() throws IOException, InterruptedException
    {
        // LevelOneCursorToFindLevelZeroSegments is used only in a consolidation scan, which doesn't need to
        // run backward.
        assert false;
        return null;
    }

    @Override
    public void close()
    {
        if (!closed) {
            runStart.destroyRecordReference();
            levelOnePosition.destroyRecordReference();
            closed = true;
        }
    }

    // TreeLevelOneScanToFindLevelZeroFiles interface

    LevelOneCursorToFindLevelZeroSegments(Tree tree) throws IOException, InterruptedException
    {
        super(null, false);
        this.tree = tree;
        this.levelOnePosition =
            tree.newPosition().level(1).goToFirstSegmentOfLevel().goToFirstPageOfSegment().goToFirstRecordOfPage();
        this.runStart = this.levelOnePosition.copy();
        this.hi = currentLevelOneRecord();
        assert this.hi != null : tree;
    }

    // For use by this class

    private IndexRecord currentLevelOneRecord() throws IOException, InterruptedException
    {
        return (IndexRecord) levelOnePosition.materializeRecord();
    }

    private int levelZeroSegmentNumber(IndexRecord indexRecord) throws IOException, InterruptedException
    {
        return tree.segmentNumber(indexRecord.childPageAddress());
    }

    // Object state

    private final Tree tree;
    private final TreePosition levelOnePosition;
    private final TreePosition runStart; // Points to first level one IndexRecord for a level zero segment.
    private IndexRecord hi;
    private boolean closed = false;
}
