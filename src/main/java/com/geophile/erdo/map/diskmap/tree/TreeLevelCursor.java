/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap.tree;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.MapCursor;
import com.geophile.erdo.map.MissingKeyAction;
import com.geophile.erdo.map.diskmap.DiskPage;
import com.geophile.erdo.map.diskmap.IndexRecord;
import com.geophile.erdo.util.IdGenerator;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

class TreeLevelCursor extends MapCursor
{
    // Object interface

    @Override
    public String toString()
    {
        return String.format("TreeLevelCursor(%s)", id);
    }

    // MapCursor interface

    @Override
    public LazyRecord next() throws IOException, InterruptedException
    {
        return neighbor(true);
    }

    @Override
    public LazyRecord previous() throws IOException, InterruptedException
    {
        return neighbor(false);
    }

    @Override
    public void close()
    {
        if (state != State.DONE) {
            if (position != null) {
                position.destroyRecordReference();
                position = null;
            }
            // Don't call end.destroyRecordReference(). end is passed in, and is not owned by this.
            super.close();
            if (LOG.isLoggable(Level.INFO)) {
                LOG.log(Level.INFO, "{0} closed", this);
            }
        }
    }

    @Override
    protected boolean isOpen(AbstractKey key)
    {
        throw new UnsupportedOperationException(getClass().getName());
    }

    // TreeLevelCursor interface

    static TreeLevelCursor newCursor(Tree tree, AbstractKey startKey)
    {
        return new TreeLevelCursor(tree, startKey);
    }

    // For use by this package

    TreeLevelCursor(Tree tree, AbstractKey startKey)
    {
        super(null, false);
        this.tree = tree;
        this.startKey = startKey;
        LOG.log(Level.INFO, "{0} created", this);
    }

    // For use by this class

    private LazyRecord neighbor(boolean forwardMove) throws IOException, InterruptedException
    {
        TreePosition neighbor = null;
        if (state != State.DONE) {
            if (state == State.NEVER_USED || forwardDirection != forwardMove) {
                restart(forwardMove ? MissingKeyAction.FORWARD : MissingKeyAction.BACKWARD);
            } else {
                if (atEnd()) {
                    close();
                } else {
                    if (forwardMove) {
                        position.goToNextRecord();
                    } else {
                        position.goToPreviousRecord();
                    }
                    if (atEnd()) {
                        close();
                    }
                }
            }
        }
        if (state != State.DONE) {
            neighbor = position.copy();
            state = State.IN_USE;
            forwardDirection = forwardMove;
            startKey = neighbor.key();
        }
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "{0} {1} to {2}",
                    new Object[]{this, (forwardMove ? "forward" : "backward"), neighbor});
        }
        return neighbor;
    }

    private void restart(MissingKeyAction missingKeyAction) throws IOException, InterruptedException
    {
        if (startKey == null) {
            // Scan an entire Map
            // TODO: This does an unnecessary page read if the usage is to create a cursor and then position
            // TODO: it as necessary from ForestMapCursor.
            position =
                missingKeyAction.forward()
                ? tree.newPosition().level(0).goToFirstSegmentOfLevel().goToFirstPageOfSegment().goToFirstRecordOfPage()
                : tree.newPosition().level(0).goToLastSegmentOfLevel().goToLastPageOfSegment().goToLastRecordOfPage();
        } else if (missingKeyAction == MissingKeyAction.CLOSE &&
                   !tree.level(0).keyPossiblyPresent(startKey)) {
            assert state == State.NEVER_USED; // implied by MisskingKeyAction.CLOSE
            close();
        } else {
            if (position != null &&
                startKey.compareTo(position.page().firstKey()) >= 0 &&
                startKey.compareTo(position.page().lastKey()) <= 0) {
                // Staying on the same page.
                assert position.level().isLeaf();
            } else {
                // Start cursor at startKey.
                position =
                    // root page
                    tree.newPosition().level(tree.levels() - 1).goToFirstSegmentOfLevel().goToFirstPageOfSegment();
            }
            // If we're already on a leaf, then descendToLeaf just sets the position within the leaf.
            descendToLeaf(missingKeyAction);
            boolean alreadyVisitedStartKey = state == State.IN_USE;
            if (alreadyVisitedStartKey && position.key().equals(startKey)) {
                if (missingKeyAction.forward()) {
                    position.goToNextRecord();
                } else {
                    position.goToPreviousRecord();
                }
            }
        }
        if (atEnd()) {
            close();
        }
        if (LOG.isLoggable(Level.INFO)) {
            LOG.log(Level.INFO, "{0} restarted at {1}: {2}", new Object[]{this, startKey, state});
        }
    }

    private void descendToLeaf(MissingKeyAction missingKeyAction)
        throws IOException, InterruptedException
    {
        int level = position.level().levelNumber();
        int recordNumber = recordNumber(position.page(), missingKeyAction);
        if (recordNumber == -1) {
            assert level == 0 : startKey;
            if (missingKeyAction.forward()) {
                position.goToNextPage();
                if (!position.atEnd()) {
                    position.goToFirstRecordOfPage();
                }
            } else {
                position.goToPreviousPage();
                if (!position.atEnd()) {
                    position.goToLastRecordOfPage();
                }
            }
        } else {
            position.recordNumber(recordNumber);
        }
        if (level > 0) {
            IndexRecord indexRecord = (IndexRecord) position.materializeRecord();
            position.level(level - 1).pageAddress(indexRecord.childPageAddress());
            descendToLeaf(missingKeyAction);
        }
    }

    // Return -1 to indicate that the resulting cursor needs to be moved off-page. I.e., missingKeyAction == FORWARD
    // and startKey > last on page; or missingKeyAction == BACKWARD and startKey < first on page.
    private int recordNumber(DiskPage page, MissingKeyAction missingKeyAction)
        throws IOException, InterruptedException
    {
        int recordNumber = page.recordNumber(startKey);
        if (recordNumber < 0) {
            // startKey not present
            if (page.level() == 0) {
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
                    int pageNumber = tree.pageNumber(page.pageAddress());
                    assert pageNumber == 0 : startKey;
                    recordNumber = 0;
                } else {
                    recordNumber = -recordNumber - 2;
                }
            }
        }
        // else: startKey is present
        return recordNumber;
    }

    private boolean atEnd()
    {
        return position.atEnd();
    }

    // Class state

    private static final Logger LOG = Logger.getLogger(TreeLevelCursor.class.getName());
    private static final IdGenerator idGenerator = new IdGenerator(0);

    // Object state

    private final long id = idGenerator.nextId();
    private final Tree tree;
    private boolean forwardDirection;
    private TreePosition position;
}
