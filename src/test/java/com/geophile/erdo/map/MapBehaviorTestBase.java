/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map;

import com.geophile.erdo.*;
import com.geophile.erdo.apiimpl.DatabaseOnDisk;
import com.geophile.erdo.apiimpl.DeletedRecord;
import com.geophile.erdo.map.arraymap.ArrayMap;
import com.geophile.erdo.map.diskmap.DiskMap;
import com.geophile.erdo.map.privatemap.PrivateMap;
import com.geophile.erdo.transaction.TimestampSet;
import com.geophile.erdo.transaction.Transaction;
import com.geophile.erdo.util.FileUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MapBehaviorTestBase
{
    @BeforeClass
    public static void beforeClass() throws IOException, InterruptedException
    {
        TestKey.testErdoId(ERDO_ID);
        Configuration configuration = Configuration.defaultConfiguration();
        configuration.diskPageSizeBytes(4096);
        configuration.diskSegmentSizeBytes(8192);
        FACTORY = new TestFactory(configuration);
    }

    @Before
    public void before() throws IOException, InterruptedException
    {
        final File DB_DIRECTORY = new File(FileUtil.tempDirectory(), DB_NAME);
        FileUtil.deleteDirectory(DB_DIRECTORY);
        db = DatabaseOnDisk.createDatabase(DB_DIRECTORY, FACTORY);
        Transaction.initialize(FACTORY);
    }

    @After
    public void after() throws IOException, InterruptedException
    {
        db.close();
        FACTORY.reset();
    }

    protected SealedMap arrayMap(List<TestRecord> testRecords) throws IOException, InterruptedException
    {
        SealedMap privateMap = privateMap(testRecords);
        ArrayMap arrayMap = new ArrayMap(FACTORY, new TimestampSet(1L));
        arrayMap.loadForConsolidation(privateMap.cursor(null, false), null);
        return arrayMap;
    }

    protected SealedMap privateMap(List<TestRecord> testRecords)
    {
        PrivateMap map = new PrivateMap(FACTORY);
        for (TestRecord record : testRecords) {
            map.put(record, false);
        }
        return map;
    }

    protected SealedMap diskMap(List<TestRecord> testRecords) throws IOException, InterruptedException
    {
        FACTORY.registerRecordFactory(ERDO_ID, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        // DiskMap requires records with timestamps, so assign them.
        final long TIMESTAMP = 0L;
        for (TestRecord record : testRecords) {
            record.key().transactionTimestamp(TIMESTAMP);
        }
        DiskMap diskMap = DiskMap.create(db, new TimestampSet(1L), null);
        SealedMap privateMap = privateMap(testRecords);
        diskMap.loadForConsolidation(privateMap.cursor(null, false),
                                     privateMap.keyScan(null, false));
        return diskMap;
    }

    protected SealedMap forestMap(List<TestRecord> testRecords) throws IOException, InterruptedException
    {
        // Create component maps in sizes according to fibonacci sequence
/*
        EnvironmentImpl env = new EnvironmentImpl(factory);
        DisklessTestDatabase db = new DisklessTestDatabase(env, Configuration.defaultConfiguration());
        this.db = db;
        Forest forest = Forest.create(db);
        ForestMapShared forest = ForestMapShared.create(forest);
        int n = 0;
        int nPrevious = 0;
        PrivateMap componentMap = new PrivateMap(factory);
        for (TestRecord record : testRecords) {
            if (componentMap.recordCount() == n) {
                componentMap.close();
                Long timestamp = Transaction.commit(factory.lockManager(), null, null);
                componentMap.transactionTimestamp(timestamp);
                forest.commit(componentMap);
                if (n == 0) {
                    n = 1;
                    nPrevious = 0;
                } else {
                    int nNext = n + nPrevious;
                    nPrevious = n;
                    n = nNext;
                }
                componentMap = new PrivateMap(factory);
            }
            record.key().transaction(Transaction.current());
            componentMap.put(record, false);
        }
        if (componentMap.recordCount() > 0) {
            componentMap.close();
            Long timestamp = Transaction.commit(factory.lockManager(), null, null);
            componentMap.transactionTimestamp(timestamp);
            forest.commit(componentMap);
        }
        return forest.snapshot();
*/
        return null;
    }

    protected TestRecord newRecord(int key, String value) throws IOException
    {
        return TestRecord.createRecord(key, value);
    }

    protected DeletedRecord newDeletedRecord(int key) throws IOException
    {
        return new DeletedRecord(new TestKey(key));
    }

    protected TestKey key(int key)
    {
        return new TestKey(key);
    }

    protected int key(LazyRecord lazyRecord) throws IOException, InterruptedException
    {
        return ((TestKey) lazyRecord.materializeRecord().key()).key();
    }

    protected void print(String template, Object... args)
    {
        System.out.println(String.format(template, args));
    }

    private static final int ERDO_ID = 1;
    private static final String DB_NAME = "erdo";
    protected static TestFactory FACTORY;

    protected DatabaseOnDisk db;
}
