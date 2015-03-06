/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.apiimpl;

import com.geophile.erdo.*;
import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.MapCursor;
import com.geophile.erdo.map.transactionalmap.TransactionalMap;
import com.geophile.erdo.util.IdGenerator;

import java.io.IOException;

public class OrderedMapImpl extends OrderedMap
{
    // OrderedMap interface

    @Override
    public void ensurePresent(AbstractRecord record)
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        database.checkDatabaseOpen();
        // Copy the record so that changes to the record, made by the caller, don't change
        // the record in the database.
        record = record.copy();
        record.key().erdoId(erdoId);
        transactionalMap().put(record, false);
    }

    @Override
    public AbstractRecord put(AbstractRecord record)
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        database.checkDatabaseOpen();
        AbstractRecord replaced = null;
        // Copy the record so that changes to the record, made by the caller, don't change
        // the record in the database.
        record = record.copy();
        record.key().erdoId(erdoId);
        LazyRecord lazyRecord = transactionalMap().put(record, true);
        if (lazyRecord != null) {
            replaced = lazyRecord.materializeRecord();
            if (replaced.deleted()) {
                replaced = null;
            }
        }
        return replaced;
    }

    @Override
    public void ensureAbsent(AbstractKey key)
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        ensurePresent(new DeletedRecord(key));
    }

    @Override
    public AbstractRecord delete(AbstractKey key)
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        return put(new DeletedRecord(key));
    }

    @Override
    public void lock(AbstractKey key)
        throws InterruptedException,
               DeadlockException,
               TransactionRolledBackException,
               IOException
    {
        database.checkDatabaseOpen();
        transactionalMap().lock(key);
    }

    @Override
    public AbstractRecord find(AbstractKey key) throws IOException, InterruptedException
    {
        database.checkDatabaseOpen();
        checkNotNull(key);
        key.erdoId(erdoId);
        return newCursor(key, true).next();
    }

    @Override
    public Cursor cursor(AbstractKey startKey) throws IOException, InterruptedException
    {
        database.checkDatabaseOpen();
        if (startKey != null) {
            startKey.erdoId(erdoId);
        }
        return newCursor(startKey, false);
    }

    @Override
    public Cursor first() throws IOException, InterruptedException
    {
        database.checkDatabaseOpen();
        return newCursor(lowestKey, false);
    }

    @Override
    public Cursor last() throws IOException, InterruptedException
    {
        database.checkDatabaseOpen();
        return newCursor(highestKey, false);
    }

    // OrderedMapImpl interface

    public OrderedMapImpl(DatabaseImpl database)
    {
        this(database, (int) erdoIdGenerator.nextId());
    }

    public OrderedMapImpl(DatabaseImpl database, int erdoId)
    {
        assert database != null;
        this.database = database;
        this.erdoId = erdoId;
        this.lowestKey = ErdoId.lowest(erdoId);
        this.highestKey = ErdoId.highest(erdoId);
    }

    public int erdoId()
    {
        return erdoId;
    }

    // For use by this class

    private Cursor newCursor(final AbstractKey key, final boolean singleKey)
        throws IOException, InterruptedException
    {
        MapCursor.Expression mapCursorExpression =
            new MapCursor.Expression()
            {
                @Override
                public MapCursor evaluate() throws IOException, InterruptedException
                {
                    return transactionalMap().cursor(key, singleKey);
                }
            };
        return new CursorImpl(database, mapCursorExpression);
    }

    private TransactionalMap transactionalMap()
    {
        transactionalMap = database.transactionManager().currentTransaction().transactionalMap();
        return transactionalMap;
    }

    private void checkNotNull(AbstractKey key)
    {
        if (key == null) {
            throw new IllegalArgumentException();
        }
    }

    // Class state

    private static final IdGenerator erdoIdGenerator = new IdGenerator(1);

    // Object state

    private final DatabaseImpl database;
    private final int erdoId;
    private final ErdoId lowestKey;
    private final ErdoId highestKey; // For positioning by last()
    private TransactionalMap transactionalMap; // For visibility in debugger
}
