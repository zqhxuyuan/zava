package com.ctriposs.bigmap;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Test;

import com.ctriposs.bigmap.utils.FileUtil;

public class MapEntryFactoryTest {
	
	private IMapEntryFactory mapEntryPool;
	private String testDir = TestUtil.TEST_BASE_DIR + "bigmap/unit/map_entry_pool_test";

	@Test
	public void TestAcquireAndRelease() throws IOException {
		mapEntryPool = new MapEntryFactoryImpl(testDir, "acquireAndReleaseTest");
		
		// acquire new
		MapEntry me = mapEntryPool.acquire(1024);
		assertTrue(me.getIndex() == 0L); // new
		me.putValueLength(1);
		assertTrue(me.isReleased() == false);
		assertTrue(me.isInUse() == true);
		assertTrue(me.isAllocated() == true);
		assertTrue(me.getSlotSize() == 1024);
		assertTrue(mapEntryPool.getFreeEntryCount() == 0);
		assertTrue(mapEntryPool.getTotalEntryCount() == 1);
		assertTrue(mapEntryPool.getTotalUsedSlotSize() == 1024);
		assertTrue(mapEntryPool.getTotalWastedSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalFreeSlotSize() == 0);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().size() == 0);
		
		// acquire new
		MapEntry me2 = mapEntryPool.acquire(4096);
		assertTrue(me2.getIndex() == 1L); // new
		assertTrue(me2.isReleased() == false);
		assertTrue(me2.isInUse() == true);
		assertTrue(me2.isAllocated() == true);
		assertTrue(me2.getSlotSize() == 4096);
		assertTrue(mapEntryPool.getFreeEntryCount() == 0);
		assertTrue(mapEntryPool.getTotalEntryCount() == 2);
		assertTrue(mapEntryPool.getTotalUsedSlotSize() == 5120);
		assertTrue(mapEntryPool.getTotalWastedSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalFreeSlotSize() == 0);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().size() == 0);
		
		mapEntryPool.release(me2);
		assertTrue(me2.isReleased() == true);
		assertTrue(me2.isInUse() == false);
		
		assertTrue(mapEntryPool.getFreeEntryCount() == 1);
		assertTrue(mapEntryPool.getTotalEntryCount() == 2);
		assertTrue(mapEntryPool.getTotalUsedSlotSize() == 1024);
		assertTrue(mapEntryPool.getTotalRealUsedSlotSize() == 1024);
		assertTrue(mapEntryPool.getTotalWastedSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalFreeSlotSize() == 4096);
		assertTrue(mapEntryPool.getFreeEntryCountByIndex((4096 - 1) / 16) == 1);
		assertTrue(mapEntryPool.getTotalFreeSlotSizeByIndex((4096 - 1) / 16) == 4096);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().size() == 1);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().contains(255)); //(length - 1) / 16
		
		// acquire new
		MapEntry me3 = mapEntryPool.acquire(4100);
		assertTrue(me3.getIndex() == 2L); // new
		assertTrue(me3.isReleased() == false);
		assertTrue(me3.isAllocated() == true);
		assertTrue(me3.isInUse() == true);
		assertTrue(me3.getSlotSize() == 4100);
		
		assertTrue(mapEntryPool.getFreeEntryCount() == 1);
		assertTrue(mapEntryPool.getTotalEntryCount() == 3);
		assertTrue(mapEntryPool.getTotalUsedSlotSize() == 5124);
		assertTrue(mapEntryPool.getTotalRealUsedSlotSize() == 5124);
		assertTrue(mapEntryPool.getTotalWastedSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalFreeSlotSize() == 4096);
		assertTrue(mapEntryPool.getFreeEntryCountByIndex((4096 - 1) / 16) == 1);
		assertTrue(mapEntryPool.getTotalFreeSlotSizeByIndex((4096 - 1) / 16) == 4096);
		
		mapEntryPool.release(me3);
		assertTrue(me3.isReleased() == true);
		assertTrue(me3.isInUse() == false);
		
		assertTrue(mapEntryPool.getFreeEntryCount() == 2);
		assertTrue(mapEntryPool.getTotalEntryCount() == 3);
		assertTrue(mapEntryPool.getTotalUsedSlotSize() == 1024);
		assertTrue(mapEntryPool.getTotalRealUsedSlotSize() == 1024);
		assertTrue(mapEntryPool.getTotalWastedSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalFreeSlotSize() == 8196);
		assertTrue(mapEntryPool.getFreeEntryCountByIndex((4096 - 1) / 16) == 1);
		assertTrue(mapEntryPool.getFreeEntryCountByIndex((4100 - 1) / 16) == 1);
		assertTrue(mapEntryPool.getTotalFreeSlotSizeByIndex((4096 - 1) / 16) == 4096);
		assertTrue(mapEntryPool.getTotalFreeSlotSizeByIndex((4100 - 1) / 16) == 4100);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().size() == 2);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().contains(255));
		assertTrue(mapEntryPool.getFreeEntryIndexSet().contains(256));
		
		assertTrue(me.getIndex() == 0L);
		assertTrue(me.isReleased() == false);
		assertTrue(me.isAllocated() == true);
		assertTrue(me.isInUse() == true);
		assertTrue(me.getValueLength() == 1);
		assertTrue(me.getSlotSize() == 1024);
		
		mapEntryPool.release(me);
		assertTrue(me.isReleased() == true);
		assertTrue(me.isInUse() == false);
		
		assertTrue(mapEntryPool.getFreeEntryCount() == 3);
		assertTrue(mapEntryPool.getTotalEntryCount() == 3);
		assertTrue(mapEntryPool.getTotalUsedSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalRealUsedSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalWastedSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalFreeSlotSize() == 9220);
		assertTrue(mapEntryPool.getFreeEntryCountByIndex((4096 - 1) / 16) == 1);
		assertTrue(mapEntryPool.getFreeEntryCountByIndex((4100 - 1) / 16) == 1);
		assertTrue(mapEntryPool.getFreeEntryCountByIndex((1024 - 1) / 16) == 1);
		assertTrue(mapEntryPool.getTotalFreeSlotSizeByIndex((4096 - 1) / 16) == 4096);
		assertTrue(mapEntryPool.getTotalFreeSlotSizeByIndex((4100 - 1) / 16) == 4100);
		assertTrue(mapEntryPool.getTotalFreeSlotSizeByIndex((1024 - 1) / 16) == 1024);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().size() == 3);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().contains(255)); // (4096 - 1) / 16
		assertTrue(mapEntryPool.getFreeEntryIndexSet().contains(256));
		assertTrue(mapEntryPool.getFreeEntryIndexSet().contains(63));
		
		// acquire new
		me = mapEntryPool.acquire(8192);
		assertTrue(me.getIndex() == 3L); // new
		assertTrue(me.isReleased() == false);
		assertTrue(me.isAllocated() == true);
		assertTrue(me.isInUse() == true);
		assertTrue(me.getSlotSize() == 8192);
		
		assertTrue(mapEntryPool.getFreeEntryCount() == 3);
		assertTrue(mapEntryPool.getTotalEntryCount() == 4);
		assertTrue(mapEntryPool.getTotalUsedSlotSize() == 8192);
		assertTrue(mapEntryPool.getTotalRealUsedSlotSize() == 8192);
		assertTrue(mapEntryPool.getTotalWastedSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalFreeSlotSize() == 9220);
		assertTrue(mapEntryPool.getFreeEntryCountByIndex((4096 - 1) / 16) == 1);
		assertTrue(mapEntryPool.getFreeEntryCountByIndex((4100 - 1) / 16) == 1);
		assertTrue(mapEntryPool.getFreeEntryCountByIndex((1024 - 1) / 16) == 1);
		assertTrue(mapEntryPool.getTotalFreeSlotSizeByIndex((4096 - 1) / 16) == 4096);
		assertTrue(mapEntryPool.getTotalFreeSlotSizeByIndex((4100 - 1) / 16) == 4100);
		assertTrue(mapEntryPool.getTotalFreeSlotSizeByIndex((1024 - 1) / 16) == 1024);
		
		mapEntryPool.release(me);
		assertTrue(me.isReleased() == true);
		assertTrue(me.isAllocated() == true);
		assertTrue(me.isInUse() == false);
		
		// reused exact match
		me = mapEntryPool.acquire(1024);
		assertTrue(me.getIndex() == 0L);  //reused
		assertTrue(me.isReleased() == false);
		assertTrue(me.isInUse() == true);
		assertTrue(me.getSlotSize() == 1024);
		
		assertTrue(mapEntryPool.getFreeEntryCount() == 3);
		assertTrue(mapEntryPool.getTotalEntryCount() == 4);
		assertTrue(mapEntryPool.getTotalUsedSlotSize() == 1024);
		assertTrue(mapEntryPool.getTotalRealUsedSlotSize() == 1024);
		assertTrue(mapEntryPool.getTotalWastedSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalFreeSlotSize() == 16388);
		assertTrue(mapEntryPool.getFreeEntryCountByIndex((4096 - 1) / 16) == 1);
		assertTrue(mapEntryPool.getFreeEntryCountByIndex((4100 - 1) / 16) == 1);
		assertTrue(mapEntryPool.getFreeEntryCountByIndex((1024 - 1) / 16) == 0);
		assertTrue(mapEntryPool.getFreeEntryCountByIndex((8192 - 1) / 16) == 1);
		assertTrue(mapEntryPool.getTotalFreeSlotSizeByIndex((4096 - 1) / 16) == 4096);
		assertTrue(mapEntryPool.getTotalFreeSlotSizeByIndex((4100 - 1) / 16) == 4100);
		assertTrue(mapEntryPool.getTotalFreeSlotSizeByIndex((1024 - 1) / 16) == 0);
		assertTrue(mapEntryPool.getTotalFreeSlotSizeByIndex((8192 - 1) / 16) == 8192);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().size() == 3);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().contains(255));
		assertTrue(mapEntryPool.getFreeEntryIndexSet().contains(256));
		assertTrue(mapEntryPool.getFreeEntryIndexSet().contains(511));
		
		// cant' reuse within 1025 to 2048
		me = mapEntryPool.acquire(1025);
		assertTrue(me.getIndex() == 4L); // new
		assertTrue(me.isReleased() == false);
		assertTrue(me.isInUse() == true);
		assertTrue(me.getSlotSize() == 1025);
		
		assertTrue(mapEntryPool.getFreeEntryCount() == 3);
		assertTrue(mapEntryPool.getTotalEntryCount() == 5);
		assertTrue(mapEntryPool.getTotalUsedSlotSize() == 2049);
		assertTrue(mapEntryPool.getTotalRealUsedSlotSize() == 2049);
		assertTrue(mapEntryPool.getTotalWastedSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalFreeSlotSize() == 16388);
		assertTrue(mapEntryPool.getFreeEntryCountByIndex((4096 - 1) / 16) == 1);
		assertTrue(mapEntryPool.getFreeEntryCountByIndex((4100 - 1) / 16) == 1);
		assertTrue(mapEntryPool.getFreeEntryCountByIndex((1024 - 1) / 16) == 0);
		assertTrue(mapEntryPool.getFreeEntryCountByIndex((8192 - 1) / 16) == 1);
		assertTrue(mapEntryPool.getTotalFreeSlotSizeByIndex((4096 - 1) / 16) == 4096);
		assertTrue(mapEntryPool.getTotalFreeSlotSizeByIndex((4100 - 1) / 16) == 4100);
		assertTrue(mapEntryPool.getTotalFreeSlotSizeByIndex((1024 - 1) / 16) == 0);
		assertTrue(mapEntryPool.getTotalFreeSlotSizeByIndex((8192 - 1) / 16) == 8192);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().size() == 3);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().contains(255));
		assertTrue(mapEntryPool.getFreeEntryIndexSet().contains(256));
		assertTrue(mapEntryPool.getFreeEntryIndexSet().contains(511));
		
		mapEntryPool.release(me);
		assertTrue(me.isReleased() == true);
		assertTrue(me.isAllocated() == true);
		assertTrue(me.isInUse() == false);
		
		// reuse within 1025 to 2048
		me = mapEntryPool.acquire(1024);
		assertTrue(me.getIndex() == 4L); // reused
		assertTrue(me.isReleased() == false);
		assertTrue(me.isInUse() == true);
		assertTrue(me.getSlotSize() == 1025);
		
		assertTrue(mapEntryPool.getFreeEntryCount() == 3);
		assertTrue(mapEntryPool.getTotalEntryCount() == 5);
		assertTrue(mapEntryPool.getTotalUsedSlotSize() == 2049);
		assertTrue(mapEntryPool.getTotalRealUsedSlotSize() == 2048);
		assertTrue(mapEntryPool.getTotalWastedSlotSize() == 1);
		
		assertTrue(mapEntryPool.getTotalFreeSlotSize() == 16388);
		assertTrue(mapEntryPool.getFreeEntryCountByIndex((4096 - 1) / 16) == 1);
		assertTrue(mapEntryPool.getFreeEntryCountByIndex((4100 - 1) / 16) == 1);
		assertTrue(mapEntryPool.getFreeEntryCountByIndex((1024 - 1) / 16) == 0);
		assertTrue(mapEntryPool.getFreeEntryCountByIndex((8192 - 1) / 16) == 1);
		assertTrue(mapEntryPool.getFreeEntryCountByIndex(1025) == 0);
		assertTrue(mapEntryPool.getTotalFreeSlotSizeByIndex((4096 - 1) / 16) == 4096);
		assertTrue(mapEntryPool.getTotalFreeSlotSizeByIndex((4100 - 1) / 16) == 4100);
		assertTrue(mapEntryPool.getTotalFreeSlotSizeByIndex((1024 - 1) / 16) == 0);
		assertTrue(mapEntryPool.getTotalFreeSlotSizeByIndex((8192 - 1) / 16) == 8192);
		assertTrue(mapEntryPool.getTotalFreeSlotSizeByIndex((1025 - 1) / 16) == 0);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().size() == 3);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().contains(255));
		assertTrue(mapEntryPool.getFreeEntryIndexSet().contains(256));
		assertTrue(mapEntryPool.getFreeEntryIndexSet().contains(511));
	}
	
	@Test
	public void TestAcquireInvalidSize() throws IOException {
		mapEntryPool = new MapEntryFactoryImpl(testDir, "TestAcquireInvalidSize");
		
		try {
			mapEntryPool.acquire(MapEntryFactoryImpl.MAX_DATA_SLOT_LENGTH + 1);
			fail();
		} catch (Exception ex) {
			// ignore
		}
		
		try {
			mapEntryPool.acquire(0);
			fail();
		} catch (Exception ex) {
			// ignore
		}
		
		// ok
		mapEntryPool.acquire(MapEntryFactoryImpl.MAX_DATA_SLOT_LENGTH);
		mapEntryPool.acquire(1);
	}
	
	@Test
	public void TestAcquireAndReleaseRandomSize() throws IOException {
		mapEntryPool = new MapEntryFactoryImpl(testDir, "TestAcquireAndReleaseRandomSize");
		
		int count = 1000;
		int maxSize = 1024 * 1024; // 1M
		Random random = new Random();
		Set<Integer> randomSet = new HashSet<Integer>();
		Set<Integer> randomIndexSet = new HashSet<Integer>();
		
		MapEntry[] entries = new MapEntry[count];
		for(int i = 0; i < count; i++) {
			int size = random.nextInt(maxSize) + 1;
			randomSet.add(size);
			randomIndexSet.add((size - 1) >> 4); /* (length - 1) / 16 */
			entries[i] = mapEntryPool.acquire(size);
		}
		
		assertTrue(randomSet.size() <= count);
		assertTrue(mapEntryPool.getFreeEntryCount() == 0);
		assertTrue(mapEntryPool.getTotalEntryCount() == count);
		assertTrue(mapEntryPool.getTotalUsedSlotSize() <= maxSize * (long)count);
		assertTrue(mapEntryPool.getTotalFreeSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalWastedSlotSize() == 0);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().size() == 0);
		
		for(int i = 0; i < count; i++) {
			mapEntryPool.release(entries[i]);
		}
		
		assertTrue(randomSet.size() <= count);
		assertTrue(mapEntryPool.getFreeEntryCount() == count);
		assertTrue(mapEntryPool.getTotalEntryCount() == count);
		assertTrue(mapEntryPool.getTotalUsedSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalFreeSlotSize() <= maxSize * (long)count);
		assertTrue(mapEntryPool.getTotalWastedSlotSize() == 0);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().size() == randomIndexSet.size() );
		for(int size : randomSet) {
			mapEntryPool.getFreeEntryIndexSet().contains(size);
		}
	}
	
	@Test
	public void TestAcquireAndReleaseBigSize() throws IOException {
		mapEntryPool = new MapEntryFactoryImpl(testDir, "TestAcquireAndReleaseBigSize");
		
		int count = 1000 * 1000;
		
		MapEntry[] entries = new MapEntry[count];
		for(int i = 0; i < count; i++) {
			entries[i] = mapEntryPool.acquire(MapEntryFactoryImpl.MAX_DATA_SLOT_LENGTH);
		}
		
		assertTrue(mapEntryPool.getFreeEntryCount() == 0);
		assertTrue(mapEntryPool.getTotalEntryCount() == count);
		assertTrue(mapEntryPool.getTotalUsedSlotSize() == MapEntryFactoryImpl.MAX_DATA_SLOT_LENGTH * (long)count);
		assertTrue(mapEntryPool.getTotalRealUsedSlotSize() == MapEntryFactoryImpl.MAX_DATA_SLOT_LENGTH * (long)count);
		assertTrue(mapEntryPool.getTotalFreeSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalWastedSlotSize() == 0);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().size() == 0);
		
		for(int i = 0; i < count; i++) {
			mapEntryPool.release(entries[i]);
		}
		
		assertTrue(mapEntryPool.getFreeEntryCount() == count);
		assertTrue(mapEntryPool.getTotalEntryCount() == count);
		assertTrue(mapEntryPool.getTotalUsedSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalRealUsedSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalFreeSlotSize() == MapEntryFactoryImpl.MAX_DATA_SLOT_LENGTH * (long)count);
		assertTrue(mapEntryPool.getTotalWastedSlotSize() == 0);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().size() == 1);
	}
	
	@Test
	public void TestAcquiredAndReleaseDifferentSize() throws IOException {
		mapEntryPool = new MapEntryFactoryImpl(testDir, "TestAcquiredAndReleaseDifferentSize");
		
		MapEntry[] entries = new MapEntry[10];
		for(int i = 0; i < 10; i++) {
			entries[i] = mapEntryPool.acquire(1024 * (i + 1));
		}
		
		assertTrue(mapEntryPool.getFreeEntryCount() == 0);
		assertTrue(mapEntryPool.getTotalEntryCount() == 10);
		assertTrue(mapEntryPool.getTotalUsedSlotSize() == 1024 * 55);
		assertTrue(mapEntryPool.getTotalRealUsedSlotSize() == 1024 * 55);
		assertTrue(mapEntryPool.getTotalFreeSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalWastedSlotSize() == 0);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().size() == 0);
		
		for(int i = 0; i < 10; i++) {
			mapEntryPool.release(entries[i]);
		}
		
		assertTrue(mapEntryPool.getFreeEntryCount() == 10);
		assertTrue(mapEntryPool.getTotalEntryCount() == 10);
		assertTrue(mapEntryPool.getTotalUsedSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalRealUsedSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalFreeSlotSize() == 1024 * 55);
		assertTrue(mapEntryPool.getTotalWastedSlotSize() == 0);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().size() == 10);
		for(int i = 0; i < 10; i++) {
			assertTrue(mapEntryPool.getFreeEntryIndexSet().contains((1024 * (i + 1) - 1) >> 4)); // (length - 1) / 16
		}
	}
	
	
	@Test
	public void TestAcquireAndReleaseSameSize() throws IOException {
		mapEntryPool = new MapEntryFactoryImpl(testDir, "TestAcquireAndReleaseSameSize");
		
		MapEntry[] entries = new MapEntry[10];
		for(int i = 0; i < 10; i++) {
			entries[i] = mapEntryPool.acquire(1024);
		}
		
		assertTrue(mapEntryPool.getFreeEntryCount() == 0);
		assertTrue(mapEntryPool.getTotalEntryCount() == 10);
		assertTrue(mapEntryPool.getTotalUsedSlotSize() == 1024 * 10);
		assertTrue(mapEntryPool.getTotalRealUsedSlotSize() == 1024 * 10);
		assertTrue(mapEntryPool.getTotalFreeSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalWastedSlotSize() == 0);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().size() == 0);
		
		for(int i = 0; i < 5; i++) {
			mapEntryPool.release(entries[i]);
		}
		
		assertTrue(mapEntryPool.getFreeEntryCount() == 5);
		assertTrue(mapEntryPool.getTotalEntryCount() == 10);
		assertTrue(mapEntryPool.getTotalUsedSlotSize() == 1024 * 5);
		assertTrue(mapEntryPool.getTotalRealUsedSlotSize() == 1024 * 5);
		assertTrue(mapEntryPool.getTotalFreeSlotSize() == 1024 * 5);
		assertTrue(mapEntryPool.getTotalWastedSlotSize() == 0);
		assertTrue(mapEntryPool.getFreeEntryCountByIndex((1024 - 1) / 16) == 5);
		assertTrue(mapEntryPool.getTotalFreeSlotSizeByIndex((1024 - 1) / 16) == 1024 * 5);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().size() == 1);
		
		for(int i = 5; i < 10; i++) {
			mapEntryPool.release(entries[i]);
		}
		
		assertTrue(mapEntryPool.getFreeEntryCount() == 10);
		assertTrue(mapEntryPool.getTotalEntryCount() == 10);
		assertTrue(mapEntryPool.getTotalUsedSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalRealUsedSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalFreeSlotSize() == 1024 * 10);
		assertTrue(mapEntryPool.getTotalWastedSlotSize() == 0);
		assertTrue(mapEntryPool.getFreeEntryCountByIndex((1024 - 1) / 16) == 10);
		assertTrue(mapEntryPool.getTotalFreeSlotSizeByIndex((1024 - 1) / 16) == 1024 * 10);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().size() == 1);
		
		entries = new MapEntry[10];
		for(int i = 0; i < 10; i++) {
			entries[i] = mapEntryPool.acquire(513);
		}
		
		assertTrue(mapEntryPool.getFreeEntryCount() == 0);
		assertTrue(mapEntryPool.getTotalEntryCount() == 10);
		assertTrue(mapEntryPool.getTotalUsedSlotSize() == 1024 * 10);
		assertTrue(mapEntryPool.getTotalRealUsedSlotSize() == 513 * 10);
		assertTrue(mapEntryPool.getTotalFreeSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalWastedSlotSize() == 511 * 10);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().size() == 0);
		
		for(int i = 0; i < 10; i++) {
			mapEntryPool.release(entries[i]);
		}
		
		assertTrue(mapEntryPool.getFreeEntryCount() == 10);
		assertTrue(mapEntryPool.getTotalEntryCount() == 10);
		assertTrue(mapEntryPool.getTotalUsedSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalRealUsedSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalFreeSlotSize() == 1024 * 10);
		assertTrue(mapEntryPool.getTotalWastedSlotSize() == 0);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().size() == 1);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().contains(63)); // (length - 1) / 16
		
		entries = new MapEntry[10];
		for(int i = 0; i < 10; i++) {
			entries[i] = mapEntryPool.acquire(128);
		}
		
		assertTrue(mapEntryPool.getFreeEntryCount() == 10);
		assertTrue(mapEntryPool.getTotalEntryCount() == 20);
		assertTrue(mapEntryPool.getTotalUsedSlotSize() == 128 * 10);
		assertTrue(mapEntryPool.getTotalRealUsedSlotSize() == 128 * 10);
		assertTrue(mapEntryPool.getTotalFreeSlotSize() == 1024 * 10);
		assertTrue(mapEntryPool.getTotalWastedSlotSize() == 0);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().size() == 1);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().contains(63));
		
		for(int i = 0; i < 10; i++) {
			mapEntryPool.release(entries[i]);
		}
		
		assertTrue(mapEntryPool.getFreeEntryCount() == 20);
		assertTrue(mapEntryPool.getTotalEntryCount() == 20);
		assertTrue(mapEntryPool.getTotalUsedSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalRealUsedSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalFreeSlotSize() == (1024 + 128) * 10);
		assertTrue(mapEntryPool.getTotalWastedSlotSize() == 0);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().size() == 2);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().contains(63));
		assertTrue(mapEntryPool.getFreeEntryIndexSet().contains(7));
	}
	
	@Test
	public void TestMultiThreading() throws IOException {
		mapEntryPool = new MapEntryFactoryImpl(testDir, "TestMultiThreading");

		// init threads and start
		int threadNum = 256;
		int maxSize = 1024 * 1024 * 4;
		int outLoop = 100;
		int inLoop = 10;
		CountDownLatch latch = new CountDownLatch(threadNum);
		Worker[] workers = new Worker[threadNum];
		for(int i = 0; i < threadNum; i++) {
			workers[i] = new Worker(mapEntryPool, outLoop, inLoop, maxSize, latch);
			workers[i].start();
		}
		
		// wait to finish
		for(int i = 0; i < threadNum; i++) {
			try {
				workers[i].join();
			} catch (InterruptedException e) {
				// ignore silently
			}
		}
		
		assertTrue(mapEntryPool.getFreeEntryIndexSet().headSet(maxSize >> 4).size() > 0);
		assertTrue(mapEntryPool.getFreeEntryIndexSet().tailSet(maxSize >> 4).size() == 0);
		assertTrue(mapEntryPool.getTotalUsedSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalRealUsedSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalWastedSlotSize() == 0);
		assertTrue(mapEntryPool.getTotalFreeSlotSize() > 0);
		assertTrue(mapEntryPool.getTotalAcquireCounter() == threadNum * outLoop * inLoop);
		assertTrue(mapEntryPool.getTotalReleaseCounter() == threadNum * outLoop * inLoop);
		assertTrue(mapEntryPool.getTotalAcquireNewCounter() == mapEntryPool.getFreeEntryCount());
		System.out.println("mapEntryPool.getFreeEntryIndexSet().size() = " + mapEntryPool.getFreeEntryIndexSet().size());
		System.out.println("mapEntryPool.getTotalExatchMatchReuseCounter() = " + mapEntryPool.getTotalExatchMatchReuseCounter());
		System.out.println("mapEntryPool.getTotalApproximateMatchReuseCounter() = " + mapEntryPool.getTotalApproximateMatchReuseCounter());
		System.out.println("mapEntryPool.getTotalAcquireNewCounter() = " + mapEntryPool.getTotalAcquireNewCounter());
		for(int i : mapEntryPool.getFreeEntryIndexSet()) {
			assertTrue(mapEntryPool.getFreeEntryCountByIndex(i) > 0);
			assertTrue(mapEntryPool.getTotalFreeSlotSizeByIndex(i) > 0);
		}
	}
	
	
	private static class Worker extends Thread {
		private IMapEntryFactory mapEntryFactory;
		private CountDownLatch latch;
		private Random random = new Random();
		private int outerIteration;
		private int innerIteration;
		private int maxSize;
		
		public Worker(IMapEntryFactory mapEntryFactory, int outerIteration, int innerIteration, 
				int maxSize, CountDownLatch latch) {
			this.mapEntryFactory = mapEntryFactory;
			this.latch = latch;
			this.outerIteration = outerIteration;
			this.innerIteration = innerIteration;
			this.maxSize = maxSize;
		}
		
		public void run() {
			latch.countDown();
			try {
				latch.await();
			} catch (InterruptedException e1) {
				// ignore silently
			}
			for(int i = 0; i < outerIteration; i++) {
				try {
					for(int j = 0; j < innerIteration; j++) {
						int size = random.nextInt(maxSize) + 1;
						MapEntry me = this.mapEntryFactory.acquire(size);
						Thread.sleep(random.nextInt(20) + 5);
						this.mapEntryFactory.release(me);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@After
	public void clear() throws IOException {
		if (this.mapEntryPool != null) {
			this.mapEntryPool.removeAll();
			this.mapEntryPool.close();
		}
		FileUtil.deleteDirectory(new File(testDir));
	}

}
