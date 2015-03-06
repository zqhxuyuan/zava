/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap;

import com.geophile.erdo.*;
import com.geophile.erdo.transaction.Transaction;
import com.geophile.erdo.transaction.TransactionManager;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static junit.framework.Assert.*;

public class DiskPageTest
{
    @BeforeClass
    public static void beforeClass()
    {
        pageSize = FACTORY.configuration().diskPageSizeBytes();
        FACTORY.recordFactory(ERDO_ID, RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        Transaction.initialize(FACTORY);
    }

    @Before
    public void before()
    {
        TestKey.testErdoId(ERDO_ID);
    }

    @After
    public void after()
    {
        FACTORY.reset();
    }

    @Test
    public void testFixedSizedRecords() throws Exception
    {
        for (int size = 1; size < pageSize / 2; size++) {
            testSize(size);
        }
    }

    @Test
    public void testSearch() throws Exception
    {
        for (int nRecords = 0; nRecords < 100; nRecords++) {
            testSearch(nRecords);
        }
    }

    private void testSize(int size) throws Exception
    {
        ByteBuffer erdoIdBuffer = newDiskPageSectionBuffer();
        ByteBuffer timestampBuffer = newDiskPageSectionBuffer();
        ByteBuffer keyBuffer = newDiskPageSectionBuffer();
        ByteBuffer recordBuffer = newDiskPageSectionBuffer();
        DiskPage page = new DiskPage(FACTORY,
                                     new PageId(0, 0),
                                     0,
                                     0,
                                     erdoIdBuffer,
                                     timestampBuffer,
                                     keyBuffer,
                                     recordBuffer);
        int i = 0;
        boolean appended;
        byte[] bytes = new byte[size];
        TransactionManager transactionManager = FACTORY.transactionManager();
        do {
            startTransaction();
            Arrays.fill(bytes, (byte) (i % 256));
            TestRecord record = TestRecord.createRecord(i, bytes);
            AbstractKey key = record.key();
            Transaction transaction = transactionManager.currentTransaction();
            key.transaction(transaction);
            assert key.transaction() == transaction;
            transactionManager.commitTransaction(TransactionCallback.DO_NOTHING, null);
            appended = page.append(record);
            i++;
        } while (appended);
        page.close();
/* TODO: Disabled until disk page space optimization is done.
        assertTrue(String.format("page.filledSize: %s, size: %s", page.filledSize(), size),
                   pageSize - page.filledSize() < keyAndRecordSize(size));
*/
        page = readable(erdoIdBuffer, timestampBuffer, keyBuffer, recordBuffer);
        startTransaction();
        DiskPageCursor cursor = new DiskPageCursor(page);
        TestRecord record;
        i = 0;
        while ((record = (TestRecord) cursor.next()) != null) {
            Arrays.fill(bytes, (byte) (i % 256));
            assertEquals(i, record.key().key());
            compare(bytes, record.bytesValue());
            i++;
        }
        transactionManager.commitTransaction(TransactionCallback.DO_NOTHING, null);
        assertEquals(i, page.nRecords());
    }

    private DiskPage readable(ByteBuffer erdoIdBuffer,
                              ByteBuffer timestampBuffer,
                              ByteBuffer keyBuffer,
                              ByteBuffer recordBuffer)
    {
        ByteBuffer pageBuffer = ByteBuffer.allocate(FACTORY.configuration().diskPageSizeBytes());
        erdoIdBuffer.flip();
        pageBuffer.put(erdoIdBuffer);
        timestampBuffer.flip();
        pageBuffer.put(timestampBuffer);
        keyBuffer.flip();
        pageBuffer.put(keyBuffer);
        recordBuffer.flip();
        pageBuffer.put(recordBuffer);
        return new DiskPage(FACTORY, new PageId(0, 0), 0, 0, erdoIdBuffer, timestampBuffer, keyBuffer, recordBuffer);
    }

    private void startTransaction()
    {
        FACTORY.transactionManager().currentTransaction(); // ensures transaction started
    }

    private void testSearch(int nRecords) throws Exception
    {
        byte[] value = new byte[]{0};
        // Keys stored are 0, 2, 4, ..., 2 * (nRecords - 1)
        DiskPage page =
            new DiskPage(FACTORY,
                         new PageId(0, 0),
                         0,
                         0,
                         newDiskPageSectionBuffer(),
                         newDiskPageSectionBuffer(),
                         newDiskPageSectionBuffer(),
                         newDiskPageSectionBuffer());
        TransactionManager transactionManager = FACTORY.transactionManager();
        for (int i = 0; i < nRecords; i++) {
            startTransaction();
            int key = i * 2;
            TestRecord record = TestRecord.createRecord(key, value);
            Transaction transaction = transactionManager.currentTransaction();
            record.key().transaction(transaction);
            assert record.key().transaction() == transaction;
            transactionManager.commitTransaction(TransactionCallback.DO_NOTHING, null);
            boolean appended = page.append(record);
            assertTrue(appended);
        }
        assertNotNull(page);
        try {
            page.close();
            assertTrue(nRecords > 0);
        } catch (AssertionError e) {
            assertEquals(0, nRecords);
        }
        for (int key = -1; key <= 2 * nRecords - 1; key++) {
            int position = page.recordNumber(new TestKey(key));
            if (nRecords == 0) {
                assertEquals(-1, position);
            } else if (key < 0) {
                assertEquals(-1, position);
            } else if (key > 2 * (nRecords - 1)) {
                assertEquals(-nRecords - 1, position);
            } else if (key % 2 == 0) {
                assertEquals(key / 2, position);
            } else {
                assertEquals(-(key / 2 + 1) - 1, position);
            }
        }
    }

    private int keyAndRecordSize(int valueSize)
    {
        // TestKey:
        //     4: erdoId
        //     2: timestamp (assuming 2-byte timestamp deltas on page)
        //     4: int key value
        // TestRecord:
        //     1: type tag
        //     4: size
        //     valueSize: value
        //     4: index entries in key and record DiskPageSections
        return 19 + valueSize;
    }

    private void compare(byte[] expected, byte[] actual)
    {
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i]);
        }
    }

    private static void clearDirectory(File dir, String name)
    {
        dir.mkdirs();
        File directory = new File(dir, name);
        if (directory.exists()) {
            deleteRecursively(directory);
        }
    }

    private static void deleteRecursively(File file)
    {
        if (file.isFile()) {
            file.delete();
        } else {
            for (File child : file.listFiles()) {
                deleteRecursively(child);
            }
        }
    }

    private ByteBuffer newDiskPageSectionBuffer()
    {
        return ByteBuffer.allocate(pageSize);
    }

    private static final TestFactory FACTORY = new TestFactory();
    private static final int ERDO_ID = 1;
    private static int pageSize;
}
