/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.mergescan;

import com.geophile.erdo.TestRecord;
import com.geophile.erdo.map.MapBehaviorTestBase;
import com.geophile.erdo.map.testarraymap.TestArrayMap;
import com.geophile.erdo.transaction.TimestampSet;
import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;

public class VersionedRecordMergeScanTest extends MapBehaviorTestBase
{
    @Test
    public void testVersionMerge() throws IOException, InterruptedException
    {
        // First map has 0..N-1
        // Second map has 0..N-2
        // ...
        // Nth map has 0
        // Value is key.version
        final int N = 10;
        // Turn off consolidation, so that we end up with N maps that need to be merged.
        MergeCursor mergeScan = new MergeCursor(null, true);
        for (int version = 0; version < N; version++) {
            TestArrayMap map = new TestArrayMap(FACTORY, new TimestampSet(version));
            for (int k = 0; k < N - version; k++) {
                TestRecord record = newRecord(k, String.format("%s.%s", k, version));
                record.key().transactionTimestamp(version);
                map.put(record, false);
            }
            mergeScan.addInput(map.cursor(null, false));
        }
        mergeScan.start();
        TestRecord record;
        int expectedKey;
        while ((record = (TestRecord) mergeScan.next()) != null) {
            expectedKey = record.key().key();
            String expectedValue = String.format("%s.%s", expectedKey, N - 1 - expectedKey);
            String actualValue = record.stringValue();
            Assert.assertEquals(expectedValue, actualValue);
        }
    }
}
