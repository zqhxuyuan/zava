/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.privatemap;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.AbstractRecord;
import com.geophile.erdo.map.Factory;
import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.MapCursor;
import com.geophile.erdo.map.OpenOrSealedMapBase;
import com.geophile.erdo.transaction.TimestampSet;
import com.geophile.erdo.transaction.Transaction;

import java.io.IOException;
import java.util.NavigableMap;
import java.util.TreeMap;

// PrivateMap is an updatable map that stores updates for a single transaction.

public class PrivateMap extends OpenOrSealedMapBase
{
    // Consolidation.Element interface

    @Override
    public boolean durable()
    {
        return false;
    }

    // TransactionUpdates interface

    public void transactionTimestamp(long timestamp)
    {
        timestamps = new TimestampSet(timestamp);
    }

    // OpenOrSealedMapBase interface

    @Override
    public LazyRecord put(AbstractRecord record, boolean returnReplaced)
    {
        assert record != null;
        AbstractKey key = record.key();
        AbstractRecord replaced = contents.put(key, record);
        estimatedSizeBytes += record.estimatedSizeBytes();
        if (replaced != null) {
            estimatedSizeBytes -= replaced.estimatedSizeBytes();
            // When TreeMap.put replaces a record, it keeps the key. If the deletion status (carried by the key)
            // has changed, then the record needs to be removed and reinserted with the new key.
            if (key.deleted() != replaced.key().deleted()) {
                contents.remove(key);
                contents.put(key, record);
            }
        }
        return returnReplaced ? replaced : null;
    }

    @Override
    public MapCursor cursor(AbstractKey startKey, boolean singleKey)
    {
        return new PrivateMapCursor(this, startKey, singleKey);
    }

    @Override
    public MapCursor keyScan(AbstractKey startKey, boolean singleKey)
        throws IOException, InterruptedException
    {
        return new PrivateMapKeyCursor(this, startKey, singleKey);
    }

    // SealedMap interface

    @Override
    public long recordCount()
    {
        return contents.size();
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
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean keysInMemory()
    {
        return true;
    }

    // PrivateMap interface

    public PrivateMap(Factory factory)
    {
        super(factory);
        Transaction transaction = factory.transactionManager().currentTransaction();
        assert transaction != null;
        transactions.add(transaction);
    }

    // Object state

    NavigableMap<AbstractKey, AbstractRecord> contents = new TreeMap<>();
    private long estimatedSizeBytes = 0;
}
