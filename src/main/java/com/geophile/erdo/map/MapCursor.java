/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.map.emptymap.EmptyMapCursor;

import java.io.IOException;

public abstract class MapCursor
{
    public abstract LazyRecord next() throws IOException, InterruptedException;

    public abstract LazyRecord previous() throws IOException, InterruptedException;

    public void close()
    {
        state = State.DONE;
    }

    public void goToFirst() throws IOException, InterruptedException
    {
        startKey = null;
        state = State.NEVER_USED;
        unboundStartAtFirstKey = true;
    }

    public void goToLast() throws IOException, InterruptedException
    {
        startKey = null;
        state = State.NEVER_USED;
        unboundStartAtFirstKey = false;
    }

    public void goTo(AbstractKey key) throws IOException, InterruptedException
    {
        assert key != null;
        startKey = key;
        state = State.NEVER_USED;
    }

    protected boolean isOpen(AbstractKey key)
    {
        if (key == null) {
            // cursor is closed because we've run off the end
            return false;
        }
        if (fullScan) {
            // scanning the entire map, regardless of erdoId, so any non-null key is part of the cursor
            return true;
        }
        if (key.erdoId() == startKey.erdoId()) {
            // In the same OrderedMap as startKey
            return !singleKey || key.equals(startKey);
        } else {
            return false;
        }
    }

    protected MapCursor(AbstractKey startKey, boolean singleKey)
    {
        assert startKey != null || !singleKey;
        this.startKey = startKey;
        this.singleKey = singleKey;
        this.state = State.NEVER_USED;
        this.fullScan = startKey == null;
    }

    // Object state

    // Kinds of scans:
    // - Complete scan of map, across all erdoIds: startKey == null, singleKey == false. Used in consolidation.
    // - Exact match: startKey != null, singleKey = true
    // - Start at key, limited to one erdoId: startKey != null, singleKey = false
    // - Other: canCheckIsOpen is false, meaning the subclass will check loop termination.
    protected AbstractKey startKey;
    protected State state;
    private final boolean singleKey;
    // unboundStartAtFirstKey is used to indicate where to position the cursor while in the NEVER_USED state,
    // with startKey = null. true means start at the first key, false means start at the last key.
    protected boolean unboundStartAtFirstKey;
    private final boolean fullScan;

    // Inner classes

    public static final MapCursor EMPTY = new EmptyMapCursor();

    protected enum State
    {
        // The cursor has been created, but has never been used to retrieve a record. If the key used to create
        // the cursor is present, then both next() and previous() will retrieve the associated record. This state
        // is also used when a cursor is repositioned using goTo().
        NEVER_USED,

        // The cursor has been created and used to retrieve at least one record. next() and previous() move the
        // cursor before retrieving a record.
        IN_USE,

        // The cursor has run off one end. A call to next() or previous() will return null.
        DONE
    }

    public interface Expression
    {
        MapCursor evaluate() throws IOException, InterruptedException;
    }
}
