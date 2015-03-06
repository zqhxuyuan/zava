/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class TestRecord extends AbstractRecord<TestKey>
{
    // Object interface

    public String toString()
    {
        return String.format("%s%s", super.toString(), value);
    }

    // AbstractRecord interface

    @Override
    public long estimatedSizeBytes()
    {
        return
            super.estimatedSizeBytes() +
            1 +
            (value == null
             ? 0 :
             value instanceof String
             ? stringValue().length()
             : bytesValue().length);
    }

    public void readFrom(ByteBuffer buffer) throws BufferUnderflowException
    {
        super.readFrom(buffer);
        byte type = buffer.get();
        if (type == VALUE_NULL) {
            value = null;
        } else {
            int size = buffer.getInt();
            byte[] bytes = new byte[size];
            buffer.get(bytes);
            value = type == VALUE_STRING ? new String(bytes) : bytes;
        }
    }

    public void writeTo(ByteBuffer buffer) throws BufferOverflowException
    {
        super.writeTo(buffer);
        byte[] bytes = null;
        if (value == null) {
            buffer.put(VALUE_NULL);
        } else if (value instanceof String) {
            buffer.put(VALUE_STRING);
            bytes = stringValue().getBytes();
        } else {
            buffer.put(VALUE_BYTE_ARRAY);
            bytes = bytesValue();
        }
        if (bytes != null) {
            buffer.putInt(bytes.length);
            buffer.put(bytes);
        }
    }

    @Override
    public AbstractRecord copy()
    {
        return new TestRecord(this);
    }

    // TestRecord interface

    public void value(Object value)
    {
        this.value = value;
    }

    public String stringValue()
    {
        return (String) value;
    }

    public byte[] bytesValue()
    {
        return (byte[]) value;
    }

    public static TestRecord createRecord(int key)
    {
        return new TestRecord(key);
    }

    public static TestRecord createRecord(int key, Object value)
    {
        TestRecord record = new TestRecord(key);
        record.value(value);
        return record;
    }

    public TestRecord(int key)
    {
        super(new TestKey(key));
    }

    public TestRecord(AbstractKey key)
    {
        super((TestKey) key);
    }

    public TestRecord()
    {}
    
    // For use by this class
    
    private TestRecord(TestRecord record)
    {
        super(record);
        this.value = record.value;
    }

    // Class state

    private static final byte VALUE_NULL = 0;
    private static final byte VALUE_STRING = 1;
    private static final byte VALUE_BYTE_ARRAY = 2;

    // Object state

    private Object value;
}
