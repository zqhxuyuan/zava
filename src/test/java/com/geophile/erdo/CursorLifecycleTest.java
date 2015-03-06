/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo;

import com.geophile.erdo.apiimpl.DisklessTestDatabase;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class CursorLifecycleTest
{
    @BeforeClass
    public static void beforeClass()
    {
        FACTORY = new TestFactory();
    }

    @Before
    public void before() throws IOException, InterruptedException, DeadlockException, TransactionRolledBackException
    {
        loadDatabase();
        assertTrue(N > 0);
    }

    @After
    public void after()
    {
        FACTORY.reset();
    }

    @Test
    public void testNoOpenScansAfterCompletingScan() throws IOException, InterruptedException
    {
        Cursor cursor = map.first();
        TestRecord record;
        int expectedKey = 0;
        while ((record = (TestRecord) cursor.next()) != null) {
            Assert.assertEquals(expectedKey, (record.key()).key());
            Assert.assertEquals(value(expectedKey), record.stringValue());
            expectedKey++;
        }
        Assert.assertEquals(N, expectedKey);
        assertEquals(0, FACTORY.transactionManager().currentTransaction().openCursors().size());
        db.commitTransaction();
        db.close();
    }

    @Test
    public void testNoOpenScansAfterClosingScanEarly() throws IOException, InterruptedException
    {
        Cursor cursor = map.first();
        TestRecord record;
        record = (TestRecord) cursor.next();
        assertNotNull(record);
        assertEquals(1, FACTORY.transactionManager().currentTransaction().openCursors().size());
        cursor.close();
        assertEquals(0, FACTORY.transactionManager().currentTransaction().openCursors().size());
        db.commitTransaction();
        db.close();
    }

    @Test
    public void testCommitWithOpenScan() throws IOException, InterruptedException
    {
        Cursor cursor = map.first();
        TestRecord record;
        record = (TestRecord) cursor.next();
        assertNotNull(record);
        assertEquals(1, FACTORY.transactionManager().currentTransaction().openCursors().size());
        db.commitTransaction();
        assertEquals(0, FACTORY.transactionManager().currentTransaction().openCursors().size());
        db.close();
    }

    @Test
    public void testRollbackWithOpenScan() throws IOException, InterruptedException
    {
        Cursor cursor = map.first();
        TestRecord record;
        record = (TestRecord) cursor.next();
        assertNotNull(record);
        assertEquals(1, FACTORY.transactionManager().currentTransaction().openCursors().size());
        db.rollbackTransaction();
        assertEquals(0, FACTORY.transactionManager().currentTransaction().openCursors().size());
        db.close();
    }

    private void loadDatabase()
        throws IOException, InterruptedException, DeadlockException, TransactionRolledBackException
    {
        db = new DisklessTestDatabase(FACTORY);
        map = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        for (int key = 0; key < N; key++) {
            AbstractRecord replaced = map.put(TestRecord.createRecord(key, value(key)));
            Assert.assertNull(replaced);
        }
    }

    private String value(int i)
    {
        return String.format("r.%s", i);
    }

    private static TestFactory FACTORY;
    private static final String MAP_NAME = "map";
    private static final int N = 10;

    private Database db;
    private OrderedMap map;
}
