/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap.tree;

import com.geophile.erdo.map.Factory;
import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.MapCursor;
import com.geophile.erdo.map.diskmap.DBStructure;
import com.geophile.erdo.map.diskmap.IndexRecord;
import com.geophile.erdo.map.mergescan.AbstractMultiRecord;
import com.geophile.erdo.map.mergescan.MultiRecordKey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class WriteableTree extends Tree
{
    // WriteableTree interface

    public int append(LazyRecord record) throws IOException, InterruptedException
    {
        int recordCount;
        if (record instanceof AbstractMultiRecord) {
            assert record instanceof LevelOneMultiRecord : record;
            recordCount = appendMultiRecord((LevelOneMultiRecord) record);
        } else {
            recordCount = appendSingleRecord(record);
        }
        return recordCount;
    }

    public Tree close() throws IOException, InterruptedException
    {
        finalizeRightEdge();
        return this;
    }

    public WriteableTreeLevel level(int levelNumber)
    {
        return (WriteableTreeLevel) super.level(levelNumber);
    }

    public WriteableTree(Factory factory, DBStructure dbStructure, long treeId)
    {
        super(factory, dbStructure, treeId);
    }

    // For use by this class

    private int appendSingleRecord(LazyRecord record) throws IOException, InterruptedException
    {
        WriteableTreeLevel leafLevel = level(0);
        if (previousAppendWasMultiRecord) {
            // Can't append to shared segment so start a new one.
            IndexRecord indexRecord = leafLevel.closeCurrentLeafSegment();
            assert indexRecord == null : indexRecord;
            leafLevel.startNewLeafSegment();
        }
        IndexRecord indexRecord = leafLevel.append(record);
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "{0}: append single record {1}", new Object[]{this, record});
        }
        if (indexRecord != null) {
            propagateUp(1, indexRecord);
        }
        previousAppendWasMultiRecord = false;
        return 1;
    }

    private int appendMultiRecord(LevelOneMultiRecord multiRecord) throws IOException, InterruptedException
    {
        if (LOG.isLoggable(Level.INFO)) {
            LOG.log(Level.INFO, "{0}: append multirecord {1}", new Object[]{this, multiRecord});
        }
        WriteableTreeLevel leafLevel = level(0);
        // A linked segment is its own segment, so ensure that the current one is closed.
        IndexRecord indexRecord = null;
        if (!previousAppendWasMultiRecord) {
            indexRecord = leafLevel.closeCurrentLeafSegment();
        }
        if (indexRecord != null) {
            propagateUp(1, indexRecord);
        }
        int linkedSegmentPageAddress = leafLevel.linkIn(multiRecord);
        // Append index records pointing to the pages of the linked leaf file. First, ensure that level 1 exists.
        if (levels.size() == 1) {
            levels.add(WriteableTreeLevel.create(this, 1));
        }
        MapCursor levelOneScan = multiRecord.levelOneScan();
        LazyRecord lazyIndexRecord;
        while ((lazyIndexRecord = levelOneScan.next()) != null) {
            indexRecord = (IndexRecord) lazyIndexRecord.materializeRecord();
            assert
                indexRecord.key().compareTo(((MultiRecordKey)multiRecord.key()).hi()) <= 0
                : String.format("multiRecord: %s, indexRecord: %s", multiRecord, indexRecord);
            indexRecord.childPageAddress(linkedSegmentPageAddress++);
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE,
                        "{0}: Propagating index record {1} from linked file page of {2}",
                        new Object[]{this, indexRecord, multiRecord});
            }
            propagateUp(1, indexRecord);
        }
        previousAppendWasMultiRecord = true;
        int recordsCovered = multiRecord.leafSegment().leafRecords();
        return recordsCovered;
    }

    private void propagateUp(int indexLevel, IndexRecord indexRecord)
        throws IOException, InterruptedException
    {
        assert indexLevel >= 1 : indexLevel;
        if (indexLevel == levels.size()) {
            // A page was just written to the root level. It could be the first page on that level, in which
            // case there's nothing to do. Or it could be the second page on that level, in which case we need
            // to create a new root level. It couldn't be the 3rd+ page on that level, since a root level is
            // no longer root after it gets its second page.
            TreeLevel rootLevel = levels.get(indexLevel - 1);
            assert rootLevel.segments() == 1 : rootLevel;
            TreeSegment rootSegment = rootLevel.segment(0);
            int pagesOnCurrentRootLevel = rootSegment.pages();
            if (pagesOnCurrentRootLevel == 1) {
                // Nothing to do
            } else if (pagesOnCurrentRootLevel == 2) {
                createNewRootLevel(indexRecord);
            } else {
                assert false : pagesOnCurrentRootLevel;
            }
        } else {
            IndexRecord newIndexRecord = level(indexLevel).append(indexRecord);
            if (newIndexRecord != null) {
                propagateUp(indexLevel + 1, newIndexRecord);
            }
        }
    }

    private void createNewRootLevel(IndexRecord indexRecord) throws IOException, InterruptedException
    {
        // Create new root level
        int indexLevel = levels.size();
        WriteableTreeLevel newRootLevel = WriteableTreeLevel.create(this, indexLevel);
        levels.add(newRootLevel);
        newRootLevel.append(indexRecord);
    }

    private void finalizeRightEdge() throws IOException, InterruptedException
    {
        if (!levels.isEmpty()) {
            List<IndexRecord> promoted = finalizeRightEdge(levels.size() - 1);
            TreeLevel rootLevel = levels.get(levels.size() - 1);
            assert rootLevel.segments() == 1 : rootLevel;
            TreeSegment rootSegment = rootLevel.segment(0);
            if (rootSegment.pages() == 1) {
                // There should be at most one promoted IndexRecord, resulting from the writing of
                // the root page.
                assert promoted.size() <= 1 : promoted;
            } else if (rootSegment.pages() > 1) {
                // Need to create a new root level
                createNewRootLevel(promoted.get(0));
                WriteableTreeLevel newRootLevel = level(levels.size() - 1);
                IndexRecord promotedFromNewRoot;
                for (int i = 1; i < promoted.size(); i++) {
                    promotedFromNewRoot = newRootLevel.append(promoted.get(i));
                    assert promotedFromNewRoot == null : promotedFromNewRoot;
                }
                newRootLevel.finalizeRightEdge();
            }
        }
    }

    private List<IndexRecord> finalizeRightEdge(int level) throws IOException, InterruptedException
    {
        List<IndexRecord> outgoing = new ArrayList<IndexRecord>(2);
        IndexRecord promoted;
        WriteableTreeLevel treeLevel = level(level);
        if (level > 0) {
            List<IndexRecord> incoming = finalizeRightEdge(level - 1);
            for (IndexRecord indexRecord : incoming) {
                promoted = treeLevel.append(indexRecord);
                if (promoted != null) {
                    outgoing.add(promoted);
                }
            }
        }
        promoted = treeLevel.finalizeRightEdge();
        if (promoted != null) {
            outgoing.add(promoted);
        }
        return outgoing;
    }

    // Object state

    private boolean previousAppendWasMultiRecord = false;
 }
