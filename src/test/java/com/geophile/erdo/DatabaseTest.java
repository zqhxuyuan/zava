/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo;

import com.geophile.erdo.map.diskmap.DBStructure;
import com.geophile.erdo.util.FileUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class DatabaseTest
{
    @BeforeClass
    public static void beforeClass() throws IOException
    {
        DB_DIRECTORY = new File(FileUtil.tempDirectory(), DB_NAME);
        DB_STRUCTURE = new DBStructure(DB_DIRECTORY);
        FACTORY = new TestFactory();
    }

    @Before
    public void before()
    {
        FileUtil.deleteDirectory(DB_STRUCTURE.dbDirectory());
    }

    @After
    public void after()
    {
        FACTORY.reset();
    }

    @Test
    public void testDatabaseCreateTwice() throws IOException, InterruptedException
    {
        db = Database.createDatabase(DB_DIRECTORY);
        try {
            Database.createDatabase(DB_DIRECTORY);
            assertTrue(false);
        } catch (UsageError e) {
            // Expected
        } finally {
            db.close();
        }
    }

    @Test
    public void testDatabaseOpenNonExistent() throws IOException, InterruptedException
    {
        db = null;
        try {
            db = Database.useDatabase(DB_DIRECTORY);
            assertTrue(false);
        } catch (UsageError e) {
            // Expected
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    @Test
    public void testDatabaseCreateAndOpen() throws IOException, InterruptedException
    {
        Configuration configuration = Configuration.defaultConfiguration();
        configuration.diskCacheSizeBytes(123L);
        db = Database.createDatabase(DB_DIRECTORY, configuration);
        FileUtil.checkDirectoryExists(DB_DIRECTORY);
        FileUtil.checkFileExists(new File(DB_DIRECTORY, "database.properties"));
        db.close();
        FACTORY.reset();
        db = Database.useDatabase(DB_DIRECTORY);
        assertEquals(123L, db.configuration().diskCacheSizeBytes());
        db.close();
    }

    @Test
    public void testMapCreateTwice() throws IOException, InterruptedException
    {
        db = Database.createDatabase(DB_DIRECTORY);
        db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        try {
            db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
            assertTrue(false);
        } catch (UsageError e) {
            // Expected
        } finally {
            db.close();
        }
    }

    @Test
    public void testMapOpenNonExistent() throws IOException, InterruptedException
    {
        db = Database.createDatabase(DB_DIRECTORY);
        try {
            db.useMap(MAP_NAME);
            assertTrue(false);
        } catch (UsageError e) {
            // Expected
        } finally {
            db.close();
        }
    }

    @Test
    public void testEmptyMapCreateAndOpen() throws IOException, InterruptedException
    {
        db = Database.createDatabase(DB_DIRECTORY);
        db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        db.close();
        FACTORY.reset();
        db = Database.useDatabase(DB_DIRECTORY);
        db.useMap(MAP_NAME);
        db.close();
    }

    @Test
    public void testNonEmptyMapCreateAndOpen()
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        Configuration configuration = Configuration.defaultConfiguration();
        configuration.consolidationMinSizeBytes(0);
        db = Database.createDatabase(DB_DIRECTORY, configuration);
        OrderedMap map = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        final int N = 1000;
        for (int i = 0; i < N; i++) {
            TestRecord record = TestRecord.createRecord(i, value(i));
            map.put(record);
        }
        db.commitTransaction();
        db.close();
        FACTORY.reset();
        db = Database.useDatabase(DB_DIRECTORY);
        map = db.useMap(MAP_NAME);
        LOG.log(Level.SEVERE, "About to start cursor of reopened map");
        Cursor cursor = map.first();
        TestRecord record;
        int expectedKey = 0;
        while ((record = (TestRecord) cursor.next()) != null) {
            assertEquals(expectedKey, record.key().key());
            assertEquals(value(expectedKey), record.stringValue());
            expectedKey++;
        }
        assertEquals(N, expectedKey);
        db.close();
    }

    private static String value(int key)
    {
        return Integer.toString(key) +
               "xxxxxxxxxxxxxxxxxxxx" +
               "xxxxxxxxxxxxxxxxxxxx" +
               "xxxxxxxxxxxxxxxxxxxx" +
               "xxxxxxxxxxxxxxxxxxxx" +
               "xxxxxxxxxxxxxxxxxxxx";
    }

    private static DBStructure DB_STRUCTURE;
    private static File DB_DIRECTORY;
    private static TestFactory FACTORY;
    private static final Logger LOG = Logger.getLogger(DatabaseTest.class.getName());

    private Database db;
    private static final String DB_NAME = "erdo";
    private static final String MAP_NAME = "test";
}
