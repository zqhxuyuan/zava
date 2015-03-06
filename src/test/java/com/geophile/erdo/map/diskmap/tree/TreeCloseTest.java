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
import com.geophile.erdo.transaction.TransactionManager;
import com.geophile.erdo.util.FileUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;

// Test logic around closing files and finalizing right edge. This is done by creating trees with N records,
// 0 <= N <= max, close, cursor.

public class TreeCloseTest
{
    @BeforeClass
    public static void beforeClass() throws IOException
    {
        TestKey.testErdoId(ERDO_ID);
        DB_STRUCTURE = new DBStructure(new File(FileUtil.tempDirectory(), "erdo"));
        FACTORY = new TestFactory();
        FACTORY.recordFactory(ERDO_ID, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        FACTORY.configuration().diskPageSizeBytes(4096);
        FACTORY.configuration().diskSegmentSizeBytes(8192);
        Transaction.initialize(FACTORY);
    }

    @Test
    public void test() throws Exception
    {
        for (int n = 0; n <= N_MAX; n++) {
            reset();
            test(n);
        }
    }

    private void reset() throws IOException
    {
        FileUtil.deleteDirectory(DB_STRUCTURE.dbDirectory());
        FileUtil.ensureDirectoryExists(DB_STRUCTURE.dbDirectory());
        FileUtil.ensureDirectoryExists(DB_STRUCTURE.forestDirectory());
        FileUtil.ensureDirectoryExists(DB_STRUCTURE.segmentsDirectory());
        FileUtil.ensureDirectoryExists(DB_STRUCTURE.summariesDirectory());
        FACTORY.reset();
    }

    private void test(int n) throws Exception
    {
        WriteableTree writeableTree = Tree.create(FACTORY, DB_STRUCTURE, TREE_ID);
        TransactionManager transactionManager = FACTORY.transactionManager();
        for (int i = 0; i < n; i++) {
            startTransaction();
            TestRecord record = TestRecord.createRecord(i, FILLER);
            record.key().transaction(transactionManager.currentTransaction());
            transactionManager.commitTransaction(TransactionCallback.DO_NOTHING, null);
            writeableTree.append(record);
        }
        Tree tree = writeableTree.close();
        startTransaction();
        MapCursor cursor = tree.cursor(null);
        int expected = 0;
        LazyRecord lazyRecord;
        while ((lazyRecord = cursor.next()) != null) {
            TestRecord record = (TestRecord) lazyRecord.materializeRecord();
            int key = record.key().key();
            assertEquals(expected, key);
            assertEquals(FILLER, record.stringValue());
            expected++;
        }
        transactionManager.commitTransaction(TransactionCallback.DO_NOTHING, null);
        assertEquals(n, expected);
    }

    private void startTransaction()
    {
        FACTORY.transactionManager().currentTransaction(); // ensures transaction started
    }

    private static final int ERDO_ID = 1;
    private static final int N_MAX = 1000;
    private static final String FILLER =
        "xxxxxxxxxxxxxxxxxxxx" +
        "xxxxxxxxxxxxxxxxxxxx" +
        "xxxxxxxxxxxxxxxxxxxx" +
        "xxxxxxxxxxxxxxxxxxxx" +
        "xxxxxxxxxxxxxxxxxxxx";
    private static DBStructure DB_STRUCTURE;
    private static final int TREE_ID = 0;
    private static TestFactory FACTORY;
}
