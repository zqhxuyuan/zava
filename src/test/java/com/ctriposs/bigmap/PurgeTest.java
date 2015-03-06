package com.ctriposs.bigmap;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Test;

import com.ctriposs.bigmap.utils.FileUtil;

public class PurgeTest {
	
	private static String testDir = TestUtil.TEST_BASE_DIR + "bigmap/unit/purge_test";
	
	private BigConcurrentHashMapImpl map;
	
	@Test
	public void scheduledPurgeTest() throws IOException, InterruptedException {
		BigConfig config = new BigConfig().setPurgeIntervalInMs(1000L);
		map = new BigConcurrentHashMapImpl(testDir, "scheduledPurgeTest", config); // 1 second expiration
		
        assertTrue(map.isEmpty());
        map.put("1".getBytes(), "A".getBytes(), 500);
        map.put("2".getBytes(), "B".getBytes(), 600);
        map.put("3".getBytes(), "C".getBytes(), 1200);
        map.put("4".getBytes(), "D".getBytes(), 2500);
        map.put("5".getBytes(), "E".getBytes(), 2500);
        map.put("6".getBytes(), "F".getBytes()); // no expiration
        assertFalse(map.isEmpty());
        assertEquals(6, map.size());
        
        Thread.sleep(2200); // 1, 2, 3 expired
        
        assertTrue(map.purgeCount.get() > 1);
        assertFalse(map.isEmpty());
        assertEquals(3, map.size()); // 4, 5, 6 left
        
        assertEquals("E", new String(map.get("5".getBytes()))); // 5 reset last access time
        
        Thread.sleep(2000); // 4 expired
        
        assertTrue(map.purgeCount.get() > 2);
        assertFalse(map.isEmpty());
        assertEquals(2, map.size()); // 5, 6 left
        
        Thread.sleep(2000); // 5 expired
        
        assertTrue(map.purgeCount.get() > 3);
        assertFalse(map.isEmpty());
        assertEquals(1, map.size()); // 6 left
        assertEquals("F", new String(map.get("6".getBytes())));
        
        map.remove("6".getBytes());
        assertTrue(map.isEmpty());
        assertEquals(0, map.size()); // 6 left
        
        for(int i = 0; i < 20000; i++) {
        	map.put(("key" + i).getBytes() , ("value" + i).getBytes(), 500);
        }
        
        Thread.sleep(2000);
        assertTrue(map.isEmpty());
        assertEquals(0, map.size()); // no left
	}
	
	@Test
	public void purgeTriggerByGetTest() throws IOException, InterruptedException {
		BigConfig config = new BigConfig().setPurgeIntervalInMs(3000L);
		map = new BigConcurrentHashMapImpl(testDir, "purgeTriggerByGetTest", config); // 3 second expiration
		
        assertTrue(map.isEmpty());
        map.put("1".getBytes(), "A".getBytes(), 500);
        map.put("2".getBytes(), "B".getBytes(), 600);
        map.put("3".getBytes(), "C".getBytes(), 1200);
        map.put("4".getBytes(), "D".getBytes(), 2500);
        map.put("5".getBytes(), "E".getBytes(), 2500);
        assertFalse(map.isEmpty());
        assertEquals(5, map.size());
        
        Thread.sleep(1000);
        assertNull(map.get("1".getBytes())); // purge trigger by get
        assertNull(map.get("2".getBytes())); // purge trigger by get
        assertTrue(map.purgeCount.get() == 0);
        
        Thread.sleep(1000);
        assertNull(map.get("3".getBytes())); // purge trigger by get
        assertTrue(map.purgeCount.get() == 0);
        
        Thread.sleep(1500); // scheduled purge
        assertTrue(map.purgeCount.get() == 1);
        assertTrue(map.isEmpty());
        assertEquals(0, map.size()); // no left
        
	}
	
	@Test
	public void purgeTriggerByRemoveTest() throws IOException, InterruptedException {
		BigConfig config = new BigConfig().setPurgeIntervalInMs(3000L);
		map = new BigConcurrentHashMapImpl(testDir, "purgeTriggerByRemoveTest", config); // 3 second expiration
		
        assertTrue(map.isEmpty());
        map.put("1".getBytes(), "A".getBytes(), 500);
        map.put("2".getBytes(), "B".getBytes(), 600);
        map.put("3".getBytes(), "C".getBytes(), 1200);
        map.put("4".getBytes(), "D".getBytes(), 2500);
        map.put("5".getBytes(), "E".getBytes(), 2500);
        assertFalse(map.isEmpty());
        assertEquals(5, map.size());
        
        Thread.sleep(1000);
        assertNull(map.remove("1".getBytes())); // purge trigger by remove
        assertNull(map.remove("2".getBytes())); // purge trigger by remove
        assertTrue(map.purgeCount.get() == 0);
        
        Thread.sleep(1000);
        assertNull(map.remove("3".getBytes())); // purge trigger by remove
        assertNotNull(map.remove("4".getBytes())); // purge trigger by remove
        assertTrue(map.purgeCount.get() == 0);
        
        Thread.sleep(1500); // scheduled purge
        assertTrue(map.purgeCount.get() == 1);
        assertTrue(map.isEmpty());
        assertEquals(0, map.size()); // no left
	}

	@After
	public void clear() throws IOException {
		if (map != null) {
			map.close();
		}
		FileUtil.deleteDirectory(new File(testDir));
	}
	
}
