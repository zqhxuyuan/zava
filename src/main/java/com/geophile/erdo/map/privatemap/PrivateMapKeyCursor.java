/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.privatemap;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.AbstractRecord;
import com.geophile.erdo.map.KeyOnlyRecord;
import com.geophile.erdo.map.MapCursor;

import java.util.Iterator;

class PrivateMapKeyCursor extends MapCursor
{
    // MapCursor interface

    public AbstractRecord next()
    {
        return neighbor(true);
    }

    public AbstractRecord previous()
    {
        return neighbor(false);
    }

    public void close()
    {
        state = State.DONE;
    }

    // PrivateMapKeyCursor interface

    public PrivateMapKeyCursor(PrivateMap map, AbstractKey startKey, boolean singleKey)
    {
        super(startKey, singleKey);
        this.map = map;
    }

    // For use by this class

    private AbstractRecord neighbor(boolean forwardMove)
    {
        AbstractRecord neighbor = null;
        AbstractKey neighborKey = null;
        if (state != State.DONE) {
            if (state == State.NEVER_USED || forwardIterator != forwardMove) {
                // Second arg to tailMap/headMap indicates whether the submap is inclusive. If state is IN_USE
                // then we've already visited the restartKey and don't want to do so again.
                if (forwardMove) {
                    iterator =
                        startKey == null
                        ? map.contents.keySet().iterator()
                        : map.contents.tailMap(startKey, state == State.NEVER_USED).keySet().iterator();
                } else {
                    iterator =
                        startKey == null
                        ? map.contents.descendingMap().keySet().iterator()
                        : map.contents.headMap(startKey, state == State.NEVER_USED).descendingMap().keySet().iterator();
                }
                forwardIterator = forwardMove;
                state = State.IN_USE;
            }
        }
        if (iterator.hasNext()) {
            neighborKey = iterator.next();
            if (!isOpen(neighborKey)) {
                neighborKey = null;
                close();
            }
        } else {
            close();
        }
        if (neighborKey != null) {
            neighbor = new KeyOnlyRecord(neighborKey);
            startKey = neighborKey;
        }
        return neighbor;
    }

    // Object state

    private final PrivateMap map;
    private Iterator<AbstractKey> iterator;
    private boolean forwardIterator;
}
