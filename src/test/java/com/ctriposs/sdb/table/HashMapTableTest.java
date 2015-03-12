package com.ctriposs.sdb.table;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Test;

import com.ctriposs.sdb.table.AbstractMapTable;
import com.ctriposs.sdb.table.GetResult;
import com.ctriposs.sdb.table.HashMapTable;
import com.ctriposs.sdb.table.IMapEntry;
import com.ctriposs.sdb.utils.FileUtil;
import com.ctriposs.sdb.utils.TestUtil;

public class HashMapTableTest {

	private static String testDir = TestUtil.TEST_BASE_DIR + "sdb/unit/hashmap_table_test";

	private HashMapTable mapTable;

	@Test
	public void testEmtpy() throws IOException {
		long createdTime = System.nanoTime();
		mapTable = new HashMapTable(testDir, 0, createdTime);

		assertTrue(mapTable.getLevel() == 0);
		assertTrue(mapTable.getCreatedTime() == createdTime);
		assertTrue(mapTable.getAppendedSize() == 0);
		assertTrue(mapTable.isEmpty());
		assertTrue(mapTable.getBackFileSize() == HashMapTable.INIT_INDEX_FILE_SIZE + HashMapTable.INIT_DATA_FILE_SIZE + HashMapTable.INDEX_ITEM_LENGTH);

		GetResult result = mapTable.get("empty".getBytes());
		assertFalse(result.isFound());
		assertFalse(result.isDeleted() || result.isExpired());

		try {
			mapTable.getMapEntry(-1);
			fail();
		} catch (IllegalArgumentException iae) {

		}

		try {
			mapTable.getMapEntry(0);
		} catch (IllegalArgumentException iae) {

		}
	}

	@Test
	public void testAppendAndGet() throws IOException {
		long createdTime = System.nanoTime();
		mapTable = new HashMapTable(testDir, 0, createdTime);

		assertTrue(mapTable.getLevel() == 0);
		assertTrue(mapTable.getCreatedTime() == createdTime);
		assertTrue(mapTable.getAppendedSize() == 0);
		assertTrue(mapTable.isEmpty());
		assertTrue(mapTable.getBackFileSize() == HashMapTable.INIT_INDEX_FILE_SIZE + HashMapTable.INIT_DATA_FILE_SIZE  + HashMapTable.INDEX_ITEM_LENGTH);

		mapTable.appendNew("key".getBytes(), "value".getBytes(), 500, System.currentTimeMillis());
		assertTrue(mapTable.getLevel() == 0);
		assertTrue(mapTable.getCreatedTime() == createdTime);
		assertTrue(mapTable.getAppendedSize() == 1);
		assertFalse(mapTable.isEmpty());

		IMapEntry mapEntry = mapTable.getMapEntry(0);
		assertTrue(Arrays.equals("key".getBytes(), mapEntry.getKey()));
		assertTrue(Arrays.equals("value".getBytes(), mapEntry.getValue()));
		assertTrue(500 == mapEntry.getTimeToLive());
		assertTrue(System.currentTimeMillis() >= mapEntry.getCreatedTime());
		assertFalse(mapEntry.isDeleted());
		assertTrue(mapEntry.isInUse());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue(mapEntry.isExpired());

		mapEntry.markDeleted();
		assertTrue(mapEntry.isDeleted());
	}

