/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap.tree;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.UsageError;
import com.geophile.erdo.config.ConfigurationKeys;
import com.geophile.erdo.map.Factory;
import com.geophile.erdo.map.MapCursor;
import com.geophile.erdo.map.MissingKeyAction;
import com.geophile.erdo.map.diskmap.DBStructure;
import com.geophile.erdo.map.diskmap.DiskPage;
import com.geophile.erdo.map.diskmap.IndexRecord;
import com.geophile.erdo.map.diskmap.Manifest;
import com.geophile.erdo.transaction.TransactionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/*
 * A Tree is a multi-way tree, organized into pages, much like a
 * btree. However, due to the append-only nature of Erdo, trees are
 * static once created, so pages are packed as full as possible, and the
 * only update permitted is appending (implemented by the Writeable*
 * classes).
 * 
 * A Tree is organized into one or more TreeLevels, with the leaf level
 * considered to be at level 0.
 * 
 * A TreeLevel is organized into fixed-size TreeSegments. Each
 * TreeSegment is stored in its own file. Segment size is configured by
 * disk.segmentSizeBytes, and all segments, except possibly the rightmost
 * segment of a level, are of this size.
 * 
 * Each TreeSegment has a TreeSegmentSummary, which records the last key
 * of the segment, and a bloom filter, used to check whether a key is
 * present in the segment.
 * 
 * Each TreeLevel is divided into pages of size disk.pageSizeBytes.
 * 
 * Erdo maintains in memory a complete representation of all Trees,
 * TreeLevels, TreeSegments and TreeSegmentSummaries.
 * 
 * Leaf pages contain full records.
 * 
 * Non-leaf pages contain IndexRecords, which comprise a key and a child
 * pointer. Records on the child pages have a key >= the index record key.
 * I.e., the key is the first key on the child page. (Corollary: The
 * smallest key in the tree is the first key of the root page.)
 */

public class Tree
{
    // Object interface

    @Override
    public String toString()
    {
        return String.format("T%s", treeId);
    }

    // Tree interface

    public MapCursor cursor(AbstractKey startKey)
        throws IOException, InterruptedException
    {
        return
            level(0).leafLevelEmpty()
            ? MapCursor.EMPTY
            : TreeLevelCursor.newCursor(this, startKey);
    }

    public MapCursor consolidationScan() throws IOException, InterruptedException
    {
        // If there aren't two levels, do a normal, slow cursor. This consolidates small files. Also, fast merge logic
        // is dependent on the existence of level 1, to delimit level 0 files.
        return
            levels.size() <= 1
            ? cursor(null)
            : new LevelOneCursorToFindLevelZeroSegments(this);
    }

    public long sizeBytes()
    {
        if (sizeBytes == -1L) {
            sizeBytes = 0;
            TreeLevel leafLevel = level(0);
            int segments = leafLevel.segments();
            for (int s = 0; s < segments; s++) {
                sizeBytes += leafLevel.segment(s).pages() * pageSizeBytes;
            }
        }
        return sizeBytes;
    }

    public void destroy()
    {
        for (TreeLevel level : levels) {
            level.destroy();
        }
    }

    public TransactionManager transactionManager()
    {
        return transactionManager;
    }

    public long treeId()
    {
        return treeId;
    }

    public int levels()
    {
        return levels.size();
    }

    public TreeLevel level(int levelNumber)
    {
        return levels.get(levelNumber);
    }

    public static WriteableTree create(Factory factory,
                                       DBStructure dbStructure,
                                       long treeId)
    {
        WriteableTree tree = new WriteableTree(factory, dbStructure, treeId);
        TreeLevel rootLevel = WriteableTreeLevel.create(tree, 0);
        tree.levels.add(rootLevel);
        return tree;
    }

    public static Tree recover(Factory factory,
                               DBStructure dbStructure,
                               Manifest manifest)
        throws IOException, InterruptedException
    {
        Tree tree = new Tree(factory, dbStructure, manifest.treeId());
        tree.recover(manifest);
        return tree;
    }

    // For use by this package

    Factory factory()
    {
        return factory;
    }

    public DBStructure dbStructure()
    {
        return dbStructure;
    }

    int pageSizeBytes()
    {
        return pageSizeBytes;
    }

    long maxFileSizeBytes()
    {
        return maxFileSizeBytes;
    }

    int segmentNumber(int pageAddress)
    {
        return pageAddress >>> pageNumberBits;
    }

