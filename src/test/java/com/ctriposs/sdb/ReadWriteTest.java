package com.ctriposs.sdb;

import java.io.IOException;

import com.ctriposs.sdb.DBConfig;
import com.ctriposs.sdb.SDB;
import com.ctriposs.sdb.utils.TestUtil;

public class ReadWriteTest {
	
	private static String testDir = TestUtil.TEST_BASE_DIR + "sdb/unit/read_write_test";
	private static long TOTAL = 10000000;
	
	private static void write(SDB db) {
		long sum = 0;
		long count = 0;
		long startTime = System.currentTimeMillis();
		
		for(long i = 0; i < TOTAL; i++) {
			byte[] keyBytes = ("" + i).getBytes();
			long oneStartTime = System.nanoTime();  
			db.put(keyBytes, keyBytes);
			
			if (i % 100000 == 0) {
				sum += (System.nanoTime() - oneStartTime);
				count++;
			}
		}
		
        System.out.println("avg:" + sum / count + " ns");  
        System.out.println("write " + TOTAL / 1000000 + " million times:" + (System.currentTimeMillis() - startTime) + " ms");
	}
	
	private static void read(SDB db) {
		long startTime = System.currentTimeMillis(); 
		for(long i = 0; i < TOTAL; i++) {
			byte[] keyBytes = ("" + i).getBytes();
			db.get(keyBytes);
		}
		System.out.println("read " + TOTAL / 1000000 + " million times:" + (System.currentTimeMillis() - startTime) + " ms"); 
	}
	
	public static void main(String args[]) throws IOException {
		SDB db = new SDB(testDir, DBConfig.HUGE);
		
		write(db);
		
		read(db);
		
		db.close();
		db.destory();
	}
}
