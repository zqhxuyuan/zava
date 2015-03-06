/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map;

import com.geophile.erdo.TestKey;
import com.geophile.erdo.TestRecord;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

public class SealedMapTest extends MapBehaviorTestBase
{
    @Test
    public void testCursor() throws IOException, InterruptedException
    {
        // LOG.log(Level.SEVERE, "Re-enable all of SealedMapTest");
        for (int n = 0; n <= N_MAX; n++) {
            testCursor(arrayMap(testRecords(n)), n);
            testCursor(privateMap(testRecords(n)), n);
            testCursor(diskMap(testRecords(n)), n);
/* Operating on a ForestMap outside the context of a TransactionalMap doesn't
   work so well due to new work on transactions & consolidation.
            testCursor(forest(testRecords(n)), n);
*/
        }
    }

    private void testCursor(SealedMap map, int n) throws IOException, InterruptedException
    {
        try {
            FACTORY.reset();
            MapCursor cursor;
            int expectedKey;
            int expectedLastKey;
            boolean expectedEmpty;
            LazyRecord record;
            // Full scan
            {
                cursor = map.cursor(null, false);
                expectedKey = 0;
                // debug("n: %s", n);
                while ((record = cursor.next()) != null) {
                    // debug("    %s", record.materializeRecord());
                    assertEquals(expectedKey, key(record));
                    expectedKey += GAP;
                }
                assertEquals(n * GAP, expectedKey);
            }
            // Try scans starting at, before, and after each key and ending at, before and after each key.
            {
                for (int i = 0; i < n; i++) {
                    int startBase = GAP * i;
                    int endBase = GAP * (n - 1 - i);
                    for (int start = startBase - 1; start <= startBase + 1; start++) {
                        for (int end = endBase - 1; end <= endBase + 1; end++) {
                            // debug("n: %s, start: %s, end: %s", n, start, end);
                            if (start <= end) {
                                cursor = map.cursor(key(start), false);
                                TestKey endKey = key(end);
                                expectedKey = start <= startBase ? startBase : startBase + GAP;
                                expectedLastKey = end >= endBase ? endBase : endBase - GAP;
                                expectedEmpty = start > end || start <= end && (end >= startBase || start <= endBase);
                                boolean empty = true;
                                while ((record = cursor.next()) != null &&
                                       record.key() .compareTo(endKey) <= 0) {
                                    // debug("    %s", record.materializeRecord());
                                    assertEquals(expectedKey, key(record));
                                    expectedKey += GAP;
                                    empty = false;
                                }
                                if (empty) {
                                    assertTrue(expectedEmpty);
                                } else {
                                    assertEquals(expectedLastKey + GAP, expectedKey);
                                }
                            }
                        }
                    }
                }
            }
            // Alternating next and previous
            {
                // System.out.println(n);
                cursor = map.cursor(null, false);
                expectedKey = 0;
                record = cursor.next();
                if (record != null) {
                    // debug("expected: %s, start: %s", expectedKey, record);
                    expectedKey += GAP;
                }
                while ((record = cursor.next()) != null) {
                    // debug("expected: %s, next: %s", expectedKey, record);
                    assertEquals(expectedKey, key(record));
                    expectedKey += GAP;
                    if (expectedKey != n * GAP) {
                        record = cursor.next();
                        // debug("expected: %s, next: %s", expectedKey, record);
                        assertNotNull(record);
                        assertEquals(expectedKey, key(record));
                        expectedKey -= GAP;
                        record = cursor.previous();
                        // debug("expected: %s, previous: %s", expectedKey, record);
                        assertEquals(expectedKey, key(record));
                        expectedKey += GAP; // About to go to next
                    }
                }
                assertEquals(n * GAP, expectedKey);
            }
            // Alternating previous and next
            {
                // debug("n: %s", n);
                cursor = map.cursor(null, false);
                expectedKey = (n - 1) * GAP;
                record = cursor.previous();
                if (record != null) {
                    // debug("expected: %s, start: %s", expectedKey, record.key());
                    expectedKey -= GAP;
                }
                while ((record = cursor.previous()) != null) {
                    // debug("expected: %s, previous: %s", expectedKey, record.key());
                    assertEquals(expectedKey, key(record));
                    expectedKey -= GAP;
                    if (expectedKey >= 0) {
                        record = cursor.previous();
                        // debug("expected: %s, previous: %s", expectedKey, record.key());
                        assertNotNull(record);
                        assertEquals(expectedKey, key(record));
                        expectedKey += GAP;
                        record = cursor.next();
                        // debug("expected: %s, next: %s", expectedKey, record.key());
                        assertEquals(expectedKey, key(record));
                        expectedKey -= GAP; // About to go to next
                    }
                }
                assertEquals(-GAP, expectedKey);
            }
            // goTo
            if (n > 0) {
                cursor = map.cursor(null, false);
                int match;
                int before;
                for (int i = 0; i <= n; i++) {
                    // debug("n: %s, i: %s", n, i);
                    match = i * GAP;
                    if (i < n) {
                        // Match, next
                        cursor.goTo(key(match));
                        assertEquals(key(match), cursor.next().key());
                        // Match, previous
                        cursor.goTo(key(match));
                        assertEquals(key(match), cursor.previous().key());
                    }
                    // Before, next
                    before = match - GAP / 2;
                    cursor.goTo(key(before));
                    if (i == n) {
                        assertNull(cursor.next());
                    } else {
                        assertEquals(key(match), cursor.next().key());
                    }
                    // Before, previous
                    cursor.goTo(key(before));
                    if (i == 0) {
                        assertNull(cursor.previous());
                    } else {
                        assertEquals(key(match - GAP), cursor.previous().key());
                    }
                }
            }
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    private List<TestRecord> testRecords(int n) throws IOException
    {
        List<TestRecord> testRecords = new ArrayList<>();
        assertTrue(GAP > 1);
        // Populate map with keys 0, GAP, ..., GAP * (n - 1)
        // System.out.println("Records:");
        for (int i = 0; i < n; i++) {
            int key = GAP * i;
            TestRecord record = newRecord(key, value(key));
            // debug("    %s", record);
            testRecords.add(record);
        }
        return testRecords;
    }

    private String value(int key)
    {
        return Integer.toString(key) + FILLER;
    }

    private void debug(String template, Object ... args)
    {
        System.out.println(String.format(template, args));
    }

    private static final String FILLER = "xxxxxxxxxxxxxxxxxxxx" +
                                         "xxxxxxxxxxxxxxxxxxxx" +
                                         "xxxxxxxxxxxxxxxxxxxx" +
                                         "xxxxxxxxxxxxxxxxxxxx" +
                                         "xxxxxxxxxxxxxxxxxxxx" +
                                         "xxxxxxxxxxxxxxxxxxxx" +
                                         "xxxxxxxxxxxxxxxxxxxx" +
                                         "xxxxxxxxxxxxxxxxxxxx" +
                                         "xxxxxxxxxxxxxxxxxxxx" +
                                         "xxxxxxxxxxxxxxxxxxxx";
    private static final int N_MAX = 100;
    private static final int GAP = 10;
}
