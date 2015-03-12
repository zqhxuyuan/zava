package com.ctriposs.sdb.merge;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import com.ctriposs.sdb.LevelQueue;
import com.ctriposs.sdb.SDB;
import com.ctriposs.sdb.merge.Level1Merger;
import com.ctriposs.sdb.table.AbstractMapTable;
import com.ctriposs.sdb.table.FCMapTable;
import com.ctriposs.sdb.table.GetResult;
import com.ctriposs.sdb.table.IMapEntry;
import com.ctriposs.sdb.table.MMFMapTable;
import com.ctriposs.sdb.utils.TestUtil;

public class Level1MergerTest {
	
	private static String testDir = TestUtil.TEST_BASE_DIR + "sdb/unit/level_1_merger_test";
	
	@Test
	public void testCase02() throws IOException, ClassNotFoundException {
		int maxSize = AbstractMapTable.INIT_INDEX_ITEMS_PER_TABLE * 4 * 4;
		//int maxSize = 1024;
		
		MMFMapTable[] sourceTables = new MMFMapTable[3];
		LevelQueue lq1 = new LevelQueue();
		for(int i = 0; i < 3; i++) {
			sourceTables[i] = new MMFMapTable(testDir, SDB.LEVEL1, System.nanoTime() + i, maxSize / 4, 4);
			lq1.addFirst(sourceTables[i]);
		}
		
		// delete
		MMFMapTable table3 = sourceTables[2];
		int start = 0;
		List<String> keyList = new ArrayList<String>();
		while(start < maxSize) {
			keyList.add(String.valueOf(start));
			start = start + 4;
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
		
		for(String key : keyList) {
			table3.appendNew(key.getBytes(), Arrays.hashCode(key.getBytes()), key.getBytes(), AbstractMapTable.NO_TIMEOUT, System.currentTimeMillis(), true, false);
		}
		
		// expiration
		MMFMapTable table2 = sourceTables[1];
		start = 1;
		keyList = new ArrayList<String>();
		while(start < maxSize) {
			keyList.add(String.valueOf(start));
			start = start + 4;
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
		
		for(String key : keyList) {
			table2.appendNew(key.getBytes(), Arrays.hashCode(key.getBytes()), key.getBytes(), 200, System.currentTimeMillis(), false, false);
		}
		
		// expire table2
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// ignore
		}
		
		
		// time to live 60 sec
		MMFMapTable table1 = sourceTables[0];
		start = 2;
		keyList = new ArrayList<String>();
		while(start < maxSize) {
			keyList.add(String.valueOf(start));
			start = start + 4;
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
		
		for(String key : keyList) {
			table1.appendNew(key.getBytes(), Arrays.hashCode(key.getBytes()), key.getBytes(), 600 * 1000, System.currentTimeMillis(), false, false);
		}
		
		//int expectedInserts = (int)(table1.getAppendedSize() + table2.getAppendedSize() + table3.getAppendedSize());
		FCMapTable table4 = new FCMapTable(testDir, SDB.LEVEL2, System.nanoTime() + 3, maxSize);
		
		start = 0;
		keyList = new ArrayList<String>();
		while(start < maxSize) {
			keyList.add(String.valueOf(start));
			start = start + 1;
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
		
		for(String key : keyList) {
			table4.appendNew(key.getBytes(), Arrays.hashCode(key.getBytes()), key.getBytes(), 1200 * 1000, System.currentTimeMillis(), false, false);
		}
		
		LevelQueue lq2 = new LevelQueue();
		lq2.add(table4);
		
		Level1Merger.mergeSort(lq1, lq2, 4, testDir, (short)2);
		
		assertTrue(lq1.size() == 0);
		assertTrue(lq2.size() == 1);
		FCMapTable targetTable = (FCMapTable) lq2.poll();

		System.out.println(targetTable.getAppendedSize() + "==" + maxSize / 2);
		assertTrue(targetTable.getAppendedSize() == maxSize / 2);
		
		/*
		// validate delete
		start = 0;
		while(start < maxSize) {
			GetResult result = targetTable.get(String.valueOf(start).getBytes());
			assertFalse(result.isFound());
			start += 4;
		}
		
		// validate expiration
		start = 1;
		while(start < maxSize) {
			GetResult result = targetTable.get(String.valueOf(start).getBytes());
			assertFalse(result.isFound());
			start += 4;
		}
		
		// validate ttl 60s
		start = 2;
		while(start < maxSize) {
			GetResult result = targetTable.get(String.valueOf(start).getBytes());
			assertTrue(result.isFound());
			assertTrue(result.getTimeToLive() == 600 * 1000);
			start += 4;
		}
		
		// validate ttl 120s
		start = 3;
		while(start < maxSize) {
			GetResult result = targetTable.get(String.valueOf(start).getBytes());
			assertTrue(result.isFound());
			assertTrue(result.getTimeToLive() == 1200 * 1000);
			start += 4;
		}*/
		
		keyList = new ArrayList<String>();
		for(long i = 0; i < maxSize; i++) {
			if (i % 4 == 0 || i % 4 == 1) continue;
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
		
		int index = 0;
		for(int i = 0; i < maxSize; i++) {
			// ignore deleted & expired
			if (i % 4 == 0 || i % 4 == 1) continue;
			IMapEntry mapEntry = targetTable.getMapEntry(index);
			assertTrue(mapEntry.getIndex() == index);
			assertTrue(new String(mapEntry.getKey()).equals(keyList.get(index)));
			assertTrue(new String(mapEntry.getValue()).equals(keyList.get(index)));
			index++;
		}
		
		Random random = new Random();
		for(int i = 0; i < 1024; i++) {
			int key = random.nextInt(maxSize);
			// ignore deleted & expired
			if (key % 4 == 0 || key % 4 == 1) continue;
			GetResult result = targetTable.get(String.valueOf(key).getBytes());
			assertTrue(result.isFound());
		}
		
		targetTable.close();
		targetTable.delete();
	}
	
	@Test
	public void testCase01() throws IOException, ClassNotFoundException {
		int maxSize = AbstractMapTable.INIT_INDEX_ITEMS_PER_TABLE;
		String value = TestUtil.randomString(1024 * 3);
		
		MMFMapTable[] sourceTables = new MMFMapTable[4];
		LevelQueue lq1 = new LevelQueue();
		for(int i = 0; i < 4; i++) {
			sourceTables[i] = new MMFMapTable(testDir, SDB.LEVEL1, System.nanoTime() + i, maxSize, 4);
			lq1.addFirst(sourceTables[i]);
		}
		
		int totalCount = 0;
		for(int i = 0; i < 4; i++) {
			int start = i;
			MMFMapTable table = sourceTables[i];
			
			List<String> keyList = new ArrayList<String>();
			while(keyList.size() < maxSize) {
				keyList.add(String.valueOf(start));
				totalCount++;
				start = start + 4;
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
			
			for(String key : keyList) {
				table.appendNew(key.getBytes(), value.getBytes(), AbstractMapTable.NO_TIMEOUT);
			}
		}
		
		LevelQueue lq2 = new LevelQueue();
		
		long start = System.currentTimeMillis();
		Level1Merger.mergeSort(lq1, lq2, 4, testDir, (short)3);
		long end = System.currentTimeMillis();
		System.out.println("Time spent to merge " + totalCount + " items in 4 ways  is " + (end - start) / 1000 + "s");
		
		assertTrue(lq1.size() == 0);
		assertTrue(lq2.size() == 1);
		FCMapTable targetTable = (FCMapTable) lq2.poll();
		assertTrue(targetTable.getLevel() == SDB.LEVEL2);
		assertTrue(targetTable.getAppendedSize() == totalCount);
		assertTrue(totalCount == maxSize * 4);
		
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
			assertTrue(new String(mapEntry.getValue()).equals(value));
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
