package com.ctriposs.bigmap;

import java.io.File;
import java.io.IOException;

import com.ctriposs.bigmap.utils.FileUtil;

public class LimitTest {

	private static String testDir = TestUtil.TEST_BASE_DIR + "bigmap/unit/perf_test";
	
	private static BigConcurrentHashMapImpl map;
	
	public static void main(String args[]) throws IOException {
		try {
			limitTest();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			clear();
		}
	}
	
	public static void limitTest() throws IOException, InterruptedException {
		map = new BigConcurrentHashMapImpl(testDir, "limitTest");
		
		String rndString = TestUtil.randomString(10);
		
		for(long counter = 0;; counter++) {
			map.put(String.valueOf(counter).getBytes(), rndString.getBytes());
			if (counter%1000000 == 0)  {
				System.out.println(""+counter);
				System.out.println(TestUtil.printMemoryFootprint());
			}
		}
	}
	
	public static void clear() throws IOException {
		if (map != null) {
			map.close();
		}
		FileUtil.deleteDirectory(new File(testDir));
	}

}
