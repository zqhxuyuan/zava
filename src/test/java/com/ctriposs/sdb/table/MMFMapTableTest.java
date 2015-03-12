package com.ctriposs.sdb.table;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import com.ctriposs.sdb.merge.Level0Merger;
import com.ctriposs.sdb.table.GetResult;
import com.ctriposs.sdb.table.MMFMapTable;
import com.ctriposs.sdb.utils.FileUtil;
import com.ctriposs.sdb.utils.TestUtil;

public class MMFMapTableTest {

	private static String testDir = TestUtil.TEST_BASE_DIR + "sdb/unit/mmf_map_table_test";

	private MMFMapTable mapTable;

	@Test
	public void testEmtpy() throws IOException, ClassNotFoundException {
		long createdTime = System.nanoTime();
		mapTable = new MMFMapTable(testDir, 1, createdTime, 1000, 4);

		assertTrue(mapTable.getLevel() == 1);
		assertTrue(mapTable.getCreatedTime() == createdTime);
		assertTrue(mapTable.getAppendedSize() == 0);
		assertTrue(mapTable.isEmpty());
		//assertTrue(mapTable.getBackFileSize() == (MMFMapTable.INIT_INDEX_FILE_SIZE + MMFMapTable.INIT_DATA_FILE_SIZE) * Level0Merger.DEFAULT_MERGE_WAYS);
		assertFalse(mapTable.isUsable());

		try {
			@SuppressWarnings("unused")
			GetResult result = mapTable.get("empty".getBytes());
			fail();
		} catch (IllegalArgumentException iae) {

		}

		try {
			mapTable.getMapEntry(-1);
			fail();
		} catch (IllegalArgumentException iae) {

		}

		try {
			mapTable.getMapEntry(0);
			fail();
		} catch (IllegalArgumentException iae) {

		}
	}

	@Test
	public void testAppendAndGet() throws IOException, ClassNotFoundException {
		long createdTime = System.nanoTime();
		mapTable = new MMFMapTable(testDir, 1, createdTime, 1000, 4);

		assertTrue(mapTable.getLevel() == 1);
		assertTrue(mapTable.getCreatedTime() == createdTime);
		assertTrue(mapTable.getAppendedSize() == 0);
		assertTrue(mapTable.isEmpty());
		//assertTrue(mapTable.getBackFileSize() == (MMFMapTable.INIT_INDEX_FILE_SIZE + MMFMapTable.INIT_DATA_FILE_SIZE) * Level0Merger.DEFAULT_MERGE_WAYS);

		mapTable.appendNew("key".getBytes(), "value".getBytes(), 500);
		assertTrue(mapTable.getLevel() == 1);
		assertTrue(mapTable.getCreatedTime() == createdTime);
		assertTrue(mapTable.getAppendedSize() == 1);
		assertFalse(mapTable.isEmpty());

		GetResult result = mapTable.get("key".getBytes());
		assertTrue(result.isFound());
		assertTrue(!result.isDeleted());
		assertTrue(!result.isExpired());

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		result = mapTable.get("key".getBytes());
		assertTrue(result.isFound());
		assertTrue(!result.isDeleted());
		assertTrue(result.isExpired());

		result = mapTable.get("key1".getBytes());
		assertFalse(result.isFound());

		assertFalse(mapTable.isUsable());

	}


