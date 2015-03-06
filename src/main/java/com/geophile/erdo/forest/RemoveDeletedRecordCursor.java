/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.forest;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.MapCursor;
import com.geophile.erdo.map.mergescan.AbstractMultiRecord;

import java.io.IOException;

class RemoveDeletedRecordCursor extends MapCursor
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
        cursor = null;
    }

    // RemoveDeletedRecordCursor interface

    public RemoveDeletedRecordCursor(MapCursor cursor, long maxDeletionTimestamp)
    {
        super(null, false);
        this.cursor = cursor;
        this.maxDeletionTimestamp = maxDeletionTimestamp;
    }

    // For use by this class

    private LazyRecord neighbor(boolean forward) throws IOException, InterruptedException
    {
        LazyRecord neighbor = null;
        AbstractKey key;
        if (cursor != null) {
            boolean removeDeletedRecord;
            do {
                neighbor = forward ? cursor.next() : cursor.previous();
                if (neighbor != null &&
                    (key = neighbor.key()).deleted() &&
                    checkSingleRecord(neighbor) &&
                    key.transactionTimestamp() <= maxDeletionTimestamp) {
                    neighbor.destroyRecordReference();
                    removeDeletedRecord = true;
                } else {
                    removeDeletedRecord = false;
                }
            } while (removeDeletedRecord);
            if (neighbor == null) {
                close();
            }
        }
        return neighbor;
    }

    private boolean checkSingleRecord(LazyRecord record)
    {
        // AbstractKey.deleted(), (called in next() above), should always be false for a multi-record.
        // So if we get here, (called after next()), then the record must not be a multi-record.
        assert !(record instanceof AbstractMultiRecord) : record;
        return true;
    }

    // Object state

    private MapCursor cursor;
    private final long maxDeletionTimestamp;
}
