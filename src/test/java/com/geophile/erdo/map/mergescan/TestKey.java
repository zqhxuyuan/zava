/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.mergescan;

import com.geophile.erdo.AbstractKey;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class TestKey extends AbstractKey
{
    // Object interface

    public String toString()
    {
        return String.format("%s/%s", key, id);
    }

    @Override
    public int hashCode()
    {
        return key()  * 9987001;
    }

    // Comparable interface

    @Override
    public int compareTo(AbstractKey key)
    {
        int c = 0; // super.compareTo(key) breaks tests
        if (c == 0) {
            TestKey that = (TestKey) key;
            c = this.key < that.key ? -1 : this.key == that.key ? 0 : 1;
        }
        return c;
    }

    // Transferrable interface

    @Override
    public void readFrom(ByteBuffer buffer) throws BufferUnderflowException
    {
        super.readFrom(buffer);
        key = buffer.getInt();
    }

    @Override
    public void writeTo(ByteBuffer buffer) throws BufferOverflowException
    {
        super.writeTo(buffer);
        buffer.putInt(key);
    }

    // AbstractKey interface

    @Override
    public int estimatedSizeBytes()
    {
        return 4;
    }

    @Override
    public AbstractKey copy()
    {
        return new TestKey(this);
    }

    // TestKey interface

    public int key()
    {
        return key;
    }

    public int id()
    {
        return id;
    }

    public void key(int key)
    {
        this.key = key;
    }

    public TestKey(int key)
    {
        this();
        this.key = key;
        this.id = idCounter++;
    }

    public TestKey()
    {
        if (testErdoId != null) {
            erdoId(testErdoId);
        }
    }

    public static void testErdoId(Integer erdoId)
    {
        TestKey.testErdoId = erdoId;
    }
    
    // For use by this class
    
    private TestKey(TestKey key)
    {
        super(key);
        this.key = key.key;
        this.id = key.id;
    }

    // Class state

    private static Integer testErdoId;
    private static int idCounter = 0;

    // Object state

    private int key;
    private int id;
}
