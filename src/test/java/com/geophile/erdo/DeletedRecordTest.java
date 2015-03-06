package com.geophile.erdo;

import com.geophile.erdo.apiimpl.DatabaseImpl;
import com.geophile.erdo.apiimpl.DatabaseOnDisk;
import com.geophile.erdo.util.FileUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// Tests handling of deleted records, through consolidations that must preserve them, and consolidations that must
// drop them.

public class DeletedRecordTest
{
    @BeforeClass
    public static void beforeClass() throws IOException
    {
        DB_DIRECTORY = new File(FileUtil.tempDirectory(), DB_NAME);
    }

    @Before
    public void before()
    {
        FileUtil.deleteDirectory(DB_DIRECTORY);
    }

    @After
    public void after()
    {
        ((TestFactory)db.factory()).reset();
    }

    @Test
    public void noPersistentDeletedRecords()
        throws IOException, InterruptedException, DeadlockException, TransactionRolledBackException
    {
        // Turn off background consolidation
        Configuration configuration = Configuration.defaultConfiguration();
        configuration.consolidationMinMapsToConsolidate(Integer.MAX_VALUE);
        configuration.consolidationMinSizeBytes(Integer.MAX_VALUE);
        configuration.consolidationThreads(0);
        db = DatabaseOnDisk.createDatabase(DB_DIRECTORY, configuration, TestFactory.class);
        map = db.createMap("test", RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        final int N = 10;
        {
            // Transaction 0: Load a private map with keys 0..N-1 and delete N/2
            for (int key = 0; key < N; key++) {
                insert(key, "a");
            }
            delete(N/2);
            db.commitTransaction();
        }
        {
            // Transaction 1: Load a private map with keys N..2N-1, reinsert N/2, and delete N + N/2
            for (int key = N; key < 2 * N; key++) {
                insert(key, "b");
            }
            insert(N / 2, "c");
            delete(N + N/2);
            db.commitTransaction();
        }
        {
            // Force consolidation
            ((DatabaseOnDisk)db).consolidateAll();
        }
        checkContents(2 * N);
        db.close();
    }

    @Test
    public void testWithPersistentDeletedRecords()
        throws IOException, InterruptedException, DeadlockException, TransactionRolledBackException
    {
        FileUtil.deleteDirectory(DB_DIRECTORY);
        // Turn off background consolidation
        Configuration configuration = Configuration.defaultConfiguration();
        configuration.consolidationMinMapsToConsolidate(2);
        configuration.consolidationMinSizeBytes(10);
        configuration.consolidationThreads(1);
        db = DatabaseOnDisk.createDatabase(DB_DIRECTORY, configuration, TestFactory.class);
        map = db.createMap("test", RecordFactory.simpleRecordFactory(TestKey.class, TestRecord.class));
        final int MAX_KEY = 20;
        final int OPERATIONS = 1000;
        final int OPERATIONS_PER_TXN = 10;
        final double DELETE_PROBABILITY = 0.3;
        final Random RANDOM = new Random();
        String transaction = null;
        for (int i = 0; i < OPERATIONS; i++) {
            if (i % OPERATIONS_PER_TXN == 0) {
                db.commitTransaction();
                transaction = String.format("txn(%s)", i / OPERATIONS_PER_TXN);
            }
            int key = RANDOM.nextInt(MAX_KEY);
            if (RANDOM.nextDouble() < DELETE_PROBABILITY) {
                delete(key);
            } else {
                insert(key, transaction);
            }
        }
        db.commitTransaction();
        checkContents(MAX_KEY);
        db.close();
        assertTrue(db.factory().testObserver().writeDeletedKeyCount() > 0);
        assertTrue(db.factory().testObserver().readDeletedKeyCount() > 0);
    }

    private void insert(int key, String value)
        throws InterruptedException, DeadlockException, TransactionRolledBackException, IOException
    {
        map.put(TestRecord.createRecord(key, value));
        expected.put(key, value);
    }

    private void delete(int key)
        throws InterruptedException, DeadlockException, TransactionRolledBackException, IOException
    {
        map.delete(new TestKey(key));
        expected.put(key, null);
    }

    private void checkContents(int n) throws IOException, InterruptedException
    {
        Cursor cursor = map.first();
        TestRecord record;
        for (int key = 0; key < n; key++) {
            String expectedValue = expected.get(key);
            if (expectedValue != null) {
                record = (TestRecord) cursor.next();
                assertEquals(key, record.key().key());
                assertEquals(expectedValue, record.stringValue());
            }
        }
    }

    private static final String DB_NAME = "erdo";
    private static File DB_DIRECTORY;

    private DatabaseImpl db;
    private OrderedMap map;
    private Map<Integer, String> expected = new HashMap<>();
}
