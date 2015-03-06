/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.mergescan;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.MapBehaviorTestBase;
import com.geophile.erdo.map.MapCursor;
import com.geophile.erdo.map.SealedMap;
import com.geophile.erdo.map.privatemap.PrivateMap;
import com.geophile.erdo.transaction.DeadlockException;
import com.geophile.erdo.transaction.TransactionRolledBackException;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MergeCursorTest extends MapBehaviorTestBase
{
    @Test
    public void testNoInputs() throws IOException, InterruptedException
    {
        MergeCursor cursor = forwardMergeCursor();
        assertNull(cursor.next());
    }

    @Test
    public void testOneInput()
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        for (int n = 0; n <= MAX_N; n++) {
            PrivateMap map = new PrivateMap(FACTORY);
            // load
            {
                for (int i = 0; i < n; i++) {
                    map.put(newRecord(i, null), false);
                }
            }
            // forward
            {
                MergeCursor cursor = forwardMergeCursor(map);
                int expected = 0;
                LazyRecord record;
                while ((record = cursor.next()) != null) {
                    assertEquals(expected++, key(record));
                }
                assertEquals(n, expected);
            }
            // backward
            {
                MergeCursor cursor = backwardMergeCursor(map);
                int expected = n;
                LazyRecord record;
                while ((record = cursor.previous()) != null) {
                    assertEquals(--expected, key(record));
                }
                assertEquals(0, expected);
            }
        }
    }

    @Test
    public void testTwoInputs()
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        PrivateMap openMap;
        for (int nEven = 0; nEven <= MAX_N; nEven++) {
            openMap = new PrivateMap(FACTORY);
            List<Integer> expectedEven = new ArrayList<>();
            for (int i = 0; i < nEven; i++) {
                int even = 2 * i;
                openMap.put(newRecord(even, null), false);
                expectedEven.add(even);
            }
            SealedMap evenMap = openMap;
            for (int nOdd = 0; nOdd <= MAX_N; nOdd++) {
                openMap = new PrivateMap(FACTORY);
                List<Integer> expected = new ArrayList<>(expectedEven);
                // Odd numbers
                for (int i = 0; i < nEven; i++) {
                    int odd = 2 * i + 1;
                    openMap.put(newRecord(odd, null), false);
                    expected.add(odd);
                }
                SealedMap oddMap = openMap;
                // forward
                {
                    Collections.sort(expected);
                    Iterator<Integer> expectedIterator = expected.iterator();
                    MergeCursor cursor = forwardMergeCursor(evenMap, oddMap);
                    LazyRecord lazyRecord;
                    while ((lazyRecord = cursor.next()) != null) {
                        assertEquals(expectedIterator.next().intValue(), key(lazyRecord));
                    }
                    assertTrue(!expectedIterator.hasNext());
                }
                // backward
                {
                    Collections.reverse(expected);
                    Iterator<Integer> expectedIterator = expected.iterator();
                    MergeCursor cursor = backwardMergeCursor(evenMap, oddMap);
                    LazyRecord lazyRecord;
                    while ((lazyRecord = cursor.previous()) != null) {
                        assertEquals(expectedIterator.next().intValue(), key(lazyRecord));
                    }
                    assertTrue(!expectedIterator.hasNext());
                }
            }
        }
    }

    @Test
    public void testManyInputs() throws IOException, InterruptedException
    {
        final int TRIALS = 1000;
        final int MAX_INPUTS = 20;
        final int AVE_RECORDS_PER_INPUT = 2;
        Random random = new Random(123456789);
        for (int t = 0; t < TRIALS; t++) {
            // load
            int nInputs = 1 + random.nextInt(MAX_INPUTS);
            PrivateMap[] maps = new PrivateMap[nInputs];
            for (int i = 0; i < nInputs; i++) {
                maps[i] = new PrivateMap(FACTORY);
            }
            int nRecords = nInputs * AVE_RECORDS_PER_INPUT;
            for (int k = 0; k < nRecords; k++) {
                int m = random.nextInt(nInputs);
                maps[m].put(newRecord(k, null), false);
            }
            for (int i = 0; i < nInputs; i++) {
                MapCursor cursor = maps[i].cursor(null, false);
                LazyRecord lazyRecord;
                StringBuffer buffer = new StringBuffer();
                while ((lazyRecord = cursor.next()) != null) {
                    buffer.append("  ");
                    buffer.append(lazyRecord.materializeRecord().key());
                }
            }
            // forward
            {
                MergeCursor cursor = new MergeCursor(null, true);
                for (PrivateMap map : maps) {
                    cursor.addInput(map.cursor(null, false));
                }
                cursor.start();
                LazyRecord lazyRecord;
                int expected = 0;
                while ((lazyRecord = cursor.next()) != null) {
                    assertEquals(expected++, key(lazyRecord));
                }
                assertEquals(nRecords, expected);
            }
            // backward
            {
                MergeCursor cursor = new MergeCursor(null, false);
                for (PrivateMap map : maps) {
                    cursor.addInput(map.cursor(null, false));
                }
                cursor.start();
                LazyRecord lazyRecord;
                int expected = nRecords;
                while ((lazyRecord = cursor.previous()) != null) {
                    assertEquals(--expected, key(lazyRecord));
                }
                assertEquals(0, expected);
            }
            // changing direction (two steps forward, one step back)
            {
                MergeCursor cursor = new MergeCursor(null, true);
                for (PrivateMap map : maps) {
                    cursor.addInput(map.cursor(null, false));
                }
                cursor.start();
                LazyRecord record;
                int expected = -1;
                for (int i = 0; i < nRecords; i++) {
                    // debug("trial: %s, nRecords: %s, i: %s", t, nRecords, i);
                    // next
                    record = cursor.next();
                    // debug("    next: %s", key(record));
                    assertEquals(++expected, key(record));
                    if (expected < nRecords - 1) {
                        // next
                        record = cursor.next();
                        // debug("    next: %s", key(record));
                        assertEquals(++expected, key(record));
                        // previous
                        record = cursor.previous();
                        // debug("    previous: %s", key(record));
                        assertEquals(--expected, key(record));
                    } else {
                        assertNull(cursor.next());
                    }
                }
            }
            // changing direction the other way (two steps backward, one step forward)
            {
                MergeCursor cursor = new MergeCursor(null, false);
                for (PrivateMap map : maps) {
                    cursor.addInput(map.cursor(null, false));
                }
                cursor.start();
                LazyRecord record;
                int expected = nRecords;
                for (int i = 0; i < nRecords; i++) {
                    // next
                    record = cursor.previous();
                    assertEquals(--expected, key(record));
                    if (expected > 0) {
                        // next
                        record = cursor.previous();
                        assertEquals(--expected, key(record));
                        // previous
                        record = cursor.next();
                        assertEquals(++expected, key(record));
                    } else {
                        assertNull(cursor.previous());
                    }
                }
            }
        }
    }

    private MergeCursor forwardMergeCursor(SealedMap... inputs) throws IOException, InterruptedException
    {
        MergeCursor mergeScan = new MergeCursor(null, true);
        for (SealedMap input : inputs) {
            mergeScan.addInput(input.cursor(null, false));
        }
        mergeScan.start();
        return mergeScan;
    }

    private MergeCursor backwardMergeCursor(SealedMap... inputs) throws IOException, InterruptedException
    {
        MergeCursor mergeCursor = new MergeCursor(null, false);
        for (SealedMap input : inputs) {
            mergeCursor.addInput(input.cursor(null, false));
        }
        mergeCursor.start();
        return mergeCursor;
    }

    private AbstractKey keyOrNull(LazyRecord record) throws IOException, InterruptedException
    {
        return record == null ? null : record.materializeRecord().key();
    }

    private void debug(String template, Object ... args)
    {
        System.out.println(String.format(template, args));
    }

    private static int MAX_N = 10;
}
