/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap.tree;

import com.geophile.erdo.*;
import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.MapCursor;
import com.geophile.erdo.map.diskmap.DBStructure;
import com.geophile.erdo.transaction.Transaction;
import com.geophile.erdo.util.FileUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class TreeTest
{
    @BeforeClass
    public static void beforeClass() throws IOException
    {
        TestKey.testErdoId(ERDO_ID);
        DB_STRUCTURE = new DBStructure(new File(FileUtil.tempDirectory(), "erdo"));
        Transaction.initialize(FACTORY);
    }

    @Before
    public void before() throws IOException
    {
        FACTORY.recordFactory(ERDO_ID, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        FileUtil.deleteDirectory(DB_STRUCTURE.dbDirectory());
        FileUtil.ensureDirectoryExists(DB_STRUCTURE.dbDirectory());
        FileUtil.ensureDirectoryExists(DB_STRUCTURE.forestDirectory());
        FileUtil.ensureDirectoryExists(DB_STRUCTURE.segmentsDirectory());
        FileUtil.ensureDirectoryExists(DB_STRUCTURE.summariesDirectory());
    }
    
    @After
    public void after()
    {
        FACTORY.reset();
    }

    @Test
    public void testScanEmpty() throws Exception
    {
        WriteableTree writeableTree = Tree.create(FACTORY, DB_STRUCTURE, TREE_ID);
        Tree tree = writeableTree.close();
        startTransaction();
        MapCursor cursor = tree.cursor(null);
        assertNull(cursor.next());
    }

    // This test checks tree scanning. Tree random access is tested indirectly, in SealedMapTest,
    // operating on a DiskMap.

    @Test
    public void testScanRecords() throws Exception
    {
        final int N = 1000;
        // Load tree
        Tree tree;
        {
            WriteableTree writeableTree = Tree.create(FACTORY, DB_STRUCTURE, TREE_ID);
            for (int i = 0; i < N; i++) {
                startTransaction();
                TestRecord record = TestRecord.createRecord(i, VALUES[i % 10]);
                record.key().transaction(FACTORY.transactionManager().currentTransaction());
                commitTransaction();
                writeableTree.append(record);
            }
            tree = writeableTree.close();
        }
        // Scan forward
        {
            startTransaction();
            MapCursor cursor = tree.cursor(null);
            int expected = 0;
            LazyRecord lazyRecord;
            while ((lazyRecord = cursor.next()) != null) {
                TestRecord record = (TestRecord) lazyRecord.materializeRecord();
                int key = record.key().key();
                assertEquals(expected, key);
                assertEquals(VALUES[key % 10], record.stringValue());
                expected++;
            }
            commitTransaction();
            assertEquals(N, expected);
        }
        // Scan backward
        {
            startTransaction();
            MapCursor cursor = tree.cursor(null);
            int expected = N;
            LazyRecord lazyRecord;
            while ((lazyRecord = cursor.previous()) != null) {
                expected--;
                TestRecord record = (TestRecord) lazyRecord.materializeRecord();
                int key = record.key().key();
                assertEquals(expected, key);
                assertEquals(VALUES[key % 10], record.stringValue());
            }
            commitTransaction();
            assertEquals(0, expected);
        }
    }
    
    private void startTransaction()
    {
        FACTORY.transactionManager().currentTransaction();
    }
    
    private void commitTransaction() throws IOException, InterruptedException
    {
        FACTORY.transactionManager().commitTransaction(TransactionCallback.DO_NOTHING, null);
    }

    private static final int ERDO_ID = 1;
    private static DBStructure DB_STRUCTURE;
    private static final int TREE_ID = 0;
    private static final String[] VALUES = {
        "",
        "a",
        "ab",
        "abc",
        "abcd",
        "abcde",
        "abcdef",
        "abcdefg",
        "abcdefgh",
        "abcdefghi"
    };
    private static TestFactory FACTORY = new TestFactory();
}
