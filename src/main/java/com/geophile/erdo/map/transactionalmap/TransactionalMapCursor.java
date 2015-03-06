/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.transactionalmap;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.MapCursor;
import com.geophile.erdo.map.forestmap.ForestMapCursor;
import com.geophile.erdo.map.mergescan.MergeCursor;

import java.io.IOException;

class TransactionalMapCursor extends MapCursor
{
    // MapCursor interface

    public LazyRecord next() throws IOException, InterruptedException
    {
        return neighbor(true);
    }

    public LazyRecord previous() throws IOException, InterruptedException
    {
        return neighbor(false);
    }

    public void close()
    {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

    // TransactionalMapCursor interface

    TransactionalMapCursor(TransactionalMap transactionalMap, AbstractKey startKey, boolean singleKey)
         throws IOException, InterruptedException
    {
        super(startKey, singleKey);
        MapCursor snapshotCursor = ForestMapCursor.newCursor(transactionalMap.forestSnapshot, startKey, singleKey);
        if (transactionalMap.updates == null || // dynamic map was rolled back.
            transactionalMap.updates.recordCount() == 0) {
            cursor = snapshotCursor;
        } else {
            MergeCursor mergeCursor = new MergeCursor(startKey, true);
            mergeCursor.addInput(snapshotCursor);
            mergeCursor.addInput(transactionalMap.updates.cursor(startKey, singleKey));
            mergeCursor.start();
            cursor = mergeCursor;
        }
    }

    // For use by this class

    private LazyRecord neighbor(boolean forward) throws IOException, InterruptedException
    {
        LazyRecord neighbor = null;
        if (cursor != null) {
            neighbor = forward ? cursor.next() : cursor.previous();
            if (neighbor == null) {
                close();
            }
            // Don't need to consult singleKey flag to see if neighbor should be kept,
            // as the underlying snapshot cursor does that.
        }
        return neighbor;
    }

    // Object state

    private MapCursor cursor;
}
