/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.apiimpl;

import com.geophile.erdo.AbstractRecord;
import com.geophile.erdo.Cursor;
import com.geophile.erdo.UsageError;
import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.MapCursor;
import com.geophile.erdo.transaction.Transaction;

import java.io.IOException;

public class CursorImpl extends Cursor
{
    // Cursor interface

    @Override
    public AbstractRecord next() throws IOException, InterruptedException
    {
        try {
            CURRENT_CURSOR.set(this);
            database.checkDatabaseOpen();
            return neighbor(true);
        } finally {
            CURRENT_CURSOR.set(null);
        }
    }

    @Override
    public AbstractRecord previous() throws IOException, InterruptedException
    {
        try {
            CURRENT_CURSOR.set(this);
            database.checkDatabaseOpen();
            return neighbor(false);
        } finally {
            CURRENT_CURSOR.set(null);
        }
    }

    @Override
    public void close()
    {
        try {
            CURRENT_CURSOR.set(this);
            if (mapCursor != null) {
                // Don't call checkTransaction. If a transaction commits and rolls back other transactions, then
                // cursors can be closed from the committing transaction's thread.
                transaction.unregisterCursor(this);
                mapCursor.close();
                mapCursor = null;
                TreePositionTracker.destroyRemainingTreePositions(threadContext());
            }
        } finally {
            CURRENT_CURSOR.set(null);
        }
    }

    // CursorImpl interface

    // The Cursor currently being operated on by this thread. Returns null if there is no Cursor method on the
    // stack. CursorImpl is an API implementation object, not internal like a MapCursor. It is therefore not possible
    // to next CursorImpl method invocations, so the current cursor need not be tracked using a stack.
    public static CursorImpl threadContext()
    {
        return CURRENT_CURSOR.get();
    }

    CursorImpl(DatabaseImpl database, MapCursor.Expression mapCursorExpression) throws IOException, InterruptedException
    {
        this.database = database;
        this.transaction = database.transactionManager().currentTransaction();
        this.transaction.registerCursor(this);
        try {
            CURRENT_CURSOR.set(this);
            this.mapCursor = mapCursorExpression.evaluate();
        } finally {
            CURRENT_CURSOR.set(null);
        }
    }

    // For use by this class

    private AbstractRecord neighbor(boolean forward) throws IOException, InterruptedException
    {
        AbstractRecord record = null;
        if (mapCursor != null) {
            LazyRecord neighbor;
            boolean deleted = false;
            checkTransaction();
            do {
                neighbor = forward ? mapCursor.next() : mapCursor.previous();
                if (neighbor != null) {
                    deleted = neighbor.key().deleted();
                    if (deleted) {
                        neighbor.destroyRecordReference();
                        database.factory().testObserver().readDeletedKey();
                    }
                }
            } while (neighbor != null && deleted);
            if (neighbor != null) {
                record = neighbor.materializeRecord();
                if (!neighbor.prefersSerialized()) {
                    // LazyRecord stores an actual record that is part of the database. Copy it so that any
                    // changes by the application don't modify database state.
                    record = record.copy();
                }
                neighbor.destroyRecordReference();
                // Give application a records without a timestamp set, which will allow it to update
                // the record, setting the transaction.
                record.key().clearTransactionState();
            } else {
                record = null;
                close();
            }
        }
        return record;
    }

    private void checkTransaction()
    {
        if (database.transactionManager().currentTransaction() != transaction) {
            throw new UsageError("Cursor cannot be used across transaction boundaries");
        }
    }

    // Class state

    private static final ThreadLocal<CursorImpl> CURRENT_CURSOR = new ThreadLocal<>();

    // Object state

    private final DatabaseImpl database;
    private final Transaction transaction;
    private MapCursor mapCursor;
}