	@Test
	public void testLoopAndReopen() throws IOException, ClassNotFoundException {
		long createdTime = System.nanoTime();
		int loop =  32 * 1024;
		mapTable = new MMFMapTable(testDir, 1, createdTime, loop, 4);

		assertTrue(mapTable.getLevel() == 1);
		assertTrue(mapTable.getCreatedTime() == createdTime);
		assertTrue(mapTable.getAppendedSize() == 0);
		assertTrue(mapTable.isEmpty());
		//assertTrue(mapTable.getBackFileSize() == (MMFMapTable.INIT_INDEX_FILE_SIZE + MMFMapTable.INIT_DATA_FILE_SIZE) * Level0Merger.DEFAULT_MERGE_WAYS);
		assertFalse(mapTable.isUsable());


		List<byte[]> list = new ArrayList<byte[]>();
		for(int i = 0; i < loop; i++) {
			list.add(("key" + i).getBytes());
		}
		Collections.sort(list, new Comparator<byte[]>() {

			@Override
			public int compare(byte[] arg0, byte[] arg1) {
				int hash0 = Arrays.hashCode(arg0);
				int hash1 = Arrays.hashCode(arg1);
				if (hash0 < hash1) return -1;
				else if (hash0 > hash1) return 1;
				else return 0;
			}


		});

		for(int i = 0; i < loop; i++) {
			mapTable.appendNew(list.get(i), ("value" + i).getBytes(), -1);
		}

		assertTrue(mapTable.getAppendedSize() == loop);

		mapTable.reMap();
		assertTrue(mapTable.getBackFileSize() < (MMFMapTable.INIT_INDEX_FILE_SIZE + MMFMapTable.INIT_DATA_FILE_SIZE) * Level0Merger.DEFAULT_MERGE_WAYS);

		long start = System.currentTimeMillis();
		for(int i = 0; i < loop; i++) {
			GetResult result = mapTable.get(("key" + i).getBytes());
			assertTrue(result.isFound());
		}
		long time = System.currentTimeMillis() - start;
		System.out.printf("Get %,d K ops per second%n",
				(int) (loop / time));

		GetResult result = mapTable.get(("key" + loop).getBytes());
		assertFalse(result.isFound());
		assertFalse(result.isDeleted() || result.isExpired());

		mapTable.markUsable(true);

		mapTable.saveMetadata();
		mapTable.close();

		mapTable = new MMFMapTable(testDir, mapTable.getFileName());
		assertTrue(mapTable.isUsable());

		assertTrue(mapTable.getAppendedSize() == loop);

		for(int i = 0; i < loop; i++) {
			result = mapTable.get(("key" + i).getBytes());
			assertTrue(result.isFound());
		}

		result = mapTable.get(("key" + loop).getBytes());
		assertFalse(result.isFound());
		assertFalse(result.isDeleted() || result.isExpired());

		mapTable.markUsable(false);

		mapTable.saveMetadata();
		mapTable.close();

		mapTable = new MMFMapTable(testDir, mapTable.getFileName());
		assertFalse(mapTable.isUsable());

		assertTrue(mapTable.getAppendedSize() == loop);

		for(int i = 0; i < loop; i++) {
			result = mapTable.get(("key" + i).getBytes());
			assertTrue(result.isFound());
		}

	}

	@Test
	public void operationAfterClosedTest() throws Exception {
		long createdTime = System.nanoTime();
		mapTable = new MMFMapTable(testDir, 1, createdTime, 1, 4);
		mapTable.close();

		final byte[] testKey = new byte[]{1}, testValue = new byte[]{1};

		try {
			mapTable.appendNew(testKey, testValue, 1000);
			fail("Should not get here after the SDB is closed.");
		} catch (IllegalStateException e) {
		}

		try {
			mapTable.appendNew(testKey,  Arrays.hashCode(testKey), testValue, 1000, System.currentTimeMillis(), false, false);
			fail("Should not get here after the SDB is closed.");
		} catch (IllegalStateException e) {
		}

		try {
			mapTable.getMapEntry(0);
			fail("Should not get here after the SDB is closed.");
		} catch (IllegalStateException e) {
		}

		try {
			mapTable.reMap();
			fail("Should not get here after the SDB is closed.");
		} catch (IllegalStateException e) {
		}

		try {
			mapTable.persistToAppendDataFileOffset();
			fail("Should not get here after the SDB is closed.");
		} catch (IllegalStateException e) {
		}

		try {
			mapTable.persistToAppendIndex();
			fail("Should not get here after the SDB is closed.");
		} catch (IllegalStateException e) {
		}

		try {
			mapTable.persistBloomFilter();
			fail("Should not get here after the SDB is closed.");
		} catch (IllegalStateException e) {
		}

		try {
			mapTable.saveMetadata();
			fail("Should not get here after the SDB is closed.");
		} catch (IllegalStateException e) {
		}
	}

	@After
	public void clear() throws IOException {
		if (mapTable != null) {
			mapTable.close();
		}
		FileUtil.deleteDirectory(new File(testDir));
	}

}
