package com.ctriposs.bigmap;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Test;

import com.ctriposs.bigmap.utils.FileUtil;

public class ReloadTest {

	private static String testDir = TestUtil.TEST_BASE_DIR + "bigmap/unit/reload_test";
	
	private BigConcurrentHashMapImpl map;
	
	@Test
	public void TestReload() throws IOException {
		map = new BigConcurrentHashMapImpl(testDir, "TestReload");
		
		String randomString = TestUtil.randomString(1024);
		
		int loop = 1024 * 1024;
		
		for(int i = 0; i < loop; i ++) {
			map.put(String.valueOf(i).getBytes(),(i + randomString).getBytes());
		}
		assertTrue(map.size() == loop);
		
		long fileUsed = map.getMemoryMappedFileStats().getBackFileUsed();
		
		for(int i = 0; i < loop; i += 2 ) {
			map.remove(String.valueOf(i).getBytes());
		}
		for(int i = 0; i < loop; i++) {
			if (i % 2 == 0) {
				assertFalse(map.containsKey(String.valueOf(i).getBytes()));
			} else {
				assertTrue(map.containsKey(String.valueOf(i).getBytes()));
			}
		}
		assertTrue(map.size() == (loop / 2));
		assertTrue(fileUsed == map.getMemoryMappedFileStats().getBackFileUsed());
		
		map.close();
		
		// reload on startup
		map = new BigConcurrentHashMapImpl(testDir, "TestReload", new BigConfig().setReloadOnStartup(true));
		
		assertTrue(map.size() == (loop / 2));
		assertTrue(fileUsed <= map.getMemoryMappedFileStats().getBackFileUsed()); // size may grow because of allocation check
		fileUsed = map.getMemoryMappedFileStats().getBackFileUsed();
		
		for(int i = 0; i < loop; i++) {
			if (i % 2 == 0) {
				assertFalse(map.containsKey(String.valueOf(i).getBytes()));
			} else {
				assertTrue(map.containsKey(String.valueOf(i).getBytes()));
			}
		}
		
		map.close();
		
		// remove all on startup
		map = new BigConcurrentHashMapImpl(testDir, "TestReload");
		
		assertTrue(map.isEmpty());
		
		for(int i = 0; i < loop; i ++) {
			map.put(String.valueOf(i).getBytes(),(i + randomString).getBytes());
		}
		assertTrue(map.size() == loop);
		
		for(int i = 0; i < loop; i += 2 ) {
			map.remove(String.valueOf(i).getBytes());
		}
		for(int i = 0; i < loop; i++) {
			if (i % 2 == 0) {
				assertFalse(map.containsKey(String.valueOf(i).getBytes()));
			} else {
				assertTrue(map.containsKey(String.valueOf(i).getBytes()));
			}
		}
		assertTrue(map.size() == (loop / 2));
		assertTrue(fileUsed >= map.getMemoryMappedFileStats().getBackFileUsed());
	}
	
	@After
	public void clear() throws IOException {
		if (map != null) {
			map.close();
		}
		FileUtil.deleteDirectory(new File(testDir));
	}

}
