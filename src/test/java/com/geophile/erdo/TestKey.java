/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class TestKey extends AbstractKey
{
    // Object interface

    public String toString()
    {
        return String.format("%s/%s", super.toString(), key);
    }

    @Override
    public int hashCode()
    {
        return key * 9987001;
    }

    @Override
    public boolean equals(Object obj)
    {
        return super.equals(obj) && compareTo((AbstractKey)obj) == 0;
    }

    // Comparable interface

    @Override
    public int compareTo(AbstractKey key)
    {
        int c = super.compareTo(key);
        if (c == 0) {
            TestKey that = (TestKey) key;
            long diff = (long) this.key - (long) that.key;
            return diff < 0 ? -1 : diff == 0 ? 0 : 1;
        } else {
            return c;
        }
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

    public void key(int key)
    {
        this.key = key;
    }

    public TestKey(int key)
    {
        this();
        this.key = key;
    }

    public TestKey()
    {
        if (testErdoId != null) {
            erdoId(testErdoId);
        }
/*
        // See discussion of erdoId in TestFactory.
        int erdoId = TestFactory.only().erdoId();
        if (erdoId != -1) {
            erdoId(erdoId);
        }
*/
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
    }

    // Class state

    private static Integer testErdoId;

    // Object state

    private int key;
}
