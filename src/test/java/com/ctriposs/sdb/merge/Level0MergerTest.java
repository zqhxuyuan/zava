package com.ctriposs.sdb.merge;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.xerial.snappy.Snappy;

import com.ctriposs.sdb.LevelQueue;
import com.ctriposs.sdb.SDB;
import com.ctriposs.sdb.merge.Level0Merger;
import com.ctriposs.sdb.table.AbstractMapTable;
import com.ctriposs.sdb.table.GetResult;
import com.ctriposs.sdb.table.HashMapTable;
import com.ctriposs.sdb.table.IMapEntry;
import com.ctriposs.sdb.table.MMFMapTable;
import com.ctriposs.sdb.utils.TestUtil;

public class Level0MergerTest {
	
	private static String testDir = TestUtil.TEST_BASE_DIR + "sdb/unit/level_0_merger_test";
	
	@Test
	public void testCase02() throws IOException, ClassNotFoundException {
		HashMapTable[] sourceTables = new HashMapTable[4];
		LevelQueue lq0 = new LevelQueue();
		for(int i = 0; i < 4; i++) {
			sourceTables[i] = new HashMapTable(testDir, SDB.LEVEL0, System.nanoTime() + i);
			sourceTables[i].setCompressionEnabled(true);
			lq0.addFirst(sourceTables[i]);
			assertTrue(sourceTables[i].isImmutable());
		}
		
		// delete
		int max = AbstractMapTable.INIT_INDEX_ITEMS_PER_TABLE;
		HashMapTable table4 = sourceTables[3];
		int start = 0;
		while(start < max) {
			table4.delete(String.valueOf(start).getBytes());
			start = start + 4;
		}
		
		// expiration
		HashMapTable table3 = sourceTables[2];
		start = 1;
		while(start < max) {
			table3.put(String.valueOf(start).getBytes(), String.valueOf(start).getBytes(), 200, System.currentTimeMillis());
			start = start + 4;
		}
		
		// expire table3
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// ignore
		}
		
		// time to live 60 sec
		HashMapTable table2 = sourceTables[1];
		start = 2;
		while(start < max) {
			table2.put(String.valueOf(start).getBytes(), String.valueOf(start).getBytes(), 60 * 1000, System.currentTimeMillis());
			start = start + 4;
		}
		
		// time to live 120 sec with items that have been updated(table 2), deleted(table 4) and expired(table 3)
		HashMapTable table1 = sourceTables[0];
		start = 0;
		while(start < max) {
			table1.put(String.valueOf(start).getBytes(), String.valueOf(start).getBytes(), 120 * 1000, System.currentTimeMillis());
			start = start + 1;
		}
		
		LevelQueue lq1 = new LevelQueue();
		
		Level0Merger.mergeSort(lq0, lq1, 4, testDir, (short)0);
		
		for(int i = 0; i < 4; i++) {
			assertTrue(sourceTables[i].isImmutable());
		}
		
		assertTrue(lq1.size() == 1);
		MMFMapTable targetTable = (MMFMapTable) lq1.poll();
		assertTrue(targetTable.getLevel() == SDB.LEVEL1);
		assertTrue(targetTable.getAppendedSize() == max);
		
		// validate delete
		start = 0;
		while(start < max) {
			GetResult result = targetTable.get(String.valueOf(start).getBytes());
			assertTrue(result.isFound());
			assertTrue(result.isDeleted());
			start += 4;
		}
		
		// validate expiration
		start = 1;
		while(start < max) {
			GetResult result = targetTable.get(String.valueOf(start).getBytes());
			assertTrue(result.isFound());
			assertTrue(result.isExpired());
			start += 4;
		}
		
		// validate ttl 60s
		start = 2;
		while(start < max) {
			GetResult result = targetTable.get(String.valueOf(start).getBytes());
			assertTrue(result.isFound());
			assertTrue(result.getTimeToLive() == 60 * 1000);
			start += 4;
		}
		
		// validate ttl 120s
		start = 3;
		while(start < max) {
			GetResult result = targetTable.get(String.valueOf(start).getBytes());
			assertTrue(result.isFound());
			assertTrue(result.getTimeToLive() == 120 * 1000);
			start += 4;
		}
		
