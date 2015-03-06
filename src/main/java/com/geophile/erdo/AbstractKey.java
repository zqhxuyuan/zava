/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo;

import com.geophile.erdo.apiimpl.ErdoId;
import com.geophile.erdo.map.Factory;
import com.geophile.erdo.transaction.Transaction;
import com.geophile.erdo.util.Transferrable;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * AbstractKey is the base class for Erdo keys. Applications need to provide subclasses that contain key state,
 * compare and hash keys, serialize and deserialize, copy a key, and provide an estimate of serialized size.
 *
 * This class contains several undocumented methods. These need to be public for implementation reasons, but are not
 * intended to be used by applications or by AbstractKey subclasses.
 */

public abstract class AbstractKey implements Comparable<AbstractKey>, Transferrable
{
    // Object interface

    @Override
    public String toString()
    {
        return
            erdoId == ERDO_ID_UNSET ? "?" :
            erdoId == ERDO_ID_UNSET_DELETED ? "?D" :
            deleted() ? String.format("%sD", erdoId()) : Integer.toString(erdoId());
    }

    @Override
    public boolean equals(Object obj)
    {
        return
            obj != null &&
            obj instanceof AbstractKey &&
            this.compareTo((AbstractKey) obj) == 0;
    }

    @Override
    public int hashCode()
    {
        return erdoId;
    }

    // Comparable interface

    /**
     * Indicates whether this key appears in key order before, after, or at the same position as the given key.
     * @param that key to compare to.
     * @return negative int if this precedes that; positive int if this follows that;
     *     zero if neither precedes the other.
     */
    public int compareTo(AbstractKey that)
    {
        int c = (this.erdoId & ~ERDO_ID_DELETED_MASK) - (that.erdoId & ~ERDO_ID_DELETED_MASK);
        if (c == 0 && this != that) {
            if (this instanceof ErdoId) {
                c = ((ErdoId) this).lowest() ? -1 : 1;
            } else if (that instanceof ErdoId) {
                c = ((ErdoId) that).lowest() ? 1 : -1;
            }
        }
        return c;
    }

    // Transferrable interface

    /**
     * Read the state of this key from the given buffer.
     * @param buffer contains the serialized state of the key.
     * @throws BufferUnderflowException
     */
    public void readFrom(ByteBuffer buffer) throws BufferUnderflowException
    {
        deleted(buffer.get() != 0);
    }

    /**
     * Write the state of this key to the given buffer.
     * @param buffer container of the serialized key.
     * @throws BufferOverflowException
     */
    public void writeTo(ByteBuffer buffer) throws BufferOverflowException
    {
        buffer.put((byte) (deleted() ? 1 : 0));
    }

    public final int recordCount()
    {
        return 1;
    }

    // AbstractKey interface

    /**
     * An estimate of the size of the serialized key.
     * @return estimate of the serialized key size, in bytes.
     */
    public abstract int estimatedSizeBytes();

    public final long transactionTimestamp()
    {
        if (timestamp == TIMESTAMP_NOT_SET || timestamp == Transaction.UNCOMMITTED_TIMESTAMP) {
            assert transaction != null;
            timestamp = transaction.timestamp();
        }
        return timestamp;
    }

    // TODO: These methods need to be public for access by internals,
    // TODO: but should not be exposed to users.

    public final void erdoId(int erdoId)
    {
        assert erdoId >= 1 : erdoId;
        if (this.erdoId == ERDO_ID_UNSET) {
            this.erdoId = erdoId;
        } else if (this.erdoId == ERDO_ID_UNSET_DELETED) {
            this.erdoId = erdoId | ERDO_ID_DELETED_MASK;
        } else {
            assert erdoId() == erdoId : erdoId;
        }
    }

    public final void transaction(Transaction transaction)
    {
        assert this.transaction == null : transaction;
        this.transaction = transaction;
        this.timestamp = TIMESTAMP_NOT_SET;
    }

    public final void deleted(boolean deleted)
    {
        if (deleted) {
            if (erdoId == ERDO_ID_UNSET) {
                erdoId = ERDO_ID_UNSET_DELETED;
            } else if (erdoId == ERDO_ID_UNSET_DELETED) {
                // Nothing to do
            } else {
                erdoId |= ERDO_ID_DELETED_MASK;
            }
        } else {
            if (erdoId == ERDO_ID_UNSET) {
                // Nothing to do
            } else if (erdoId == ERDO_ID_UNSET_DELETED) {
                erdoId = ERDO_ID_UNSET;
            } else {
                erdoId &= ~ERDO_ID_DELETED_MASK;
            }
        }
    }

    public final void clearTransactionState()
    {
        transaction = null;
        timestamp = TIMESTAMP_NOT_SET;
    }
    
    public static AbstractKey deserialize(Factory factory, ByteBuffer keyBuffer, int erdoId)
    {
        RecordFactory recordFactory = factory.recordFactory(erdoId);
        AbstractKey key = recordFactory.newKey();
        key.erdoId(erdoId);
        key.readFrom(keyBuffer);
        return key;
    }

    public static AbstractKey deserialize(Factory factory, ByteBuffer keyBuffer, int erdoId, long timestamp)
    {
        RecordFactory recordFactory = factory.recordFactory(erdoId);
        AbstractKey key = recordFactory.newKey();
        key.erdoId(erdoId);
        key.readFrom(keyBuffer);
        key.transactionTimestamp(timestamp);
        return key;
    }

    public static int erdoId(ByteBuffer buffer)
    {
        int erdoId = buffer.getInt(buffer.position());
        assert erdoId != ERDO_ID_UNSET;
        return erdoId;
    }

    public final Transaction transaction()
    {
        return transaction;
    }

    /**
     * Returns a copy of this key. The original and the copy should not share mutable state, so that if one is changed
     * in any way, the other is not affected.
     * @return A copy of this key.
     */
    public abstract AbstractKey copy();

    public final void transactionTimestamp(long timestamp)
    {
        assert this.timestamp == TIMESTAMP_NOT_SET : timestamp;
        this.timestamp = timestamp;
    }

    public final boolean deleted()
    {
        return (erdoId & ERDO_ID_DELETED_MASK) != 0;
    }

    public final int erdoId()
    {
        assert !(erdoId == ERDO_ID_UNSET || erdoId == ERDO_ID_UNSET_DELETED) : this;
        return erdoId & ~ERDO_ID_DELETED_MASK;
    }

    // For use by subclasses
    
    protected AbstractKey(AbstractKey key)
    {
        this.erdoId = key.erdoId;
        this.transaction = key.transaction;
        this.timestamp = key.timestamp;
    }

    protected AbstractKey()
    {}

    // Class state

    private static final long TIMESTAMP_NOT_SET = -1L;
    private static final int ERDO_ID_UNSET = 0;
    private static final int ERDO_ID_UNSET_DELETED = Integer.MIN_VALUE;
    private static final int ERDO_ID_DELETED_MASK = 0x80000000;

    // Object state

    private volatile Transaction transaction;
    private volatile long timestamp = TIMESTAMP_NOT_SET;
    // A valid erdoId is between 1 and Integer.MAX_VALUE inclusive. The stored value is encoded as follows:
    // - High bit is 1 if the key is deleted.
    // - 0 indicates that erdoId has not been assigned, and deleted state has not been set.
    // - MIN_VALUE indicates that erdoId has not been assigned, but deleted state has been set.
    private int erdoId;
}
