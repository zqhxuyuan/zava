/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.mergescan;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.AbstractRecord;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class TestRecord extends AbstractRecord
{
    // Object interface

    public String toString()
    {
        return key().toString();
    }

    // AbstractRecord interface

    @Override
    public long estimatedSizeBytes()
    {
        return super.estimatedSizeBytes() + 1 + (value == null ? 0 : value.length());
    }

    public void readFrom(ByteBuffer buffer) throws BufferUnderflowException
    {
        super.readFrom(buffer);
        int size = buffer.getInt();
        byte[] bytes = new byte[size];
        buffer.get(bytes);
        value = new String(bytes);
    }

    public void writeTo(ByteBuffer buffer) throws BufferOverflowException
    {
        super.writeTo(buffer);
        byte[] bytes = value().getBytes();
        buffer.putInt(bytes.length);
        buffer.put(bytes);
    }

    // TestRecord interface

    public void value(String value)
    {
        this.value = value;
    }

    public String value()
    {
        return value;
    }

    @Override
    public AbstractRecord copy()
    {
        return new TestRecord(this);
    }

    public TestRecord()
    {}

    public TestRecord(AbstractKey key)
    {
        super(key);
    }

    public TestRecord(AbstractKey key, String value)
    {
        super(key);
        this.value = value;
    }
    
    // For use by this class
    
    private TestRecord(TestRecord record)
    {
        super(record);
        this.value = record.value;
    }

    // Object state

    private String value;
}
