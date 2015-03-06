/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.AbstractRecord;
import com.geophile.erdo.RecordFactory;
import com.geophile.erdo.map.Factory;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class IndexRecord extends AbstractRecord<AbstractKey>
{
    // AbstractRecord interface

    @Override
    public long estimatedSizeBytes()
    {
        assert false;
        return -1L;
    }

    @Override
    public void readFrom(ByteBuffer buffer) throws BufferUnderflowException
    {
        super.readFrom(buffer);
        childPageAddress = buffer.getInt();
    }

    @Override
    public void writeTo(ByteBuffer buffer) throws BufferOverflowException
    {
        super.writeTo(buffer);
        buffer.putInt(childPageAddress);
    }

    @Override
    public AbstractRecord copy()
    {
        throw new UnsupportedOperationException();
    }

    // LazyRecord interface (key() is provided by AbstractRecord)

    @Override
    public ByteBuffer keyBuffer() throws IOException, InterruptedException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public AbstractRecord materializeRecord() throws IOException, InterruptedException
    {
        return this;
    }

    @Override
    public ByteBuffer recordBuffer() throws IOException, InterruptedException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean prefersSerialized()
    {
        return false;
    }

    // IndexRecord interface

    public int childPageAddress()
    {
        return childPageAddress;
    }

    public void childPageAddress(int childPageAddress)
    {
        this.childPageAddress = childPageAddress;
    }

    public static IndexRecord deserialize(Factory factory, DiskPage.AccessBuffers pageAccessBuffers, int erdoId)
    {
        // key
        ByteBuffer keyBuffer = pageAccessBuffers.keyBuffer();
        ByteBuffer recordBuffer = pageAccessBuffers.recordBuffer();
        keyBuffer.mark();
        recordBuffer.mark();
        try {
            RecordFactory recordFactory = factory.recordFactory(erdoId);
            AbstractKey key = recordFactory.newKey();
            key.erdoId(erdoId);
            key.readFrom(pageAccessBuffers.keyBuffer());
            // record
            IndexRecord record = new IndexRecord(key);
            record.readFrom(pageAccessBuffers.recordBuffer());
            return record;
        } finally {
            keyBuffer.reset();
            recordBuffer.reset();
        }
    }

    public IndexRecord(AbstractKey key, int childPageAddress)
    {
        this(key);
        this.childPageAddress = childPageAddress;
    }

    // For use by this class

    private IndexRecord(AbstractKey key)
    {
        super(key);
    }

    // State

    private int childPageAddress;
}