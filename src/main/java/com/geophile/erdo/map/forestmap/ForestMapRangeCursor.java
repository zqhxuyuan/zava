/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.forestmap;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.forest.ForestSnapshot;
import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.MapCursor;
import com.geophile.erdo.map.SealedMap;
import com.geophile.erdo.map.emptymap.EmptyMapCursor;
import com.geophile.erdo.map.mergescan.MergeCursor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

class ForestMapRangeCursor extends ForestMapCursor
{
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
            if (cursor != null) {
                cursor.close();
            } else {
                assert state == State.NEVER_USED;
            }
            super.close();
            for (MapCursor smallMapScan : smallMapScans.values()) {
                smallMapScan.close();
            }
        }
    }

    // ForestMapRangeCursor interface

    ForestMapRangeCursor(ForestSnapshot forestSnapshot, AbstractKey startKey)
        throws IOException, InterruptedException
    {
        super(forestSnapshot, startKey, false);
    }

    // For use by this class

    private LazyRecord updateRecord(AbstractKey key) throws IOException, InterruptedException
    {
        SealedMap map = forestSnapshot.mapContainingTransaction(key.transactionTimestamp());
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Getting record of {0} from {1}", new Object[]{key, map});
        }
        assert map != null : key;
        MapCursor cursor = keyFinder(map, key);
        LazyRecord updateRecord = cursor.next();
        assert updateRecord != null : key;
        return updateRecord;
    }

    private MapCursor keyFinder(SealedMap map, AbstractKey key) throws IOException, InterruptedException
    {
        MapCursor smallMapScan = smallMapScans.get(map.mapId());
        if (smallMapScan == null) {
            smallMapScan = map.cursor(key, true);
            smallMapScans.put(map.mapId(), smallMapScan);
        }
        smallMapScan.goTo(key);
        return smallMapScan;
    }

    private static MapCursor merge(List<SealedMap> maps, AbstractKey startKey, boolean forward)
        throws IOException, InterruptedException
    {
        MapCursor cursor;
        int mapSize = maps.size();
        if (mapSize == 0) {
            cursor = new EmptyMapCursor();
        } else if (mapSize == 1) {
            cursor = maps.get(0).cursor(startKey, false);
        } else {
            MergeCursor mergeScan = new MergeCursor(startKey, forward);
            for (SealedMap map : maps) {
                mergeScan.addInput(map.keyScan(startKey, false));
            }
            mergeScan.start();
            cursor = mergeScan;
        }
        return cursor;
    }

    private LazyRecord neighbor(boolean forward) throws IOException, InterruptedException
    {
        LazyRecord neighbor = null;
        if (state != State.DONE) {
            if (state == State.NEVER_USED) {
                MergeCursor combinedScan = new MergeCursor(startKey, forward);
                combinedScan.addInput(new KeyToUpdatedRecordCursor(merge(forestSnapshot.smallTrees(), startKey, forward)));
                combinedScan.addInput(merge(forestSnapshot.bigTrees(), startKey, forward));
                combinedScan.start();
                cursor = combinedScan;
                state = State.IN_USE;
            }
            neighbor = forward ? cursor.next() : cursor.previous();
            if (neighbor == null || !isOpen(neighbor.key())) {
                close();
            }
        }
        return neighbor;
    }

    // Object state

    private MapCursor cursor;
    private final Map<Long, MapCursor> smallMapScans = new HashMap<>(); // mapId -> MapCursor

    // Inner classes

    private class KeyToUpdatedRecordCursor extends MapCursor
    {
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
        public void goToFirst() throws IOException, InterruptedException
        {
            cursor.goToFirst();
        }

        @Override
        public void goToLast() throws IOException, InterruptedException
        {
            cursor.goToLast();
        }

        @Override
        public void goTo(AbstractKey key) throws IOException, InterruptedException
        {
            cursor.goTo(key);
        }

        @Override
        public void close()
        {
            cursor.close();
        }

        // KeyToUpdatedRecordCursor interface

        public KeyToUpdatedRecordCursor(MapCursor cursor)
        {
            super(null, false);
            this.cursor = cursor;
        }

        // For use by this class

        private LazyRecord neighbor(boolean forward) throws IOException, InterruptedException
        {
            LazyRecord neighbor = null;
            LazyRecord record = forward ? cursor.next() : cursor.previous();
            if (record == null) {
                close();
            } else {
                AbstractKey key = record.key();
                record.destroyRecordReference();
                neighbor = updateRecord(key);
            }
            return neighbor;
        }

        // Object state

        private MapCursor cursor;
    }
}
