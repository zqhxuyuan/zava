/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo;

import com.geophile.erdo.apiimpl.DisklessTestDatabase;
import com.geophile.erdo.transaction.Transaction;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.Assert.assertNull;

public class TransactionOptionsTest
{
    @BeforeClass
    public static void beforeClass()
    {
        Configuration configuration = Configuration.defaultConfiguration();
        configuration.consolidationMinSizeBytes(0);
        FACTORY = new TestFactory(configuration);
        Transaction.initialize(FACTORY);
    }
    
    @Before
    public void before() throws IOException, InterruptedException
    {
        db = new DisklessTestDatabase(FACTORY);
    }
    
    @After
    public void after() throws IOException, InterruptedException
    {
        db.close();
        FACTORY.reset();
    }

    @Test
    public void testSynchronousCommitNoUpdate() throws IOException, InterruptedException
    {
        OrderedMap map = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        db.commitTransaction();
        Cursor cursor = map.first();
        assertNull(cursor.next());
    }

    @Test
    public void testSynchronousCommit()
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        final int N = 100000;
        OrderedMap map = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        TestRecord record;
        for (int i = 0; i < N; i++) {
            record = TestRecord.createRecord(i, null);
            map.put(record);
        }
        db.commitTransaction();
        Cursor cursor = map.first();
        int expected = 0;
        while ((record = (TestRecord) cursor.next()) != null) {
            Assert.assertEquals(expected++, ((TestKey) record.key()).key());
        }
        Assert.assertEquals(N, expected);
    }

    @Test
    public void testAsynchronousCommit()
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        final int N = 1000;
        final AtomicInteger lock = new AtomicInteger(0);
        TransactionCallback callback =
            new TransactionCallback()
            {
                public void whenDurable(Object commitInfo)
                {
                    synchronized (lock) {
                        lock.incrementAndGet();
                        lock.notify();
                    }
                }
            };
        OrderedMap map = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        TestRecord record;
        int i = 0;
        for (; i < N; i++) {
            record = TestRecord.createRecord(i, null);
            map.put(record);
            db.commitTransactionAsynchronously(callback, i);
        }
        synchronized (lock) {
            while (lock.get() < N) {
                lock.wait();
            }
        }
        Cursor cursor = map.first();
        int expected = 0;
        while ((record = (TestRecord) cursor.next()) != null) {
            Assert.assertEquals(expected++, ((TestKey) record.key()).key());
        }
        Assert.assertEquals(N, expected);
    }

    @Test
    public void testRollbackNoUpdate() throws IOException, InterruptedException
    {
        OrderedMap map = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        db.rollbackTransaction();
        Cursor cursor = map.first();
        assertNull(cursor.next());
    }

    private static TestFactory FACTORY;
    private static final String MAP_NAME = "test";

    private Database db;
}
