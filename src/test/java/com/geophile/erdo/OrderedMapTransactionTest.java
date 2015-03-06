/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo;

import com.geophile.erdo.apiimpl.DisklessTestDatabase;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNull;

public class OrderedMapTransactionTest
{
    @Before
    public void before() throws IOException, InterruptedException
    {
        db = new DisklessTestDatabase(FACTORY);
        map = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
    }

    @After
    public void after()
    {
        FACTORY.reset();
    }

    @Test
    public void testOneMapOneTransaction()
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        TestRecord record;
        for (int i = 0; i < N; i++) {
            record = TestRecord.createRecord(i, null);
            map.put(record);
        }
        Cursor cursor = map.first();
        int expected = 0;
        while ((record = (TestRecord) cursor.next()) != null) {
            Assert.assertEquals(expected++, record.key().key());
        }
        Assert.assertEquals(N, expected);
        db.close();
    }

    @Test
    public void testOneMapTransactionAfterLoading()
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        for (int i = 0; i < N; i++) {
            map.put(TestRecord.createRecord(i, null));
        }
        db.commitTransaction();
        Cursor cursor = map.first();
        TestRecord record;
        int expected = 0;
        while ((record = (TestRecord) cursor.next()) != null) {
            Assert.assertEquals(expected++, record.key().key());
        }
        Assert.assertEquals(N, expected);
        db.close();
    }

    @Test
    public void testOneMapRollback()
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        for (int i = 0; i < N; i++) {
            map.put(TestRecord.createRecord(i, null));
        }
        db.rollbackTransaction();
        Cursor cursor = map.first();
        while (cursor.next() != null) {
            Assert.assertTrue(false);
        }
        db.close();
    }

    @Test
    public void testMultipleTransactions()
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        for (int t = 0; t < TRANSACTIONS; t++) {
            for (int i = 0; i < N; i++) {
                int key = t * N + i;
                map.put(TestRecord.createRecord(key, null));
            }
            // commit even-numbered txns, rollback odd-numbered txns.
            if (t % 2 == 0) {
                db.commitTransaction();
            } else {
                db.rollbackTransaction();
            }
            Cursor cursor = map.first();
            TestRecord record;
            int expected = 0;
            while ((record = (TestRecord) cursor.next()) != null) {
                int recordTransaction = expected / N;
                if (recordTransaction % 2 == 1 && expected % N == 0) {
                    expected += N;
                }
                Assert.assertEquals(expected, record.key().key());
                expected++;
            }
            if (t % 2 == 0) {
                Assert.assertEquals((t + 1) * N, expected);
            } else {
                Assert.assertEquals(t * N, expected);
            }
        }
        db.close();
    }

    @Test
    public void testScanNextAfterCommit()
        throws IOException, DeadlockException, TransactionRolledBackException, InterruptedException
    {
        for (int i = 0; i < N; i++) {
            map.put(TestRecord.createRecord(i, null));
        }
        Cursor cursor = map.first();
        db.commitTransaction();
        assertNull(cursor.next());
    }

    @Test
    public void testScanNextAfterRollback()
        throws IOException, DeadlockException, TransactionRolledBackException, InterruptedException
    {
        for (int i = 0; i < N; i++) {
            map.put(TestRecord.createRecord(i, null));
        }
        Cursor cursor = map.first();
        db.rollbackTransaction();
        assertNull(cursor.next());
    }

    @Test
    public void testScanCloseAfterCommit()
        throws IOException, DeadlockException, TransactionRolledBackException, InterruptedException
    {
        for (int i = 0; i < N; i++) {
            map.put(TestRecord.createRecord(i, null));
        }
        Cursor cursor = map.first();
        db.commitTransaction();
        cursor.close();
        assertNull(cursor.next());
    }

    @Test
    public void testScanCloseAfterRollback()
        throws IOException, DeadlockException, TransactionRolledBackException, InterruptedException
    {
        for (int i = 0; i < N; i++) {
            map.put(TestRecord.createRecord(i, null));
        }
        Cursor cursor = map.first();
        db.rollbackTransaction();
        cursor.close();
        assertNull(cursor.next());
    }

    private static final TestFactory FACTORY = new TestFactory();
    private static final int N = 10;
    private static final int TRANSACTIONS = 100;
    private static final String MAP_NAME = "map";

    private Database db;
    private OrderedMap map;
}
