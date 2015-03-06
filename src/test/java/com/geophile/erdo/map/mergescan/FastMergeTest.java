/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.mergescan;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.AbstractRecord;
import com.geophile.erdo.TestFactory;
import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.MapCursor;
import com.geophile.erdo.map.testarraymap.TestArrayMap;
import com.geophile.erdo.transaction.TimestampSet;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;

import static junit.framework.Assert.*;

public class FastMergeTest
{
    @BeforeClass
    public static void beforeClass() throws IOException
    {
        TestKey.testErdoId(ERDO_ID);
    }

    @After
    public void after()
    {
        FACTORY.reset();
    }

    @Test
    public void testOverlapping() throws IOException, InterruptedException
    {
        final int TRIALS = 10;
        final int SEED = 123456789;
        final int KEYS = 100;
        final int MAX_MULTI_RECORD_SIZE = 4;
        Random random = new Random(SEED);
        for (int t = 0; t < TRIALS; t++) {
            TestArrayMap a = arrayMap(KEYS, MAX_MULTI_RECORD_SIZE, random, 2, 0);
            TestArrayMap b = arrayMap(KEYS, MAX_MULTI_RECORD_SIZE, random, 2, 1);
            FastMergeCursor merge = new FastMergeCursor(MERGER);
            merge.addInput(a.cursor(null, false));
            merge.addInput(b.cursor(null, false));
            merge.start();
            // Should see 0 .. 2 * KEYS - 1
            int expected = 0;
            LazyRecord lazyRecord;
            while ((lazyRecord = merge.next()) != null) {
                AbstractRecord record = lazyRecord.materializeRecord();
                if (record instanceof TestMultiRecord) {
                    TestMultiRecord multiRecord = (TestMultiRecord) record;
                    MapCursor cursor = multiRecord.cursor();
                    LazyRecord lazyScanRecord;
                    while ((lazyScanRecord = cursor.next()) != null) {
                        assertEquals(expected++, ((TestKey) lazyScanRecord.key()).key());
                    }
                } else if (record instanceof TestRecord) {
                    assertEquals(expected++, ((TestKey)record.key()).key());
                } else {
                    fail();
                }
            }
            assertEquals(2 * KEYS, expected);
        }
    }

    private void dump(String label, TestArrayMap map) throws IOException, InterruptedException
    {
        System.out.println(label);
        MapCursor cursor = map.cursor(null, false);
        LazyRecord lazyRecord;
        while ((lazyRecord = cursor.next()) != null) {
            System.out.println(String.format("    %s", lazyRecord.materializeRecord()));
        }
    }

    // TODO: This doesn't test overlapping MultiRecords

    @Test
    public void testRandom() throws IOException, InterruptedException
    {
        final int TRIALS = 1000;
        final int MAX_INPUTS = 20;
        final int AVERAGE_RECORDS_PER_INPUT = 500;
        final int MAX_MULTI_RECORD_SIZE = 5;
        final double SINGLETON_FREQUENCY = 0.25;
        final int SEED = 123456789;
        Random random = new Random(SEED);
        for (int t = 0; t < TRIALS; t++) {
            int inputs = 2 + random.nextInt(MAX_INPUTS - 1); // 2 - MAX_INPUTS
            int records = inputs * AVERAGE_RECORDS_PER_INPUT;
            TestArrayMap[] arrayMaps = new TestArrayMap[inputs];
            for (int i = 0; i < inputs; i++) {
                arrayMaps[i] = new TestArrayMap(FACTORY, new TimestampSet());
            }
            int count = 0;
            // Load records and multi-records into ArrayMaps
            int expectedSingletonRecordCount = 0;
            int expectedMultiRecordCount = 0;
            while (count < records) {
                AbstractRecord record;
                if (random.nextDouble() < SINGLETON_FREQUENCY) {
                    record = record(count++);
                    expectedSingletonRecordCount++;
                } else {
                    int runSize = Math.min(records - count, 1 + random.nextInt(MAX_MULTI_RECORD_SIZE));
                    assertTrue(runSize > 0);
                    assertTrue(runSize <= MAX_MULTI_RECORD_SIZE);
                    record = multiRecord(count, runSize);
                    count += runSize;
                    expectedMultiRecordCount += runSize;
                }
                int a = random.nextInt(inputs);
                arrayMaps[a].put(record, false);
            }
            // Start a merge of the ArrayMaps
            FastMergeCursor merge = new FastMergeCursor(MERGER);
            for (TestArrayMap arrayMap : arrayMaps) {
                merge.addInput(arrayMap.cursor(null, false));
            }
            merge.start();
            // Check contents
            LazyRecord lazyRecord;
            int expectedKey = 0;
            int actualSingletonRecordCount = 0;
            int actualMultiRecordCount = 0;
            while ((lazyRecord = merge.next()) != null) {
                if (lazyRecord instanceof AbstractMultiRecord) {
                    TestMultiRecord multiRecord = (TestMultiRecord) lazyRecord;
                    for (AbstractRecord memberRecord : multiRecord.records()) {
                        assertEquals(expectedKey, ((TestKey) memberRecord.key()).key());
                        expectedKey++;
                    }
                    actualMultiRecordCount += multiRecord.records().size();
                } else {
                    assertEquals(expectedKey, ((TestKey) lazyRecord.key()).key());
                    expectedKey++;
                    actualSingletonRecordCount++;
                }
            }
            assertEquals(expectedSingletonRecordCount, actualSingletonRecordCount);
            assertEquals(expectedMultiRecordCount, actualMultiRecordCount);
            assertEquals(records, actualMultiRecordCount + actualSingletonRecordCount);
        }
    }

    private TestArrayMap arrayMap(int keys, int maxMultiRecordSize, Random random, int gap, int offset)
    {
        TestArrayMap map = new TestArrayMap(FACTORY, new TimestampSet());
        int k = offset;
        int keyCount = 0;
        while (keyCount < keys) {
            int runSize = random.nextInt(maxMultiRecordSize) + 1;
            if (keyCount + runSize > keys) {
                runSize = keys - keyCount;
            }
            AbstractRecord record;
            if (runSize == 1) {
                record = record(k);
                k += gap;
            } else {
                TestMultiRecord multiRecord =
                    new TestMultiRecord(new MultiRecordKey(new TestKey(k),
                                                           new TestKey(k + gap * runSize)));
                for (int i = 0; i < runSize; i++) {
                    TestKey key = new TestKey(k);
                    multiRecord.append(new TestRecord(key, null));
                    k += gap;
                }
                record = multiRecord;
            }
            map.put(record, false);
            keyCount += runSize;
        }
        return map;
    }

    private TestRecord record(int key)
    {
        return new TestRecord(new TestKey(key), null);
    }

    private TestMultiRecord multiRecord(int start, int n)
    {
        assertTrue(n > 0);
        TestMultiRecord multiRecord = new TestMultiRecord(new MultiRecordKey(new TestKey(start),
                                                                             new TestKey(start + n)));
        for (int i = 0; i < n; i++) {
            TestKey key = new TestKey(start + i);
            TestRecord record = new TestRecord(key, null);
            multiRecord.append(record);
        }
        return multiRecord;
    }

    private static final TestFactory FACTORY = new TestFactory();

    private static final Merger MERGER =
        new Merger()
        {
            @Override
            public Side merge(AbstractKey left, AbstractKey right)
            {
                int leftId = ((TestKey) left).id();
                int rightId = ((TestKey) right).id();
                return leftId < rightId ? Side.LEFT : Side.RIGHT;
            }
        };

    private static final int ERDO_ID = 1;
}
