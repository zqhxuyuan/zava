/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.arraymap;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.map.Factory;
import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.MapCursor;
import com.geophile.erdo.map.SealedMapBase;
import com.geophile.erdo.transaction.TimestampSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Used for in-memory consolidations.

public class ArrayMap extends SealedMapBase
{
    // Consolidation.Element interface

    @Override
    public boolean durable()
    {
        return false;
    }

    // OpenOrSealedMapBase interface

    @Override
    public MapCursor cursor(AbstractKey startKey, boolean singleKey)
        throws IOException, InterruptedException
    {
        return new ArrayMapCursor(this, startKey);
    }

    @Override
    public MapCursor keyScan(AbstractKey startKey, boolean singleKey)
        throws IOException, InterruptedException
    {
        return cursor(startKey, singleKey);
    }

    @Override
    public long recordCount()
    {
        return records.size();
    }

    @Override
    public long estimatedSizeBytes()
    {
        return estimatedSizeBytes;
    }

    @Override
    public void loadForConsolidation(MapCursor recordScan, MapCursor keyScan)
        throws UnsupportedOperationException, IOException, InterruptedException
    {
        estimatedSizeBytes = 0;
        LazyRecord record;
        while ((record = recordScan.next()) != null) {
            records.add(record);
            estimatedSizeBytes += record.estimatedSizeBytes();
        }
    }

    @Override
    public boolean keysInMemory()
    {
        return true;
    }

    // ArrayMap interface

    public ArrayMap(Factory factory, TimestampSet timestamps)
    {
        super(factory);
        this.timestamps = timestamps;
    }

    // Object state

    List<LazyRecord> records = new ArrayList<>();
    private long estimatedSizeBytes;
}
