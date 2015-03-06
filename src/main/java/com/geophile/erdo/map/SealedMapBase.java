/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.transaction.TimestampSet;
import com.geophile.erdo.transaction.Transaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for sealed maps
 */
public abstract class SealedMapBase extends MapBase implements SealedMap
{
    // Object interface

    @Override
    public String toString()
    {
        StringBuilder buffer = new StringBuilder(super.toString());
        buffer.append('(');
        buffer.append(recordCount());
        buffer.append(')');
        TimestampSet timestamps = timestamps();
        if (timestamps != null) {
            buffer.append("txns(");
            buffer.append(timestamps.toString());
            buffer.append(")");
        }
        return buffer.toString();
    }

    // TransactionUpdates interface

    public void transactionTimestamp(long timestamp)
    {
        throw new UnsupportedOperationException();
    }

    public TimestampSet timestamps()
    {
        return timestamps;
    }

    // SealedMap interface

    public abstract MapCursor cursor(AbstractKey startKey, boolean singleKey)
        throws IOException, InterruptedException;

    public abstract long recordCount();

    public abstract long estimatedSizeBytes();

    public abstract void loadForConsolidation(MapCursor recordScan, MapCursor keyScan)
        throws IOException, InterruptedException;

    // Belongs to Consolidation.Element interface too
    public void destroyPersistentState()
    {}

    public abstract boolean keysInMemory();

    public abstract MapCursor keyScan(AbstractKey startKey, boolean singleKey)
        throws IOException, InterruptedException;

    public MapCursor consolidationScan() throws IOException, InterruptedException
    {
        return cursor(null, false);
    }

    // Consolidation.Element interface

    public final long id()
    {
        return mapId();
    }

    public final long count()
    {
        return recordCount();
    }

    public final long sizeBytes()
    {
        return estimatedSizeBytes();
    }

    public abstract boolean durable();

    public void markDurable()
    {
    }

    public final void registerTransactions(List<Transaction> transactions)
    {
        this.transactions.addAll(transactions);
    }

    public final List<Transaction> transactions()
    {
        return transactions;
    }

    // For use by subclasses

    protected SealedMapBase(Factory factory)
    {
        super(factory);
    }

    protected SealedMapBase(Factory factory, int mapId)
    {
        super(factory, mapId);
    }

    // Object state

    // transactions is needed so that when a non-durable -> durable consolidation is done,
    // we know what transactions have become durable, (markDurable is then called).
    // timestamps are tracked separately. timestamps could be derived from transactions,
    // but this would complicate testing that doesn't use transactions.
    // TODO: Are there still such tests? Should there be?
    protected TimestampSet timestamps;
    protected List<Transaction> transactions = new ArrayList<Transaction>();
}
