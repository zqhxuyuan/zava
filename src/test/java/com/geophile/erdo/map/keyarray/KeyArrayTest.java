/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.keyarray;

import com.geophile.erdo.AbstractRecord;
import com.geophile.erdo.RecordFactory;
import com.geophile.erdo.TestFactory;
import com.geophile.erdo.TestKey;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.Assert.*;

public class KeyArrayTest
{
    @BeforeClass
    public static void beforeClass()
    {
        FACTORY = new TestFactory();
        FACTORY.recordFactory(1, RecordFactory.simpleRecordFactory(TestKey1.class, null));
        FACTORY.recordFactory(2, RecordFactory.simpleRecordFactory(TestKey2.class, null));
    }

    @After
    public void after()
    {
        FACTORY.reset();
    }

    @Test
    public void testEmpty()
    {
        KeyArray a = new KeyArray(FACTORY, 1000);
        a.close();
        TestKey1 key = new TestKey1();
        // size
        assertEquals(0, a.size());
        // scan forward
        assertNull(a.cursor(null).next());
        // scan backward
        assertNull(a.cursor(null).next());
        // binary search
        assertEquals(-1, a.binarySearch(key));
        // subscript
        try {
            a.key(0, key);
            fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    public void testNonEmpty()
    {
        final int MAX_N = 100;
        for (int n = 1; n <= MAX_N; n++) {
            // debug("n: %s", n);
            KeyArray a;
            KeyArrayCursor cursor;
            AbstractRecord record;
            int expected;
            TestKey1 key;
            // load
            {
                a = new KeyArray(FACTORY, 1000);
                for (int i = 1; i <= n; i++) {
                    key = key1(i * 10);
                    a.append(key);
                    // debug("    %s", key);
                }
                a.close();
                // check size
                assertEquals(n, a.size());
            }
            // scan forward
            {
                cursor = a.cursor(null);
                expected = 0;
                while ((record = cursor.next()) != null) {
                    expected += 10;
                    assertEquals(expected, ((TestKey1)record.key()).key());
                }
                assertEquals(n * 10, expected);
            }
            // scan backward
            {
                cursor = a.cursor(null);
                expected = n * 10;
                while ((record = cursor.previous()) != null) {
                    assertEquals(expected, ((TestKey1)record.key()).key());
                    expected -= 10;
                }
                assertEquals(0, expected);
            }
            // binary search - search key present
            {
                for (int i = 1; i <= n; i++) {
                    key = key1(i * 10);
                    // forward
                    cursor = a.cursor(key);
                    record = cursor.next();
                    assertNotNull(record);
                    assertEquals(i * 10, ((TestKey1)record.key()).key());
                    // backward
                    cursor = a.cursor(key);
                    record = cursor.previous();
                    assertNotNull(record);
                    assertEquals(i * 10, ((TestKey1)record.key()).key());
                }
            }
            // binary search - search key missing
            {
                for (int i = 1; i <= n + 1; i++) {
                    key = key1(i * 10 - 5);
                    // debug("binary search - missing key: %s", key);
                    TestKey1 above = i <= n ? key1(i * 10) : null;
                    TestKey1 below = i > 1 ? key1((i - 1) * 10) : null;
                    // debug("    above: %s", above);
                    // debug("    below: %s", below);
                    // forward
                    cursor = a.cursor(key);
                    record = cursor.next();
                    if (above == null) {
                        assertNull(record);
                    } else {
                        assertNotNull(record);
                        assertEquals(above, record.key());
                    }
                    // backward
                    cursor = a.cursor(key);
                    record = cursor.previous();
                    if (below == null) {
                         assertNull(record);
                    } else {
                        assertNotNull(record);
                        assertEquals(below, record.key());
                    }
                }
            }
        }
    }
    
    @Test
    public void testMixed()
    {
        final int N = 100;
        for (int n1 = 1; n1 < N; n1++) {
            int n2 = N - n1;
            KeyArray a = new KeyArray(FACTORY, 1000);
            for (int i = 0; i < n1; i++) {
                a.append(key1(i));
            }
            for (int i = 0; i < n2; i++) {
                a.append(key2(i));
            }
            a.close();
            // size
            assertEquals(N, a.size());
            // scan forward
            KeyArrayCursor cursor = a.cursor(null);
            AbstractRecord record;
            int expected = 0;
            while ((record = cursor.next()) != null) {
                if (expected < n1) {
                    assertEquals(expected, ((TestKey)record.key()).key());
                } else {
                    assertEquals(expected - n1, ((TestKey)record.key()).key());
                }
                expected++;
            }
            // binary search
            for (int i = 0; i < n1; i++) {
                assertEquals(i, a.binarySearch(key1(i)));
            }
            for (int i = 0; i < n2; i++) {
                assertEquals(n1 + i, a.binarySearch(key2(i)));
            }
            // subscript
            int i = 0;
            TestKey1 key1 = key1(-1);
            while (i < n1) {
                a.key(i, key1);
                assertEquals(1, key1.erdoId());
                assertEquals(i, key1.key());
                i++;
            }
            TestKey2 key2 = key2(-1);
            while (i < N) {
                a.key(i, key2);
                assertEquals(2, key2.erdoId());
                assertEquals(i - n1, key2.key());
                i++;
            }
        }
    }

    private TestKey1 key1(int k)
    {
        TestKey1 key = new TestKey1();
        key.key(k);
        key.erdoId(1);
        key.transactionTimestamp(100);
        return key;
    }

    private TestKey2 key2(int k)
    {
        TestKey2 key = new TestKey2();
        key.key(k);
        key.erdoId(2);
        key.transactionTimestamp(200);
        return key;
    }

    private void debug(String template, Object ... args)
    {
        System.out.println(String.format(template, args));
    }

    private static TestFactory FACTORY;

    public static class TestKey1 extends TestKey
    {
        public TestKey1()
        {
            erdoId(1);
        }
    }

    public static class TestKey2 extends TestKey
    {
        public TestKey2()
        {
            erdoId(2);
        }
    }
}