    int pageNumber(int pageAddress)
    {
        return pageAddress & pageNumberMask;
    }

    int pageAddress(int segmentNumber, int pageNumber)
    {
        return (segmentNumber << pageNumberBits) | pageNumber;
    }

    TreePosition newPosition()
    {
        TreePositionPool treePositionPool = factory.threadTreePositionPool();
        TreePosition treePosition = (TreePosition) treePositionPool.takeResource();
        treePosition.initialize(this);
        return treePosition;
    }

    void descendToLeaf(TreePosition position, AbstractKey startKey, MissingKeyAction missingKeyAction)
        throws IOException, InterruptedException
    {
        int level = position.level().levelNumber();
        int recordNumber = recordNumber(position.page(), startKey, missingKeyAction);
        if (recordNumber == -1) {
            assert level == 0 : startKey;
            position.goToEnd();
        } else {
            position.recordNumber(recordNumber);
        }
        if (level > 0) {
            IndexRecord indexRecord = (IndexRecord) position.materializeRecord();
            position.level(level - 1).pageAddress(indexRecord.childPageAddress());
            descendToLeaf(position, startKey, missingKeyAction);
        }
    }

    // Return -1 to indicate that the resulting cursor will be immediately closed. I.e., missingKeyAction == FORWARD
    // and startKey > last on page; or missingKeyAction == BACKWARD and startKey < first on page.
    int recordNumber(DiskPage page, AbstractKey startKey, MissingKeyAction missingKeyAction)
        throws IOException, InterruptedException
    {
        int recordNumber = page.recordNumber(startKey);
        if (recordNumber >= 0) {
            // startKey found
        } else if (page.level() == 0) {
            // recordNumber is -p-1 where p is insertion point of key.
            recordNumber = -recordNumber - 1;
            if (recordNumber == page.nRecords() && missingKeyAction == MissingKeyAction.FORWARD ||
                recordNumber == 0 && missingKeyAction == MissingKeyAction.BACKWARD) {
                recordNumber = -1;
            } else if (recordNumber > 0 && missingKeyAction == MissingKeyAction.BACKWARD) {
                recordNumber--;
            }
        } else {
            // recordNumber is -p-1 where p is insertion point of key. We are above the leaf level so
            // we want the preceding record. if p = 0, then either this is the left most node (page 0),
            // or we made a mistake getting here from the parent.
            assert page.level() > 0 : startKey;
            if (recordNumber == -1) {
                int pageNumber = pageNumber(page.pageAddress());
                assert pageNumber == 0 : startKey;
                recordNumber = 0;
            } else {
                recordNumber = -recordNumber - 2;
            }
        }
        return recordNumber;
    }

    // For use by this class

    // For use by subclasses

    protected Tree(Factory factory, DBStructure dbStructure, long treeId)
    {
        this.treeId = treeId;
        this.factory = factory;
        this.transactionManager = factory.transactionManager();
        this.dbStructure = dbStructure;
        this.pageSizeBytes = factory.configuration().diskPageSizeBytes();
        this.maxFileSizeBytes = factory.configuration().diskSegmentSizeBytes();
        this.pageNumberBits = com.geophile.erdo.util.Math.ceilLog2((int) (maxFileSizeBytes / pageSizeBytes));
        this.pageNumberMask = (1 << pageNumberBits) - 1;
        if (maxFileSizeBytes % pageSizeBytes != 0) {
            throw new UsageError(String.format("%s (%s) is not divisible by %s (%s)",
                                                         ConfigurationKeys.DISK_SEGMENT_SIZE_BYTES,
                                                         maxFileSizeBytes,
                                                         ConfigurationKeys.DISK_PAGE_SIZE_BYTES,
                                                         pageSizeBytes));
        }
    }

    // For use by this class

    private void recover(Manifest manifest) throws IOException, InterruptedException
    {
        for (int level = 0; level < manifest.levels(); level++) {
            levels.add(TreeLevel.recover(this, level, manifest));
        }
    }

    // Class state

    protected static final Logger LOG = Logger.getLogger(Tree.class.getName());

    // Object state

    protected final long treeId;
    protected final Factory factory;
    protected final TransactionManager transactionManager;
    protected final DBStructure dbStructure;
    protected final int pageSizeBytes;
    protected final long maxFileSizeBytes;
    protected final int pageNumberBits;
    protected final int pageNumberMask;
    protected final List<TreeLevel> levels = new ArrayList<>();
    private long sizeBytes = -1L;
}
