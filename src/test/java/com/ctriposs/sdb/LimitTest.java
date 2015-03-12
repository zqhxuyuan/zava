package com.ctriposs.sdb;

import com.ctriposs.sdb.DBConfig;
import com.ctriposs.sdb.SDB;
import com.ctriposs.sdb.utils.DateFormatter;
import com.ctriposs.sdb.utils.TestUtil;

public class LimitTest {
	
	private static String testDir = TestUtil.TEST_BASE_DIR + "sdb/unit/limit_test";
	
	public static void main(String args[]) {
		SDB db = new SDB(testDir, DBConfig.SMALL);
		
		String rndString = TestUtil.randomString(10);
		
		System.out.println("Start from date " + DateFormatter.formatCurrentDate());
		long start = System.currentTimeMillis();
		for(long counter = 0;; counter++) {
			db.put(String.valueOf(counter).getBytes(), rndString.getBytes());
			if (counter%1000000 == 0) {
				System.out.println("Current date " + DateFormatter.formatCurrentDate());
				System.out.println(""+counter);
				System.out.println(TestUtil.printMemoryFootprint());
				long end = System.currentTimeMillis();
				System.out.println("timeSpent = " + (end - start));
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// ignore
				}
				start = System.currentTimeMillis();
			}
			
		}
	}

}
