package com.ctriposs.sdb;

import com.ctriposs.sdb.stats.AvgStats;
import com.ctriposs.sdb.stats.SDBStats;
import com.ctriposs.sdb.stats.SingleStats;
import com.ctriposs.sdb.utils.TestUtil;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class SDBTest {

	// You can set the STRESS_FACTOR system property to make the tests run more iterations.
	public static final double STRESS_FACTOR = Double.parseDouble(System.getProperty("STRESS_FACTOR", "1.0"));

	private static String testDir = TestUtil.TEST_BASE_DIR + "sdb/unit/sdb_test";

	private SDB db;

	@Test
	public void testDB() {
		db = new SDB(testDir);

		Set<String> rndStringSet = new HashSet<String>();
		for (int i = 0; i < 2000000 * STRESS_FACTOR; i++) {
			String rndString = TestUtil.randomString(64);
			rndStringSet.add(rndString);
			db.put(rndString.getBytes(), rndString.getBytes());
			if ((i % 50000) == 0 && i != 0) {
				System.out.println(i + " rows written");
			}
		}

		for (String rndString : rndStringSet) {
			byte[] value = db.get(rndString.getBytes());
			assertNotNull(value);
			assertEquals(rndString, new String(value));
		}

		// delete
		for (String rndString : rndStringSet) {
			db.delete(rndString.getBytes());
		}

		for (String rndString : rndStringSet) {
			byte[] value = db.get(rndString.getBytes());
			assertNull(value);
		}

		SDBStats stats = db.getStats();
		long inMemPut = getAvgStatsCount(stats, "put.inMem.cost"),
			 level0Put = getAvgStatsCount(stats, "put.level0.cost");
		assertEquals(rndStringSet.size(), inMemPut + level0Put);
		long inMemGet = getAvgStatsCount(stats, "get.inMem.cost"),
			 level0Get = getAvgStatsCount(stats, "get.level0.cost"),
			 level1Get = getAvgStatsCount(stats, "get.level1.cost"),
			 level2Get = getAvgStatsCount(stats, "get.level2.cost");
		assertEquals(rndStringSet.size() * 2, inMemGet + level0Get + level1Get + level2Get);

		// Make sure FileStatsCollector has enough time to finish up its work.
		try {
			Thread.sleep(10 * 1000);
		} catch (InterruptedException e) {
		}

		outputStats(stats);
	}

	@Test
	public void operationAfterClosedTest() throws Exception {
		db = new SDB(testDir);
		db.close();

		final byte[] testKey = new byte[]{1}, testValue = new byte[]{1};

		try {
			db.get(testKey);
			fail("Should not get here after the SDB is closed.");
		} catch (IllegalStateException e) {
		}

		try {
			db.put(testKey, testValue);
			fail("Should not get here after the SDB is closed.");
		} catch (IllegalStateException e) {
		}

		try {
			db.put(testKey, testValue, 1000);
			fail("Should not get here after the SDB is closed.");
		} catch (IllegalStateException e) {
		}
	}

	@After
	public void clear() throws IOException {
		if (db != null) {
			db.close();
			db.destory();
		}
	}

	private static long getAvgStatsCount(SDBStats stats, String name) {
		AtomicReference<AvgStats> ref = stats.getAvgStatsMap().get(name);
		return ref != null ? ref.get().getCount() : 0;
	}

	private static void outputStats(SDBStats stats) {
		for (String key : stats.getAvgStatsMap().keySet()) {
			AvgStats avgStats = stats.getAvgStatsMap().get(key).get();
			if (avgStats.getCount() == 0) {
				continue;
			}
			System.out.printf("%s: Count %d    Min %d    Max %d   Avg %d", key, avgStats.getCount(), avgStats.getMin(),
					avgStats.getMax(), avgStats.getAvg());
			System.out.println();
		}

		for (String key : stats.getSingleStatsMap().keySet()) {
			SingleStats singleStats = stats.getSingleStatsMap().get(key).get();
			if (singleStats.getValue() == 0) {
				continue;
			}
			System.out.printf("%s: %d", key, singleStats.getValue());
			System.out.println();
		}
	}
}