		List<String> keyList = new ArrayList<String>();
		for(long i = 0; i < max; i++) {
			keyList.add(String.valueOf(i));
		}
		Collections.sort(keyList, new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				int hash0 = Arrays.hashCode(arg0.getBytes());
				int hash1 = Arrays.hashCode(arg1.getBytes());
				if (hash0 < hash1) return -1;
				else if (hash0 > hash1) return 1;
				else return 0;
			}
			
		});
		
		for(int i = 0; i < max; i++) {
			IMapEntry mapEntry = targetTable.getMapEntry(i);
			assertTrue(mapEntry.getIndex() == i);
			assertTrue(new String(mapEntry.getKey()).equals(keyList.get(i)));
			int intKey = Integer.parseInt(new String(mapEntry.getKey()));
			// validate disk space optimization
			if (intKey % 4 == 0) // deleted
				assertTrue(Arrays.equals(new byte[] {0}, mapEntry.getValue()));
			else if (intKey % 4 == 1) // expired
				assertTrue(Arrays.equals(new byte[] {0}, mapEntry.getValue()));
			else {
				assertTrue(new String(Snappy.uncompress(mapEntry.getValue())).equals(keyList.get(i)));
				//assertTrue(new String(mapEntry.getValue()).equals(keyList.get(i)));
			}
		}
		
		Random random = new Random();
		for(int i = 0; i < 1024; i++) {
			long key = random.nextInt(max);
			GetResult result = targetTable.get(String.valueOf(key).getBytes());
			assertTrue(result.isFound());
		}
		
		targetTable.close();
		targetTable.delete();
	}
	
	@Test
	public void testCase01() throws IOException, ClassNotFoundException {
		
		String value = TestUtil.randomString(128);
		
		HashMapTable[] sourceTables = new HashMapTable[4];
		LevelQueue lq0 = new LevelQueue();
		for(int i = 0; i < 4; i++) {
			sourceTables[i] = new HashMapTable(testDir, SDB.LEVEL0, System.nanoTime() + i);
			sourceTables[i].setCompressionEnabled(true);
			lq0.addFirst(sourceTables[i]);
			assertTrue(sourceTables[i].isImmutable());
		}
		
		int totalCount = 0;
		int max = 0;
		for(int i = 0; i < 4; i++) {
			int start = i;
			HashMapTable table = sourceTables[i];
			while(table.put(String.valueOf(start).getBytes(), value.getBytes(), AbstractMapTable.NO_TIMEOUT, System.currentTimeMillis(), false)) {
				totalCount++;
				if (start > max) max = start;
				start = start + 4;
			}
		}
		
		LevelQueue lq1 = new LevelQueue();
		
		long start = System.currentTimeMillis();
		Level0Merger.mergeSort(lq0, lq1, 4, testDir, (short)1);
		long end = System.currentTimeMillis();
		System.out.println("Time spent to merge " + totalCount + " items in 4 ways  is " + (end - start) / 1000 + "s");
		
		for(int i = 0; i < 4; i++) {
			assertTrue(sourceTables[i].isImmutable());
		}
		
		assertTrue(lq1.size() == 1);
		MMFMapTable targetTable = (MMFMapTable) lq1.poll();
		assertTrue(targetTable.getLevel() == SDB.LEVEL1);
		assertTrue(targetTable.getAppendedSize() == totalCount);
		
		List<String> keyList = new ArrayList<String>();
		for(long i = 0; i < totalCount; i++) {
			keyList.add(String.valueOf(i));
		}
		Collections.sort(keyList, new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				int hash0 = Arrays.hashCode(arg0.getBytes());
				int hash1 = Arrays.hashCode(arg1.getBytes());
				if (hash0 < hash1) return -1;
				else if (hash0 > hash1) return 1;
				else return 0;
			}
			
		});
		
		for(int i = 0; i < totalCount; i++) {
			IMapEntry mapEntry = targetTable.getMapEntry(i);
			assertTrue(mapEntry.getIndex() == i);
			assertTrue(new String(mapEntry.getKey()).equals(keyList.get(i)));
			assertTrue(new String(Snappy.uncompress(mapEntry.getValue())).equals(value));
		}
		
		start = System.currentTimeMillis();
		Random random = new Random();
		for(int i = 0; i < 1024; i++) {
			long key = random.nextInt(totalCount);
			GetResult result = targetTable.get(String.valueOf(key).getBytes());
			assertTrue(result.isFound());
		}
		end = System.currentTimeMillis();
		System.out.println("Time to lookup 1024 random key in the target table is " + (end - start) + "ms");
		
		targetTable.close();
		targetTable.delete();
	}

}
