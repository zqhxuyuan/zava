/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.privatemap;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.AbstractRecord;
import com.geophile.erdo.map.MapCursor;

import java.util.Iterator;

class PrivateMapCursor extends MapCursor
{
    // MapCursor interface

    @Override
    public AbstractRecord next()
    {
        return neighbor(true);
    }

    @Override
    public AbstractRecord previous()
    {
        return neighbor(false);
    }

    // PrivateMapCursor interface

    PrivateMapCursor(PrivateMap map, AbstractKey startKey, boolean singleKey)
    {
        super(startKey, singleKey);
        this.map = map;
    }

    // For use by this class

    private AbstractRecord neighbor(boolean forwardMove)
    {
        AbstractRecord neighbor = null;
        if (state != State.DONE) {
            if (state == State.NEVER_USED || forwardIterator != forwardMove) {
                // Second arg to tailMap/headMap indicates whether the submap is inclusive. If state is IN_USE
                // or REPOSITIONED, (i.e. not NEVER_USED or DONE), then we've already visited the startKey and don't
                // want to do so again. (I.e., the state is either IN_USE or REPOSITIONED. In either case we've
                // visited the startKey.)
                if (forwardMove) {
                    iterator =
                        startKey == null
                        ? map.contents.values().iterator()
                        : map.contents.tailMap(startKey, state == State.NEVER_USED).values().iterator();
                } else {
                    iterator =
                        startKey == null
                        ? map.contents.descendingMap().values().iterator()
                        : map.contents.headMap(startKey, state == State.NEVER_USED).descendingMap().values() .iterator();
                }
                forwardIterator = forwardMove;
                state = State.IN_USE;
            }
            if (iterator.hasNext()) {
                neighbor = iterator.next();
                if (!isOpen(neighbor.key())) {
                    neighbor = null;
                    close();
                }
            } else {
                close();
            }
            if (neighbor != null) {
                startKey = neighbor.key();
            }
        }
        return neighbor;
    }

    // State

    private final PrivateMap map;
    private Iterator<AbstractRecord> iterator;
    private boolean forwardIterator;
}
