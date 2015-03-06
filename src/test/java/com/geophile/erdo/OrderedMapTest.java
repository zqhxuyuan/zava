/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo;

import com.geophile.erdo.apiimpl.DisklessTestDatabase;
import com.geophile.erdo.util.FileUtil;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.*;

public class OrderedMapTest
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
    public void testPut()
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        Database db = new DisklessTestDatabase(FACTORY);
        OrderedMap map = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        for (int i = 0; i < N; i++) {
            AbstractRecord replaced = map.put(TestRecord.createRecord(i, "first"));
            assertNull(replaced);
        }
        for (int i = 0; i < N; i++) {
            AbstractRecord replaced = map.put(TestRecord.createRecord(i, "second"));
            assertEquals(i, ((TestKey) replaced.key()).key());
            assertEquals("first", ((TestRecord) replaced).stringValue());
        }
        Cursor cursor = map.first();
        TestRecord record;
        int expected = 0;
        while ((record = (TestRecord) cursor.next()) != null) {
            assertEquals(expected++, record.key().key());
            assertEquals("second", record.stringValue());
        }
        assertEquals(N, expected);
        db.close();
    }

    @Test
    public void testEnsurePresent()
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        Database db = new DisklessTestDatabase(FACTORY);
        OrderedMap map = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        for (int i = 0; i < N; i++) {
            map.ensurePresent(TestRecord.createRecord(i, "first"));
        }
        for (int i = 0; i < N; i++) {
            map.ensurePresent(TestRecord.createRecord(i, "second"));
        }
        Cursor cursor = map.first();
        TestRecord record;
        int expected = 0;
        while ((record = (TestRecord) cursor.next()) != null) {
            assertEquals(expected++, ((TestKey) record.key()).key());
            assertEquals("second", record.stringValue());
        }
        assertEquals(N, expected);
        db.close();
    }

    @Test
    public void testDelete()
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        Database db = new DisklessTestDatabase(FACTORY);
        OrderedMap map = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        for (int i = 0; i < N; i++) {
            AbstractRecord replaced = map.put(TestRecord.createRecord(i, "first"));
            assertNull(replaced);
        }
        for (int i = 0; i < N; i++) {
            // Delete present key
            {
                AbstractRecord replaced = map.delete(new TestKey(i));
                assertNotNull(replaced);
                assertEquals(i, ((TestKey) replaced.key()).key());
                assertEquals("first", ((TestRecord) replaced).stringValue());
            }
            // Delete missing key (a version of bug #4)
            {
                AbstractRecord replaced = map.delete(new TestKey(-i));
                assertNull(replaced);
            }
        }
        assertNull(map.first().next());
        db.close();
    }

    @Test
    public void testEnsureDeleted()
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        Database db = new DisklessTestDatabase(FACTORY);
        OrderedMap map = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        for (int i = 0; i < N; i++) {
            map.ensurePresent(TestRecord.createRecord(i, "first"));
        }
        for (int i = 0; i < N; i++) {
            map.ensureAbsent(new TestKey(i));
        }
        assertNull(map.first().next());
        db.close();
    }

    @Test
    public void testScan()
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        // Based on SealedMapTest.testScan
        Database db = new DisklessTestDatabase(FACTORY);
        OrderedMap map = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        Cursor cursor;
        int expectedKey;
        int expectedLastKey;
        boolean expectedEmpty;
        int gap = 10;
        AbstractRecord record;
        // Load
        for (int i = 0; i < N; i++) {
            map.put(TestRecord.createRecord(i * gap, null));
        }
        // Full cursor
        cursor = map.first();
        expectedKey = 0;
        while ((record = cursor.next()) != null) {
            assertEquals(expectedKey, key(record));
            expectedKey += gap;
        }
        assertEquals(N * gap, expectedKey);
        // Try scans starting at, before, and after each key and ending at, before and after each key.
        for (int i = 0; i < N; i++) {
            int startBase = gap * i;
            int endBase = gap * (N - 1 - i);
            for (int start = startBase - 1; start <= startBase + 1; start++) {
                for (int end = endBase - 1; end <= endBase + 1; end++) {
                    if (start <= end) {
                        TestKey endKey = new TestKey(end);
                        cursor = map.cursor(new TestKey(start));
                        expectedKey = start <= startBase ? startBase : startBase + gap;
                        expectedLastKey = end >= endBase ? endBase : endBase - gap;
                        expectedEmpty = start > end || start <= end && (end >= startBase || start <= endBase);
                        boolean empty = true;
                        while ((record = cursor.next()) != null && record.key().compareTo(endKey) <= 0) {
                            assertEquals(expectedKey, key(record));
                            expectedKey += gap;
                            empty = false;
                        }
                        if (empty) {
                            assertTrue(expectedEmpty);
                        } else {
                            assertEquals(expectedLastKey + gap, expectedKey);
                        }
                    }
                }
            }
        }
        db.close();
    }

    // Problem uncovered while working on geophile-erdo. Bug #1.

    @Test
    public void testBackwardFromBeginning()
        throws IOException, InterruptedException, DeadlockException, TransactionRolledBackException
    {
        Database db = new DisklessTestDatabase(FACTORY);
        OrderedMap map = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        for (int i = 0; i < 10; i++) {
            map.ensurePresent(TestRecord.createRecord(i, null));
        }
        // map is represented by an empty forest and a PrivateMap containing updates. These will be combined
        // by a MergeCursor intent on going forward.
        Cursor cursor = map.cursor(new TestKey(-1));
        TestRecord record = (TestRecord) cursor.previous();
        assertNull(record);
    }

    @Test
    public void testForwardFromEnd()
        throws IOException, InterruptedException, DeadlockException, TransactionRolledBackException
    {
        Database db = new DisklessTestDatabase(FACTORY);
        OrderedMap map = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        for (int i = 0; i < 10; i++) {
            map.ensurePresent(TestRecord.createRecord(i, null));
        }
        // map is represented by an empty forest and a PrivateMap containing updates. These will be combined
        // by a MergeCursor intent on going forward.
        Cursor cursor = map.cursor(new TestKey(99));
        TestRecord record = (TestRecord) cursor.next();
        assertNull(record);
    }

    // End of tests for bug #1.

    // Bug #3
    @Test
    public void testPutAfterDelete()
        throws IOException, InterruptedException, DeadlockException, TransactionRolledBackException
    {
        Database db = new DisklessTestDatabase(FACTORY);
        OrderedMap map = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        TestRecord record = TestRecord.createRecord(100, null);
        TestRecord replaced = (TestRecord) map.put(record);
        assertNull(replaced);
        TestRecord removed = (TestRecord) map.delete(record.key());
        assertTrue(!removed.deleted());
        // Comparing TestKey objects doesn't work. record.key() has no erdoId.
        assertEquals(record.key().key(), removed.key().key());
        replaced = (TestRecord) map.put(record);
        assertNull(replaced);
    }

    // Check for another version of bug #3
    @Test
    public void testIterationOverMapWithDeletedRecords()
        throws IOException, InterruptedException, DeadlockException, TransactionRolledBackException
    {
        final int N = 10;
        Database db = new DisklessTestDatabase(FACTORY);
        OrderedMap map = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        TestRecord[] records = new TestRecord[N];
        for (int i = 0; i < N; i++) {
            records[i] = TestRecord.createRecord(i, null);
            map.ensurePresent(records[i]);
        }
        int firstExpected = 0;
        TestKey key0 = new TestKey(0);
        for (int i = 0; i < N; i++) {
            // Check contents before deletion
            Cursor cursor = map.cursor(key0);
            TestRecord record;
            int expected = firstExpected;
            while ((record = (TestRecord) cursor.next()) != null) {
                assertEquals(expected, record.key().key());
                expected++;
            }
            assertEquals(N, expected);
            // delete key i
            TestRecord replaced = (TestRecord) map.delete(new TestKey(i));
            assertEquals(i, replaced.key().key());
            firstExpected++;
            // Check contents after deletion
            cursor = map.cursor(key0);
            expected = firstExpected;
            while ((record = (TestRecord) cursor.next()) != null) {
                assertEquals(expected, record.key().key());
                expected++;
            }
            assertEquals(N, expected);
        }
    }

    // Bug #4

    @Test
    public void testMoreDeletion()
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        final File DB_DIRECTORY = new File(FileUtil.tempDirectory(), "erdo");
        FileUtil.deleteDirectory(DB_DIRECTORY);
        Database db = Database.createDatabase(DB_DIRECTORY);
        OrderedMap map = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        for (int i = 0; i < N; i++) {
            AbstractRecord replaced = map.put(TestRecord.createRecord(i, String.format("v%s", i)));
            assertNull(replaced);
        }
        // Delete odd keys
        for (int i = 1; i < N; i += 2) {
            TestRecord replaced = (TestRecord) map.delete(new TestKey(i));
            assertNotNull(replaced);
            assertEquals(i, replaced.key().key());
        }
        db.commitTransaction();
        db.flush();
        // Check that the expected even keys remain
        assertEquals(0, N % 2);
        int evenCount = 0;
        for (int i = 0; i < N; i += 2) {
            TestRecord record = (TestRecord) map.find(new TestKey(i));
            assertEquals(i, record.key().key());
            evenCount++;
        }
        assertEquals(N/2, evenCount);
        // Delete them again, even though they aren't there
        for (int i = 1; i < N; i += 2) {
            TestRecord replaced = (TestRecord) map.delete(new TestKey(i));
            assertNull(replaced);
        }
        db.close();
    }

    @Test
    public void testEvenMoreDeletion()
        throws IOException, InterruptedException, DeadlockException, TransactionRolledBackException
    {
        final File DB_DIRECTORY = new File(FileUtil.tempDirectory(), "erdo");
        FileUtil.deleteDirectory(DB_DIRECTORY);
        // Make background consolidation happen more often
        Configuration configuration = Configuration.defaultConfiguration();
        configuration.consolidationThreads(1);
        configuration.consolidationMinSizeBytes(100);
        configuration.consolidationMinMapsToConsolidate(2);
        Database db = Database.createDatabase(DB_DIRECTORY, configuration);
        OrderedMap map = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        int txn = 0;
        {
            // Load some records
            int id = 0;
            for (int t = 0; t < 100; t++) {
                String transactionName = String.format("t%s", txn++);
                for (int i = 0; i < 100; i++) {
                    AbstractRecord replaced = map.put(TestRecord.createRecord(id++, transactionName));
                    assertNull(replaced);
                }
                db.commitTransaction();
            }
        }
        {
            // Delete them all
            int id = 0;
            for (int t = 0; t < 100; t++) {
                txn++;
                for (int i = 0; i < 100; i++) {
                    TestRecord deleted = (TestRecord) map.delete(new TestKey(id));
                    assertNotNull(deleted);
                    assertEquals(id, deleted.key().key());
                    id++;
                }
                db.commitTransaction();
            }
        }
        {
            // Check empty
            Cursor cursor = map.first();
            while (cursor.next() != null) {
                fail();
            }
        }
        db.close();
    }

    // End testing of bug #4

    private int key(AbstractRecord record)
    {
        return ((TestKey) record.key()).key();
    }

    private void dump(String label, OrderedMap map) throws IOException, InterruptedException
    {
        System.out.println(label);
        Cursor cursor = map.first();
        AbstractRecord record;
        while ((record = cursor.next()) != null) {
            System.out.println(String.format("    %s, deleted: %s", record, record.deleted()));
        }
    }

    private static TestFactory FACTORY;
    private static final String MAP_NAME = "map";
    private static final int N = 10;
}
