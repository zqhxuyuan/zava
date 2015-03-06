/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo;

import com.geophile.erdo.apiimpl.DeletedRecord;
import com.geophile.erdo.map.Factory;
import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.diskmap.DiskPage;
import com.geophile.erdo.transaction.Transaction;
import com.geophile.erdo.util.Transferrable;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * AbstractRecord is the base class for Erdo records. Applications need to provide subclasses that contain record state,
 * serialize and deserialize, copy a record, provide an estimate of serialized size.
 *
 * This class contains several undocumented methods. These need to be public for implementation reasons, but are not
 * intended to be used by applications or by AbstractKey subclasses.
 */

public abstract class AbstractRecord<KEY extends AbstractKey> extends LazyRecord<KEY> implements Transferrable
{
    // Object interface

    @Override
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append('(');
        buffer.append(key().toString());
        Transaction transaction = key.transaction();
        if (transaction != null) {
            buffer.append("/");
            buffer.append(transaction.toString());
        }
        buffer.append(')');
        return buffer.toString();
    }

    // Transferrable interface

    /**
     * Read the state of this record from the given buffer.
     * @param buffer contains the serialized state of the record.
     * @throws java.nio.BufferUnderflowException
     */
    public void readFrom(ByteBuffer buffer)
    {}

    /**
     * Write the state of this record to the given buffer.
     * @param buffer container of the serialized record.
     * @throws java.nio.BufferOverflowException
     */
    public void writeTo(ByteBuffer buffer)
    {}

    public int recordCount()
    {
        return 1;
    }

    // LazyRecord interface

    @Override
    public final KEY key()
    {
        return key;
    }

    @Override
    public ByteBuffer keyBuffer() throws IOException, InterruptedException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public AbstractRecord<KEY> materializeRecord() throws IOException, InterruptedException
    {
        return this;
    }

    @Override
    public ByteBuffer recordBuffer() throws IOException, InterruptedException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public long estimatedSizeBytes()
    {
        return key.estimatedSizeBytes();
    }

    @Override
    public boolean prefersSerialized()
    {
        return false;
    }

    @Override
    public void destroyRecordReference()
    {
    }

    // AbstractRecord interface

    public boolean deleted()
    {
        return key.deleted();
    }

    /**
     * Returns a copy of this record. The original and the copy should not share mutable state, so that if one is changed
     * in any way, the other is not affected.
     * @return A copy of this record.
     */
    public abstract AbstractRecord<KEY> copy();

    public static AbstractRecord deserialize(Factory factory,
                                             DiskPage.AccessBuffers pageAccessBuffers,
                                             int erdoId,
                                             long timestamp)
    {
        ByteBuffer keyBuffer = pageAccessBuffers.keyBuffer();
        ByteBuffer recordBuffer = pageAccessBuffers.recordBuffer();
        keyBuffer.mark();
        recordBuffer.mark();
        try {
            // key
            AbstractKey key = factory.recordFactory(erdoId).newKey();
            key.erdoId(erdoId);
            key.readFrom(keyBuffer);
            key.transactionTimestamp(timestamp);
            // record
            AbstractRecord record;
            if (key.deleted()) {
                record = new DeletedRecord(key);
            } else {
                record = factory.recordFactory(erdoId).newRecord();
                record.key = key;
                record.readFrom(recordBuffer);
            }
            return record;
        } finally {
            keyBuffer.reset();
            recordBuffer.reset();
        }
    }

    // For use by subclasses

    protected AbstractRecord()
    {}

    protected AbstractRecord(boolean deleted)
    {
        key.deleted(deleted);
    }

    protected AbstractRecord(KEY key)
    {
        this.key = key;
    }
    
    protected AbstractRecord(AbstractRecord record)
    {
        key = (KEY) record.key.copy();
    }

    // Object state

    private KEY key;
}
