/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap;

import com.geophile.erdo.*;
import com.geophile.erdo.apiimpl.TreePositionTracker;
import com.geophile.erdo.apiimpl.DatabaseOnDisk;
import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.MapCursor;
import com.geophile.erdo.map.diskmap.tree.LevelOneMultiRecord;
import com.geophile.erdo.map.mergescan.FastMergeCursor;
import com.geophile.erdo.map.mergescan.MultiRecordKey;
import com.geophile.erdo.map.testarraymap.TestArrayMap;
import com.geophile.erdo.transaction.TimestampSet;
import com.geophile.erdo.util.FileUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.*;

public class DiskMapFastMergeTest
{
    @BeforeClass
    public static void beforeClass() throws IOException
    {
        TestKey.testErdoId(ERDO_ID);
        FACTORY.recordFactory(ERDO_ID, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        Configuration configuration = FACTORY.configuration();
        configuration.diskPageSizeBytes(4096);
        configuration.diskSegmentSizeBytes(8192);
        configuration.keysInMemoryMapLimit(0);
        DB_DIRECTORY = new File(FileUtil.tempDirectory(), DB_NAME);
        DB_STRUCTURE = new DBStructure(DB_DIRECTORY.getCanonicalFile());
    }

    @Before
    public void before() throws IOException, InterruptedException
    {
        FileUtil.deleteDirectory(DB_DIRECTORY);
        FileUtil.deleteDirectory(DB_STRUCTURE.dbDirectory());
        db = DatabaseOnDisk.createDatabase(DB_DIRECTORY.getAbsoluteFile(), FACTORY);
    }

    @After
    public void after() throws IOException, InterruptedException
    {
        db.close();
        FACTORY.reset();
    }

    @Test
    public void testSingleLevel() throws IOException, InterruptedException
    {
        final int N = 10;
        TestArrayMap a = new TestArrayMap(FACTORY, new TimestampSet(111));
        TestArrayMap b = new TestArrayMap(FACTORY, new TimestampSet(222));
        int id = 0;
        // 0-9 A
        for (int i = 0; i < N; i++) {
            addRecord(a, id++, A_SMALL_FILLER);
        }
        // 10-19 B
        for (int i = 0; i < N; i++) {
            addRecord(b, id++, B_SMALL_FILLER);
        }
        // Load DiskMaps
        DiskMap diskMapA = newDiskMap(111);
        DiskMap diskMapB = newDiskMap(222);
        diskMapA.loadForConsolidation(a.cursor(null, false),
                                      a.keyScan(null, false));
        diskMapB.loadForConsolidation(b.cursor(null, false),
                                      b.keyScan(null, false));
        // Merge
        FastMergeCursor merge = new FastMergeCursor();
        merge.addInput(diskMapA.consolidationScan());
        merge.addInput(diskMapB.consolidationScan());
        merge.start();
        DiskMap mergedMap = newDiskMap(333);
        mergedMap.loadWithKeys(merge, 1000);
        // Check merged map
        MapCursor cursor = mergedMap.cursor(null, false);
        LazyRecord lazyRecord;
        int expected = 0;
        while ((lazyRecord = cursor.next()) != null) {
            TestRecord record = (TestRecord) lazyRecord.materializeRecord();
            int key = record.key().key();
            char v = record.stringValue().charAt(0);
            assertEquals(expected++, key);
            if (key < N) {
                assertEquals('a', v);
            } else if (key < 2 * N) {
                assertEquals('b', v);
            } else {
                fail();
            }
        }
        assertEquals(N * 2, expected);
    }

    @Test
    public void testTwoLevelDisjoint() throws IOException, InterruptedException
    {
        // A has 0..(N-1). B has N..(2N-1). This should be a fast merge. Requires correct handling of end-of-file
        // keys in level 1. Incorrect behavior sees ranges 0..null and N..null in the merge, resulting in a
        // slow merge. Correct behavior sees 0..(N-1), N..(2N-1). (This was the motivation for file-terminating keys
        // in summary files.)
        final int N = 64;
        TestArrayMap a = new TestArrayMap(FACTORY, new TimestampSet(111));
        TestArrayMap b = new TestArrayMap(FACTORY, new TimestampSet(222));
        int id = 0;
        // 0-(N-1) A
        for (int i = 0; i < N; i++) {
            addRecord(a, id++, A_BIG_FILLER);
        }
        // N-(2N-1) B
        for (int i = N; i < 2 * N; i++) {
            addRecord(b, id++, B_BIG_FILLER);
        }
        // Load DiskMaps
        DiskMap diskMapA = newDiskMap(111);
        DiskMap diskMapB = newDiskMap(222);
        diskMapA.loadForConsolidation(a.cursor(null, false),
                                      a.keyScan(null, false));
        diskMapB.loadForConsolidation(b.cursor(null, false),
                                      b.keyScan(null, false));
        // Merge
        FastMergeCursor merge = new FastMergeCursor();
        merge.addInput(diskMapA.consolidationScan());
        merge.addInput(diskMapB.consolidationScan());
        merge.start();
        // First record
        AbstractRecord record = merge.next().materializeRecord();
        assertTrue(record instanceof LevelOneMultiRecord);
        LevelOneMultiRecord multiRecord = (LevelOneMultiRecord) record;
        MultiRecordKey key = (MultiRecordKey) multiRecord.key();
        assertEquals(0, ((TestKey) key.lo()).key());
        assertEquals(N - 1, ((TestKey) key.hi()).key());
        // Second record
        record = merge.next().materializeRecord();
        assertTrue(record instanceof LevelOneMultiRecord);
        multiRecord = (LevelOneMultiRecord) record;
        key = (MultiRecordKey) multiRecord.key();
        assertEquals(N, ((TestKey) key.lo()).key());
        assertEquals(2 * N - 1, ((TestKey) key.hi()).key());
        // There should be no more records
        assertTrue(merge.next() == null);
        TreePositionTracker.destroyRemainingTreePositions(null);
    }

    @Test
    public void testTwoLevel() throws IOException, InterruptedException
    {
        final int N = 1000;
        TestArrayMap a = new TestArrayMap(FACTORY, new TimestampSet(111));
        TestArrayMap b = new TestArrayMap(FACTORY, new TimestampSet(222));
        int id = 0;
        // 0-1999 AB: Interleave records between two maps
        for (int i = 0; i < N; i++) {
            addRecord(a, id++, A_BIG_FILLER);
            addRecord(b, id++, B_BIG_FILLER);
        }
        // 2000-2999 A: Put a long run in each map
        for (int i = 0; i < N; i++) {
            addRecord(a, id++, A_BIG_FILLER);
        }
        // 3000-3999 B
        for (int i = 0; i < N; i++) {
            addRecord(b, id++, B_BIG_FILLER);
        }
        // 4000-5999 AB: More interleaved records
        for (int i = 0; i < N; i++) {
            addRecord(a, id++, A_BIG_FILLER);
            addRecord(b, id++, B_BIG_FILLER);
        }
        // Load DiskMaps
        DiskMap diskMapA = newDiskMap(111);
        DiskMap diskMapB = newDiskMap(222);
        diskMapA.loadForConsolidation(a.cursor(null, false),
                                      a.keyScan(null, false));
        diskMapB.loadForConsolidation(b.cursor(null, false),
                                      b.keyScan(null, false));
        // Merge
        FastMergeCursor merge = new FastMergeCursor();
        merge.addInput(diskMapA.consolidationScan());
        merge.addInput(diskMapB.consolidationScan());
        merge.start();
        DiskMap mergedMap = newDiskMap(333);
        mergedMap.loadWithoutKeys(merge);
        // Check merged map
        MapCursor scan = mergedMap.cursor(null, false);
        LazyRecord lazyRecord;
        int expected = 0;
        while ((lazyRecord = scan.next()) != null) {
            TestRecord record = (TestRecord) lazyRecord.materializeRecord();
            int key = record.key().key();
            char v = record.stringValue().charAt(0);
            assertEquals(expected++, key);
            if (key < 2 * N) {
                assertEquals(key % 2 == 0 ? 'a' : 'b', v);
            } else if (key < 3 * N) {
                assertEquals('a', v);
            } else if (key < 4 * N) {
                assertEquals('b', v);
            } else if (key < 6 * N) {
                assertEquals(key % 2 == 0 ? 'a' : 'b', v);
            } else {
                fail();
            }
        }
        assertEquals(N * 6, expected);
    }

    private DiskMap newDiskMap(long timestamp) throws IOException, InterruptedException
    {
        return DiskMap.create(db, new TimestampSet(timestamp), null);
    }

    private void addRecord(TestArrayMap map, int id, String value) throws IOException, InterruptedException
    {
        TestKey key = new TestKey(id);
        key.transactionTimestamp(map.timestamps().minTimestamp());
        TestRecord record = new TestRecord(key);
        record.value(value);
        map.put(record, false);
    }

    private void dump(String label, DiskMap map) throws IOException, InterruptedException
    {
        MapCursor cursor = map.cursor(null, false);
        LazyRecord lazyRecord;
        int count = 0;
        while ((lazyRecord = cursor.next()) != null) {
            TestRecord record = (TestRecord) lazyRecord.materializeRecord();
            System.out.println(String.format("%s %s: %s -> %s", label, count++, record.key(), record.stringValue().substring(0, 10)));
        }
    }

    private static final String A_BIG_FILLER =
        "aaaaaaaaaaaaaaaaaaaa" +
        "aaaaaaaaaaaaaaaaaaaa" +
        "aaaaaaaaaaaaaaaaaaaa" +
        "aaaaaaaaaaaaaaaaaaaa" +
        "aaaaaaaaaaaaaaaaaaaa";
    private static final String B_BIG_FILLER =
        "bbbbbbbbbbbbbbbbbbbb" +
        "bbbbbbbbbbbbbbbbbbbb" +
        "bbbbbbbbbbbbbbbbbbbb" +
        "bbbbbbbbbbbbbbbbbbbb" +
        "bbbbbbbbbbbbbbbbbbbb";
    private static final String A_SMALL_FILLER = "a";
    private static final String B_SMALL_FILLER = "b";
    private static final int ERDO_ID = 1;
    private static final String DB_NAME = "erdo";
    private static final TestFactory FACTORY = new TestFactory();
    private static File DB_DIRECTORY;
    private static DBStructure DB_STRUCTURE;
    private DatabaseOnDisk db;
}
