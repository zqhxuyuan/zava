/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo;

import com.geophile.erdo.apiimpl.DatabaseImpl;
import com.geophile.erdo.apiimpl.DatabaseOnDisk;
import com.geophile.erdo.util.FileUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

public class CommitTest
{
    @BeforeClass
    public static void beforeClass()
    {
        FACTORY = new TestFactory();
    }

    @Before
    public void before() throws IOException, InterruptedException
    {
        final File DB_DIRECTORY = new File(FileUtil.tempDirectory(), DB_NAME);
        FileUtil.deleteDirectory(DB_DIRECTORY);
        db = DatabaseOnDisk.createDatabase(DB_DIRECTORY, FACTORY);
    }

    @After
    public void after()
    {
        FACTORY.reset();
    }

    @Test
    public void testSynchronousCommit()
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        OrderedMap map = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        int count = 0;
        while (count < RECORDS) {
            map.ensurePresent(TestRecord.createRecord(count++, FILLER));
            if (count % RECORDS_PER_TRANSACTION == 0) {
/*
                System.out.println(String.format("Commit %s", count));
*/
                db.commitTransaction();
            }
        }
        // Check contents
        Cursor cursor = map.first();
        AbstractRecord record;
        int expected = 0;
        while ((record = cursor.next()) != null) {
            assertEquals(expected++, key(record));
        }
        db.close();
    }

    @Test
    public void testAsynchronousCommit()
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        OrderedMap map = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        final List<Integer> commitCounts = new ArrayList<>();
        TransactionCallback callback =
            new TransactionCallback()
            {
                public void whenDurable(Object commitInfo)
                {
                    commitCounts.add((Integer) commitInfo);
/*
                    System.out.println(String.format("%s: Callback for %s: %s/%s",
                                                     System.currentTimeMillis(), commitInfo, commitCounts.size(), TRANSACTIONS));
*/
                    synchronized (commitCounts) {
                        if (commitCounts.size() == TRANSACTIONS) {
                            commitCounts.notify();
                        }
                    }
                }
            };
        int count = 0;
        while (count < RECORDS) {
            map.ensurePresent(TestRecord.createRecord(count++, FILLER));
            if (count % RECORDS_PER_TRANSACTION == 0) {
/*
                System.out.println(String.format("%s: Commit %s",
                                                 System.currentTimeMillis(), count));
*/
                db.commitTransactionAsynchronously(callback, count);
            }
        }
        db.flush();
        long startWait = System.currentTimeMillis();
        synchronized (commitCounts) {
            while (commitCounts.size() < TRANSACTIONS) {
                commitCounts.wait(1000);
                long now = System.currentTimeMillis();
                if (now - startWait > 10000) {
                    System.out.println(String.format("Waiting too long, commitCounts.size() = %s, TRANSACTIONS = %s",
                                                     commitCounts.size(), TRANSACTIONS));
                    fail();
                }
            }
        }

        Collections.sort(commitCounts);
        int expected = 0;
        for (Integer commitCount : commitCounts) {
            expected += RECORDS_PER_TRANSACTION;
            assertEquals(expected, commitCount.intValue());
        }
/*
        System.out.println("Callbacks complete");
*/
        // Check contents
        Cursor cursor = map.first();
        AbstractRecord record;
        expected = 0;
        while ((record = cursor.next()) != null) {
            assertEquals(expected++, key(record));
        }
        db.close();
    }

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

    private static final String DB_NAME = "erdo";
    private static final String MAP_NAME = "map";
    private static final int RECORDS = 1000000;
    private static final int RECORDS_PER_TRANSACTION = 1000;
    private static final int TRANSACTIONS = RECORDS / RECORDS_PER_TRANSACTION;
    private static final String FILLER =
        "xxxxxxxxxxxxxxxxxxxx" +
        "xxxxxxxxxxxxxxxxxxxx" +
        "xxxxxxxxxxxxxxxxxxxx" +
        "xxxxxxxxxxxxxxxxxxxx" +
        "xxxxxxxxxxxxxxxxxxxx";
    private static TestFactory FACTORY;

    private Configuration configuration;
    private DatabaseImpl db;
}
