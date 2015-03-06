/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.transaction;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TimestampSetTest extends TestCase
{
    public void testEmpty()
    {
        TimestampSet timestampSet = TimestampSet.consolidate(Collections.<TimestampSet>emptyList());
        assertTrue(timestampSet.empty());
    }

    public void testDisjoint()
    {
        for (int n = 1; n <= 10; n++) {
            StringBuilder expected = new StringBuilder();
            List<TimestampSet> timestampSets = new ArrayList<>();
            int t = 0;
            for (int intervalSize = 1; intervalSize <= 3; intervalSize++) {
                for (int i = 0; i < n; i++) {
                    long start = t;
                    long end = start + intervalSize - 1;
                    TimestampSet timestampSet = new TimestampSet();
                    timestampSet.append(start, end);
                    timestampSets.add(timestampSet);
                    if (expected.length() > 0) {
                        expected.append(',');
                    }
                    expected.append(start);
                    if (start < end) {
                        expected.append('-');
                        expected.append(end);
                    }
                    t += intervalSize + 1;
                }
                TimestampSet timestampSet = TimestampSet.consolidate(timestampSets);
                assertEquals(expected.toString(), timestampSet.toString());
            }
        }
    }

    public void testAdjacent()
    {
        for (int n = 1; n <= 10; n++) {
            List<TimestampSet> timestampSets = new ArrayList<>();
            int t = 0;
            for (int intervalSize = 1; intervalSize <= 3; intervalSize++) {
                for (int i = 0; i < n; i++) {
                    long start = t;
                    long end = start + intervalSize - 1;
                    TimestampSet timestampSet = new TimestampSet();
                    timestampSet.append(start, end);
                    timestampSets.add(timestampSet);
                    t += intervalSize;
                }
                String expected = t == 1 ? "0" : String.format("0-%s", t - 1);
                TimestampSet timestampSet = TimestampSet.consolidate(timestampSets);
                assertEquals(expected, timestampSet.toString());
            }
        }
    }

    public void testEmptyMinusEmpty()
    {
        TimestampSet x = new TimestampSet();
        TimestampSet y = new TimestampSet();
        TimestampSet m = x.minus(y);
        assertTrue(m.empty());
    }

    public void testEmptyMinusSomething()
    {
        TimestampSet x = new TimestampSet();
        TimestampSet y = transactionTimestamps(5);
        TimestampSet m = x.minus(y);
        assertTrue(m.empty());
    }

    public void testSomethingMinusEmpty()
    {
        TimestampSet x = transactionTimestamps(5);
        TimestampSet y = new TimestampSet();
        TimestampSet m = x.minus(y);
        assertEquals("5", m.toString());
    }

    public void testMinusSame()
    {
        TimestampSet x = transactionTimestamps(5);
        TimestampSet y = transactionTimestamps(5);
        TimestampSet m = x.minus(y);
        assertTrue(m.empty());
    }

    public void testFixedKeepMinusVariousRemove()
    {
        TimestampSet keep = transactionTimestamps(1, 2);
        assertEquals("1-2", keep.minus(transactionTimestamps(0, 0)).toString());
        assertEquals("2", keep.minus(transactionTimestamps(0, 1)).toString());
        assertEquals("-", keep.minus(transactionTimestamps(0, 2)).toString());
        assertEquals("-", keep.minus(transactionTimestamps(0, 3)).toString());
        assertEquals("2", keep.minus(transactionTimestamps(1, 1)).toString());
        assertEquals("-", keep.minus(transactionTimestamps(1, 2)).toString());
        assertEquals("-", keep.minus(transactionTimestamps(1, 3)).toString());
        assertEquals("1", keep.minus(transactionTimestamps(2, 2)).toString());
        assertEquals("1", keep.minus(transactionTimestamps(2, 3)).toString());
        assertEquals("1-2", keep.minus(transactionTimestamps(3, 3)).toString());
    }

    public void testVariousKeepMinusFixedRemove()
    {
        TimestampSet remove = transactionTimestamps(1, 2);
        assertEquals("0", transactionTimestamps(0, 0).minus(remove).toString());
        assertEquals("0", transactionTimestamps(0, 1).minus(remove).toString());
        assertEquals("0", transactionTimestamps(0, 2).minus(remove).toString());
        assertEquals("0,3", transactionTimestamps(0, 3).minus(remove).toString());
        assertEquals("-", transactionTimestamps(1, 1).minus(remove).toString());
        assertEquals("-", transactionTimestamps(1, 2).minus(remove).toString());
        assertEquals("3", transactionTimestamps(1, 3).minus(remove).toString());
        assertEquals("-", transactionTimestamps(2, 2).minus(remove).toString());
        assertEquals("3", transactionTimestamps(2, 3).minus(remove).toString());
        assertEquals("3", transactionTimestamps(3, 3).minus(remove).toString());
    }

    public void testRandomMinus()
    {
        final int TRIALS = 1000;
        final int TRANSACTIONS = 10000;
        Random random = new Random(419);
        for (int i = 0; i < TRIALS; i++) {
            TimestampSet keepTxns = new TimestampSet();
            TimestampSet removeTxns = new TimestampSet();
            StringBuilder expected = new StringBuilder();
            double pKeep = random.nextDouble();
            double pRemove = random.nextDouble();
            boolean inKeep = false;
            boolean inRemove = false;
            long intervalStart = -1L;
            for (long t = 0; t < TRANSACTIONS; t++) {
                boolean keep = random.nextDouble() < pKeep;
                boolean remove = random.nextDouble() < pRemove;
                if (t < TRANSACTIONS) {
                    if (keep) {
                        keepTxns.append(t);
                    }
                    if (remove) {
                        removeTxns.append(t);
                    }
                }
                boolean wasInInterval = inKeep && !inRemove;
                boolean nowInInterval = keep && !remove;
                if (!wasInInterval && nowInInterval) {
                    // interval starting
                    intervalStart = t;
                } else if (wasInInterval && !nowInInterval) {
                    // interval ended
                    if (expected.length() > 0) {
                        expected.append(',');
                    }
                    assert intervalStart >= 0;
                    expected.append(intervalStart);
                    if (t - 1 > intervalStart) {
                        expected.append('-');
                        expected.append(t - 1);
                    }
                    intervalStart = -1L;
                }
                inKeep = keep;
                inRemove = remove;
            }
            if (intervalStart >= 0) {
                if (expected.length() > 0) {
                    expected.append(',');
                }
                expected.append(intervalStart);
                if (TRANSACTIONS - 1 > intervalStart) {
                    expected.append('-');
                    expected.append(TRANSACTIONS - 1);
                }
            }
            if (expected.length() == 0) {
                expected.append('-');
            }
            assertEquals(expected.toString(), keepTxns.minus(removeTxns).toString());
        }
    }

    private TimestampSet transactionTimestamps(long t)
    {
        return transactionTimestamps(t, t);
    }

    private TimestampSet transactionTimestamps(long min, long max)
    {
        TimestampSet transactionTimestamps = new TimestampSet();
        transactionTimestamps.append(min, max);
        return transactionTimestamps;
    }
}