	@Test
	public void testMapOps() throws IOException {
		long createdTime = System.nanoTime();
		mapTable = new HashMapTable(testDir, 0, createdTime);

		assertTrue(mapTable.getLevel() == 0);
		assertTrue(mapTable.getCreatedTime() == createdTime);
		assertTrue(mapTable.getAppendedSize() == 0);
		assertTrue(mapTable.isEmpty());
		assertTrue(mapTable.getBackFileSize() == HashMapTable.INIT_INDEX_FILE_SIZE + HashMapTable.INIT_DATA_FILE_SIZE  + HashMapTable.INDEX_ITEM_LENGTH);

		for(int i = 0; i < 100; i++) {
			mapTable.put(("key"+i).getBytes(), ("value" + i).getBytes(), AbstractMapTable.NO_TIMEOUT, System.currentTimeMillis());
		}

		for(int i = 0; i < 100; i++) {
			if (i % 2 == 0) {
				mapTable.delete(("key" + i).getBytes());
			}
		}
		mapTable.delete(("key" + 100).getBytes());

		for(int i = 0; i < 100; i++) {
			GetResult result = mapTable.get(("key" + i).getBytes());
			if (i % 2 == 0) {
				assertTrue(result.isFound() && result.isDeleted());
			} else {
				assertTrue(result.isFound() && !result.isDeleted() && !result.isExpired());
				assertTrue(Arrays.equals(("value" + i).getBytes(), result.getValue()));
			}
		}

		GetResult result = mapTable.get(("key" + 100).getBytes());
		assertTrue(result.isFound() && result.isDeleted());
		result = mapTable.get(("key" + 101).getBytes());
		assertTrue(!result.isFound() && !result.isDeleted());

		mapTable.close();

		// test expiration
		createdTime = System.nanoTime();
		mapTable = new HashMapTable(testDir, 0, createdTime);
		for(int i = 0; i < 100; i++) {
			mapTable.put(("key" + i).getBytes(), ("value" + i).getBytes(), 200, System.currentTimeMillis());
		}

		try {
			Thread.sleep(600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for(int i = 0; i < 100; i++) {
			result = mapTable.get(("key" + i).getBytes());
			assertTrue(result.isFound() && !result.isDeleted() && result.isExpired());
		}
	}

	@Test
	public void testLoop() throws IOException {
		long createdTime = System.nanoTime();
		mapTable = new HashMapTable(testDir, 0, createdTime);

		assertTrue(mapTable.getLevel() == 0);
		assertTrue(mapTable.getCreatedTime() == createdTime);
		assertTrue(mapTable.getAppendedSize() == 0);
		assertTrue(mapTable.isEmpty());
		assertTrue(mapTable.getBackFileSize() == HashMapTable.INIT_INDEX_FILE_SIZE + HashMapTable.INIT_DATA_FILE_SIZE  + HashMapTable.INDEX_ITEM_LENGTH);

		int loop = 6 * 1024;
		for(int i = 0; i < loop; i++) {
			mapTable.appendNew(("key" + i).getBytes(), ("value" + i).getBytes(), -1, System.currentTimeMillis());
		}

		assertTrue(mapTable.getAppendedSize() == loop);

		for(int i = 0; i < loop; i++) {
			IMapEntry mapEntry = mapTable.getMapEntry(i);
			assertTrue(Arrays.equals(("key" + i).getBytes(), mapEntry.getKey()));
			assertTrue(Arrays.equals(("value" + i).getBytes(), mapEntry.getValue()));
			assertTrue(-1 == mapEntry.getTimeToLive());
			assertTrue(System.currentTimeMillis() >= mapEntry.getCreatedTime());
			assertFalse(mapEntry.isDeleted());
			assertTrue(mapEntry.isInUse());
		}

		for(int i = 0; i < loop; i++) {
			GetResult result = mapTable.get(("key" + i).getBytes());
			assertTrue(result.isFound());
			assertFalse(result.isDeleted() || result.isExpired());
			assertTrue(Arrays.equals(("value" + i).getBytes(),result.getValue()));
		}

		GetResult result = mapTable.get(("key" + loop).getBytes());
		assertFalse(result.isFound());
		assertFalse(result.isDeleted() || result.isExpired());

		try {
			mapTable.getMapEntry(loop);
		} catch (IllegalArgumentException iae) {

		}
	}

	@Test
	public void testReOpen() throws IOException {
		long createdTime = System.nanoTime();
		mapTable = new HashMapTable(testDir, 0, createdTime);

		assertTrue(mapTable.getLevel() == 0);
		assertTrue(mapTable.getCreatedTime() == createdTime);
		assertTrue(mapTable.getAppendedSize() == 0);
		assertTrue(mapTable.isEmpty());
		assertTrue(mapTable.getBackFileSize() == HashMapTable.INIT_INDEX_FILE_SIZE + HashMapTable.INIT_DATA_FILE_SIZE  + HashMapTable.INDEX_ITEM_LENGTH);

		int loop = 1024;
		for(int i = 0; i < loop; i++) {
			mapTable.appendNew(("key" + i).getBytes(), ("value" + i).getBytes(), -1, System.currentTimeMillis());
		}

		assertTrue(mapTable.getAppendedSize() == loop);

		mapTable.close();

		mapTable = new HashMapTable(testDir, 0, createdTime);

		for(int i = 0; i < loop; i++) {
			IMapEntry mapEntry = mapTable.getMapEntry(i);
			assertTrue(Arrays.equals(("key" + i).getBytes(), mapEntry.getKey()));
			assertTrue(Arrays.equals(("value" + i).getBytes(), mapEntry.getValue()));
			assertTrue(-1 == mapEntry.getTimeToLive());
			assertTrue(System.currentTimeMillis() >= mapEntry.getCreatedTime());
			assertFalse(mapEntry.isDeleted());
			assertTrue(mapEntry.isInUse());
		}

		try {
			mapTable.getMapEntry(loop);
		} catch (IllegalArgumentException iae) {

		}

		for(int i = 0; i < loop; i++) {
			GetResult result = mapTable.get(("key" + i).getBytes());
			assertTrue(result.isFound());
			assertFalse(result.isDeleted() || result.isExpired());
			assertTrue(Arrays.equals(("value" + i).getBytes(),result.getValue()));
		}

		GetResult result = mapTable.get(("key" + loop).getBytes());
		assertFalse(result.isFound());
		assertFalse(result.isDeleted() || result.isExpired());

	}

	@Test
	public void testAppendConcurrency() throws IOException, InterruptedException, ExecutionException {
		ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		long createdTime = System.nanoTime();
		mapTable = new HashMapTable(testDir, 1, createdTime);

		List<Future<?>> futures = new ArrayList<Future<?>>();

		int N_THREADS = 128;
		final int LOOP = 512;
		final AtomicInteger failedCount = new AtomicInteger(0);
		for(int t = 0; t < N_THREADS; t++) {
			final int finalT = t;
			futures.add(es.submit(new Runnable() {

				@Override
				public void run() {
					for(int i = 0; i < LOOP; i++) {
						try {
							mapTable.appendNew(("" + finalT).getBytes(), ("" + i).getBytes(), -1, System.currentTimeMillis());
						} catch (IOException e) {
							e.printStackTrace();
							failedCount.incrementAndGet();
						}
					}

				}

			}));
		}

		for(Future<?> future : futures) {
			future.get();
		}

		assertTrue(failedCount.get() == 0);
		assertTrue(mapTable.getAppendedSize() == N_THREADS * LOOP);

		Map<Integer, Set<Integer>> resultMap = new HashMap<Integer, Set<Integer>>();
		for(int i = 0; i < mapTable.getAppendedSize(); i++) {
			IMapEntry mapEntry = mapTable.getMapEntry(i);
			String key = new String(mapEntry.getKey());
			String value = new String(mapEntry.getValue());
			if (!resultMap.containsKey(Integer.parseInt(key))) {
				resultMap.put(Integer.parseInt(key), new HashSet<Integer>());
			}
			resultMap.get(Integer.parseInt(key)).add(Integer.parseInt(value));
		}

		assertTrue(resultMap.size() == N_THREADS);
		Set<Integer> keySet = resultMap.keySet();
		for(int t = 0; t < N_THREADS; t++) {
			keySet.contains(t);
		}
		for(Integer key : keySet) {
			Set<Integer> valueSet = resultMap.get(key);
			assertTrue(valueSet.size() == LOOP);
			for(int l = 0; l < LOOP; l++) {
				valueSet.contains(l);
			}
		}

		es.shutdown();
	}

	@Test
	public void operationAfterClosedTest() throws Exception {
		long createdTime = System.nanoTime();
		mapTable = new HashMapTable(testDir, 1, createdTime);
		mapTable.close();

		final byte[] testKey = new byte[]{1}, testValue = new byte[]{1};

		try {
			mapTable.appendNew(testKey, testValue, 1000, System.currentTimeMillis());
			fail("Should not get here after the SDB is closed.");
		} catch (IllegalStateException e) {
		}

		try {
			mapTable.getMapEntry(0);
			fail("Should not get here after the SDB is closed.");
		} catch (IllegalStateException e) {
		}

		try {
			mapTable.getEntrySet();
			fail("Should not get here after the SDB is closed.");
		} catch (IllegalStateException e) {
		}

		try {
			mapTable.put(testKey, testValue, 1000, System.currentTimeMillis());
			fail("Should not get here after the SDB is closed.");
		} catch (IllegalStateException e) {
		}

		try {
			mapTable.put(testKey, testValue, 1000, System.currentTimeMillis(), true);
			fail("Should not get here after the SDB is closed.");
		} catch (IllegalStateException e) {
		}

		try {
			mapTable.delete(testKey);
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
