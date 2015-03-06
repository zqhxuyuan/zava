/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo;

import com.geophile.erdo.apiimpl.DisklessTestDatabase;
import com.geophile.erdo.util.FileUtil;
import junit.framework.Assert;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class CursorTest
{
    @BeforeClass
    public static void beforeClass()
    {
        FACTORY = new TestFactory();
    }

    @After
    public void after()
    {
        FACTORY.reset();
    }

    @Test
    public void testFindAndNext()
        throws InterruptedException, DeadlockException, TransactionRolledBackException, IOException
    {
        for (int n = 0; n < 100; n++) {
            loadDisklessDatabase(n);
            Cursor cursor;
            TestRecord record;
            TestKey key;
            int expected;
            // Complete cursor
            {
                expected = 0;
                cursor = map.first();
                while ((record = (TestRecord) cursor.next()) != null) {
                    checkRecord(expected++, record);
                }
                assertEquals(n, expected);
            }
            // Random access
            {
                // Test:
                // -    k * GAP - GAP/2 (missing)
                // -    k * GAP (present if k < n, missing if k = n)
                for (int k = 0; k <= n; k++) {
                    // Test missing
                    {
                        int missingKey = k * GAP - GAP / 2;
                        key = new TestKey(missingKey);
                        // test find -> record
                        record = (TestRecord) map.find(key);
                        assertNull(record);
                        // test find -> cursor
                        cursor = map.cursor(key);
                        expected = k;
                        while ((record = (TestRecord) cursor.next()) != null) {
                            checkRecord(expected++, record);
                        }
                        assertEquals(n, expected);
                    }
                    // Test present
                    int presentKey = k * GAP;
                    expected = k;
                    key = new TestKey(presentKey);
                    // test find -> record
                    record = (TestRecord) map.find(key);
                    if (k < n) {
                        checkRecord(expected, record);
                    } else {
                        assertEquals(n, k);
                        assertNull(record);
                    }
                    // test find -> cursor
                    cursor = map.cursor(key);
                    while ((record = (TestRecord) cursor.next()) != null) {
                        checkRecord(expected++, record);
                    }
                    assertEquals(n, expected);
                }
            }
        }
    }

    @Test
    public void testFindAndPrevious()
        throws InterruptedException, DeadlockException, TransactionRolledBackException, IOException
    {
        for (int n = 0; n < 100; n++) {
            // debug("n: %s", n);
            loadDisklessDatabase(n);
            Cursor cursor;
            TestRecord record;
            TestKey key;
            int expected;
            // Complete cursor
            {
                expected = n;
                cursor = map.last();
                while ((record = (TestRecord) cursor.previous()) != null) {
                    // debug("expected: %s, actual: %s", (expected - 1) * GAP, record.key());
                    checkRecord(--expected, record);
                }
                assertEquals(0, expected);
            }
            // Random access
            {
                // Test:
                // -    k * GAP - GAP/2 (missing)
                // -    k * GAP (present if k < n, missing if k = n)
                for (int k = 0; k <= n; k++) {
                    // Test missing
                    {
                        int missingKey = k * GAP - GAP / 2;
                        key = new TestKey(missingKey);
                        // test find -> record
                        record = (TestRecord) map.find(key);
                        assertNull(record);
                        // test find -> cursor
                        cursor = map.cursor(key);
                        expected = k;
                        while ((record = (TestRecord) cursor.previous()) != null) {
                            checkRecord(--expected, record);
                        }
                        assertEquals(0, expected);
                    }
                    // Test present
                    int presentKey = k * GAP;
                    expected = k;
                    key = new TestKey(presentKey);
                    // test find -> record
                    record = (TestRecord) map.find(key);
                    if (k < n) {
                        checkRecord(expected, record);
                        // test find -> cursor
                        cursor = map.cursor(key);
                        while ((record = (TestRecord) cursor.previous()) != null) {
                            checkRecord(expected--, record);
                        }
                        assertEquals(n == 0 ? 0 : -1, expected);
                    } else {
                        assertEquals(n, k);
                        assertNull(record);
                    }
                }
            }
        }
    }

    @Test
    public void testClose() throws InterruptedException, DeadlockException, TransactionRolledBackException, IOException
    {
        // Test close of cursor over empty (which starts out closed)
        {
            loadDisklessDatabase(0);
            Cursor cursor = map.first();
            assertNull(cursor.next());
            cursor.close();
            assertNull(cursor.next());
             cursor.close();
            assertNull(cursor.next());
        }
        // Test repeated close of cursor that wasn't closed to start
        {
            loadDisklessDatabase(10);
            Cursor cursor = map.first();
            assertNotNull(cursor.next());
            assertNotNull(cursor.next());
            cursor.close();
            assertNull(cursor.next());
            cursor.close();
            assertNull(cursor.next());
        }
    }

    @Test
    public void testNextAlternatingWithPrevious()
        throws InterruptedException, DeadlockException, TransactionRolledBackException, IOException
    {
        final int N = 10;
        loadDisklessDatabase(N);
        Cursor cursor = map.first();
        TestRecord record;
        int expected = -1;
        for (int i = 0; i < N; i++) {
            record = (TestRecord) cursor.next();
            checkRecord(++expected, record);
            if (i < N - 1) {
                record = (TestRecord) cursor.next();
                checkRecord(++expected, record);
                record = (TestRecord) cursor.previous();
                checkRecord(--expected, record);
            } else {
                assertNull(cursor.next());
            }
        }
        assertEquals(N - 1, expected);
    }

    @Test
    public void testPreviousAlternatingWithNext()
        throws InterruptedException, DeadlockException, TransactionRolledBackException, IOException
    {
        final int N = 10;
        loadDisklessDatabase(N);
        Cursor cursor = map.last();
        TestRecord record;
        int expected = N;
        for (int i = 0; i < N; i++) {
            record = (TestRecord) cursor.previous();
            checkRecord(--expected, record);
            if (i < N - 1) {
                record = (TestRecord) cursor.previous();
                checkRecord(--expected, record);
                record = (TestRecord) cursor.next();
                checkRecord(++expected, record);
            } else {
                assertNull(cursor.previous());
            }
        }
        assertEquals(0, expected);
    }

    // Inspired by bug #5

    @Test
    public void multipleCursors()
        throws InterruptedException, DeadlockException, TransactionRolledBackException, IOException
    {
        final int N = 100;
        loadDatabase(N);
        Cursor cursor1 = map.first();
        Cursor cursor2 = map.first();
        TestRecord record1;
        TestRecord record2;
        int expected = 0;
        while (expected < N/2) {
            record1 = (TestRecord) cursor1.next();
            record2 = (TestRecord) cursor2.next();
            checkRecord(expected, record1);
            checkRecord(expected, record2);
            expected++;
        }
        cursor1.close();
        while (expected < N) {
            record2 = (TestRecord) cursor2.next();
            checkRecord(expected, record2);
            expected++;
        }
    }

/*
    @Test
    public void closeImmediatelyAfterOpen()
        throws InterruptedException, DeadlockException, TransactionRolledBackException, IOException
    {
        final int N = 100;
        loadDatabase(N);
        Cursor cursor = map.cursor(new TestKey(testKey(-1)));
        map.delete(new TestKey(testKey(0)));
        cursor.close();
    }
*/

    private void loadDisklessDatabase(int n)
        throws IOException, InterruptedException, DeadlockException, TransactionRolledBackException
    {
        // map is loaded with (x * GAP, "r.x"), 0 <= x < n
        Database db = new DisklessTestDatabase(FACTORY);
        map = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        for (int key = 0; key < n; key++) {
            AbstractRecord replaced = map.put(TestRecord.createRecord(testKey(key), testValue(key)));
            Assert.assertNull(replaced);
        }
        db.commitTransaction();
    }

    private void loadDatabase(int n)
        throws IOException, InterruptedException, DeadlockException, TransactionRolledBackException
    {
        // map is loaded with (x * GAP, "r.x"), 0 <= x < n
        final File DB_DIRECTORY = new File(FileUtil.tempDirectory(), DB_NAME);
        FileUtil.deleteDirectory(DB_DIRECTORY);
        Database db = Database.createDatabase(DB_DIRECTORY);
        map = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        for (int key = 0; key < n; key++) {
            AbstractRecord replaced = map.put(TestRecord.createRecord(testKey(key), testValue(key)));
            Assert.assertNull(replaced);
        }
        db.commitTransaction();
    }

    private void checkRecord(int expected, TestRecord record)
    {
        assertNotNull(record);
        assertEquals(testKey(expected), record.key().key());
        assertEquals(testValue(expected), record.stringValue());
    }

    private int testKey(int x)
    {
        return x * GAP;
    }

    private String testValue(int x)
    {
        return String.format("r.%s", x);
    }

    private void debug(String template, Object ... args)
    {
        System.out.println(String.format(template, args));
    }

    private static TestFactory FACTORY;
    private static final String DB_NAME = "erdo";
    private static final String MAP_NAME = "map";
    private static final int GAP = 10;

    private OrderedMap map;
}
