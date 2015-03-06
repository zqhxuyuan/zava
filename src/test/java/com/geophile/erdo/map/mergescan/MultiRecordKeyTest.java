/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.mergescan;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

public class MultiRecordKeyTest
{
    @BeforeClass
    public static void beforeClass() throws IOException
    {
        TestKey.testErdoId(ERDO_ID);
    }

    @Test
    public void badKeyRange()
    {
        try {
            keyRange(10, 10);
            fail();
        } catch (AssertionError e) {
        }
        try {
            keyRange(10, 5);
            fail();
        } catch (AssertionError e) {
        }
        try {
            keyRange(null, 10);
            fail();
        } catch (AssertionError e) {
        }
    }

    @Test
    public void testXNXN()
    {
        assertEquals(0, keyRange(10, null).compareTo(keyRange(9, null)));
        assertEquals(0, keyRange(10, null).compareTo(keyRange(10, null)));
        assertEquals(0, keyRange(10, null).compareTo(keyRange(11, null)));
    }

    @Test
    public void testXNXX()
    {
        assertEquals(1, keyRange(10, null).compareTo(keyRange(8, 9)));
        assertEquals(1, keyRange(10, null).compareTo(keyRange(8, 10)));
        assertEquals(0, keyRange(10, null).compareTo(keyRange(8, 11)));
        assertEquals(1, keyRange(10, null).compareTo(keyRange(9, 10)));
        assertEquals(0, keyRange(10, null).compareTo(keyRange(9, 11)));
        assertEquals(0, keyRange(10, null).compareTo(keyRange(10, 11)));
        assertEquals(0, keyRange(10, null).compareTo(keyRange(11, 12)));
    }

    @Test
    public void testXXXN()
    {
        assertEquals(0, keyRange(10, 20).compareTo(keyRange(9, null)));
        assertEquals(0, keyRange(10, 20).compareTo(keyRange(10, null)));
        assertEquals(0, keyRange(10, 20).compareTo(keyRange(11, null)));
        assertEquals(0, keyRange(10, 20).compareTo(keyRange(19, null)));
        assertEquals(-1, keyRange(10, 20).compareTo(keyRange(20, null)));
        assertEquals(-1, keyRange(10, 20).compareTo(keyRange(21, null)));
    }

    @Test
    public void testXXXX()
    {
        assertEquals(1, keyRange(10, 20).compareTo(keyRange(5, 9)));
        assertEquals(1, keyRange(10, 20).compareTo(keyRange(5, 10)));
        assertEquals(0, keyRange(10, 20).compareTo(keyRange(5, 11)));
        assertEquals(0, keyRange(10, 20).compareTo(keyRange(9, 19)));
        assertEquals(0, keyRange(10, 20).compareTo(keyRange(9, 20)));
        assertEquals(0, keyRange(10, 20).compareTo(keyRange(9, 21)));
        assertEquals(0, keyRange(10, 20).compareTo(keyRange(10, 19)));
        assertEquals(0, keyRange(10, 20).compareTo(keyRange(10, 20)));
        assertEquals(0, keyRange(10, 20).compareTo(keyRange(10, 21)));
        assertEquals(0, keyRange(10, 20).compareTo(keyRange(11, 19)));
        assertEquals(0, keyRange(10, 20).compareTo(keyRange(11, 20)));
        assertEquals(0, keyRange(10, 20).compareTo(keyRange(11, 21)));
        assertEquals(0, keyRange(10, 20).compareTo(keyRange(19, 30)));
        assertEquals(-1, keyRange(10, 20).compareTo(keyRange(20, 30)));
        assertEquals(-1, keyRange(10, 20).compareTo(keyRange(21, 30)));
    }

    private MultiRecordKey keyRange(Integer lo, Integer hi)
    {
        TestKey loKey = lo == null ? null : new TestKey(lo);
        TestKey hiKey = hi == null ? null : new TestKey(hi);
        return new MultiRecordKey(loKey, hiKey);
    }

    private static final int ERDO_ID = 1;
}
