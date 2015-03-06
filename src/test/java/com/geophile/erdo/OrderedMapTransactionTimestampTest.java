/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo;

import com.geophile.erdo.map.diskmap.DBStructure;
import com.geophile.erdo.map.diskmap.Manifest;
import com.geophile.erdo.transaction.TimestampSet;
import com.geophile.erdo.util.FileUtil;
import com.geophile.erdo.util.Interval;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.junit.Assert.assertEquals;

// Transaction timestamps should be assigned without holes, even with read-only transactions.

public class OrderedMapTransactionTimestampTest
{
    @BeforeClass
    public static void beforeClass() throws IOException
    {
        DB_DIRECTORY = new File(FileUtil.tempDirectory(), DB_NAME);
    }

    @Before
    public void before()
    {
        FileUtil.deleteDirectory(DB_DIRECTORY);
    }

    @After
    public void after()
    {
        FACTORY.reset();
    }

    @Test
    public void testAllUpdates()
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        Database db = Database.createDatabase(DB_DIRECTORY, Configuration.defaultConfiguration());
        OrderedMap map = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        TestRecord record;
        final int N = 10;
        for (int i = 0; i < N; i++) {
            record = TestRecord.createRecord(i, null);
            map.put(record);
            db.commitTransaction();
        }
        db.close();
        TimestampSet allTimestamps = timestamps(manifests());
        int count = 0;
        long minTimestamp = Long.MAX_VALUE;
        long maxTimestamp = Long.MIN_VALUE;
        for (Interval interval : allTimestamps) {
            minTimestamp = min(minTimestamp, interval.min());
            maxTimestamp = max(maxTimestamp, interval.max());
            count++;
        }
        assertEquals(1, count);
        assertEquals(0, minTimestamp);
        assertEquals(N - 1, maxTimestamp);
    }

    @Test
    public void testUpdatesAndReads()
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        Database db = Database.createDatabase(DB_DIRECTORY, Configuration.defaultConfiguration());
        OrderedMap map = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        TestRecord record;
        final int N = 10;
        for (int i = 0; i < N; i++) {
            // Write transaction
            record = TestRecord.createRecord(i, null);
            map.put(record);
            db.commitTransaction();
            // Read transaction
            Cursor cursor = map.first();
            while (cursor.next() != null);
            db.commitTransaction();
        }
        db.close();
        TimestampSet allTimestamps = timestamps(manifests());
        int count = 0;
        long minTimestamp = Long.MAX_VALUE;
        long maxTimestamp = Long.MIN_VALUE;
        for (Interval interval : allTimestamps) {
            minTimestamp = min(minTimestamp, interval.min());
            maxTimestamp = max(maxTimestamp, interval.max());
            count++;
        }
        assertEquals(1, count);
        assertEquals(0, minTimestamp);
        assertEquals(2 * N - 1, maxTimestamp);
    }

    @Test
    public void testUpdatesAndRollbacks()
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        Database db = Database.createDatabase(DB_DIRECTORY, Configuration.defaultConfiguration());
        OrderedMap map = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        TestRecord record;
        final int N = 10;
        for (int i = 0; i < N; i++) {
            // commit an update
            record = TestRecord.createRecord(i, null);
            map.put(record);
            db.commitTransaction();
            // rollback an update
            record = TestRecord.createRecord(i, null);
            map.put(record);
            db.rollbackTransaction();
        }
        db.close();
        TimestampSet allTimestamps = timestamps(manifests());
        // System.out.println(allTimestamps);
        int count = 0;
        long minTimestamp = Long.MAX_VALUE;
        long maxTimestamp = Long.MIN_VALUE;
        for (Interval interval : allTimestamps) {
            minTimestamp = min(minTimestamp, interval.min());
            maxTimestamp = max(maxTimestamp, interval.max());
            count++;
        }
        assertEquals(1, count);
        assertEquals(0, minTimestamp);
        assertEquals(N - 1, maxTimestamp);
    }

    private TimestampSet timestamps(List<Manifest> manifests)
    {
        TimestampSet allTimestamps = new TimestampSet();
        for (Manifest manifest : manifests) {
            TimestampSet timestamps = manifest.timestamps();
            allTimestamps = allTimestamps.union(timestamps);
        }
        return allTimestamps;
    }

    private List<Manifest> manifests() throws IOException
    {
        List<Manifest> manifests = new ArrayList<>();
        DBStructure dbStructure = new DBStructure(DB_DIRECTORY);
        for (File manifestFile : dbStructure.forestDirectory().listFiles()) {
            manifests.add(Manifest.read(manifestFile));
        }
        return manifests;
    }

    private static final TestFactory FACTORY = new TestFactory();
    private static final String DB_NAME = "erdo";
    private static File DB_DIRECTORY;
    private static final String MAP_NAME = "map";
}
