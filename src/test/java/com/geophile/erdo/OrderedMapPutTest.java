/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo;

import com.geophile.erdo.apiimpl.DatabaseOnDisk;
import com.geophile.erdo.util.FileUtil;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

// A more thorough test of put. OrderedMapTest. testPut only tests duplicate keys in a single tree.
// There are subtleties in handling them across trees, and especially DiskMaps. So this uses DatabaseOnDisk,
// not DisklessTestDatabase.

public class OrderedMapPutTest
{
    @BeforeClass
    public static void beforeClass() throws IOException
    {
        DB_DIRECTORY = new File(FileUtil.tempDirectory(), DB_NAME);
        FACTORY = new TestFactory();
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
    public void testPut()
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        Configuration configuration = Configuration.defaultConfiguration();
        configuration.consolidationMinSizeBytes(0);
/*
// For FractalConsolidationPlanner2
        configuration.consolidationMinMapsToConsolidate(1);
        configuration.consolidationAllOrNoneMaxLargestMapCoverage(1.0);
        // For AllOrNone2ConsolidationPlanner
        configuration.consolidationMinMapsToConsolidate(2);
*/
        // For FractalConsolidationPlanner
        configuration.consolidationMinSizeBytes(0);
        configuration.consolidationMinMapsToConsolidate(0);
        Database db = DatabaseOnDisk.createDatabase(DB_DIRECTORY, FACTORY);
        Assert.assertTrue(N >= 4);
        Assert.assertTrue(N % 2 == 0);
        OrderedMap map = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        // Load map with keys 0..N-1 in one transaction
        for (int id = 0; id < N; id++) {
            AbstractRecord replaced = map.put(TestRecord.createRecord(id, "first"));
            Assert.assertNull(replaced);
        }
        db.commitTransaction();
        // Load same map with keys N/2..2N-1 in a different transaction
        for (int id = N / 2; id < 2 * N; id++) {
            AbstractRecord replaced = map.put(TestRecord.createRecord(id, "second"));
            if (id < N) {
                Assert.assertEquals(id, ((TestKey) replaced.key()).key());
                Assert.assertEquals("first", ((TestRecord) replaced).stringValue());
            } else {
                Assert.assertNull(replaced);
            }
        }
        db.commitTransaction();
        // Wait for consolidation. DB_DIRECTORY/forest should have one tree
        File forestDirectory = new File(DB_DIRECTORY, "forest");
        int count;
        do {
            count = forestDirectory.listFiles(PROPERTIES_FILE_FILTER).length;
            if (count > 1) {
                Thread.sleep(500);
            }
        } while (count > 1);
        Cursor cursor = map.first();
        TestRecord record;
        int expected = 0;
        while ((record = (TestRecord) cursor.next()) != null) {
            Assert.assertEquals(expected, record.key().key());
            if (expected < N / 2) {
                Assert.assertEquals("first", record.stringValue());
            } else {
                Assert.assertEquals("second", record.stringValue());
            }
            expected++;
        }
        Assert.assertEquals(2 * N, expected);
        db.close();
    }

    // TODO: similar test for delete

    private static final FilenameFilter PROPERTIES_FILE_FILTER = new FilenameFilter()
    {
        public boolean accept(File dir, String name)
        {
            return name.endsWith(".properties");
        }
    };

    private static TestFactory FACTORY;
    private static final String MAP_NAME = "map";
    private static final int N = 10;
    private static final String DB_NAME = "erdo";
    private static File DB_DIRECTORY;
}
