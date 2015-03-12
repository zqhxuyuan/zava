package com.ctriposs.sdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Test;

import com.ctriposs.sdb.DBConfig;
import com.ctriposs.sdb.SDB;
import com.ctriposs.sdb.table.AbstractMapTable;
import com.ctriposs.sdb.table.HashMapTablePerfTest.SampleValue;
import com.ctriposs.sdb.utils.TestUtil;

import static com.ctriposs.sdb.table.HashMapTablePerfTest.users;

public class SDBPerfTestA {
	
	private static String testDir = TestUtil.TEST_BASE_DIR + "sdb/sdb_perf_testa";
	
	private SDB db;
	
	static final int N_THREADS = 128;
	
	@Test
	public void testPut() throws IOException, ClassNotFoundException {
		int count = 400000;
		
		db = new SDB(testDir);
		
		long start = System.nanoTime();
		
		final SampleValue value = new SampleValue();
		StringBuilder user = new StringBuilder();
		System.out.println(value.toBytes().length);
		for(int i = 0; i < count; i++) {
			value.ee = i;
			value.gg = i;
			value.ii = i;
			db.put(users(user, i).getBytes(), value.toBytes(), AbstractMapTable.NO_TIMEOUT);
		}
		for(int i = 0; i < count; i++) {
			byte[] result = db.get(users(user, i).getBytes());
			assertNotNull(result);
			SampleValue value2 = SampleValue.fromBytes(result);
			assertEquals(i, value2.ee);
			assertEquals(i, value2.gg, 0.0);
			assertEquals(i, value2.ii);
		}
		for(int i = 0; i < count; i++) {
			byte[] result = db.get(users(user, i).getBytes());
			assertNotNull(result);
		}
        for (int i = 0; i < count; i++) {
            db.delete(users(user, i).getBytes());
        }
        long time = System.nanoTime() - start;
        System.out.printf("Put/get %,d K operations per second%n",
                (int) (count * 4 * 1e6 / time));
	}
	
	@Test
	public void testMultThreadsPut() throws ExecutionException, InterruptedException, IOException {
		ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		
		System.out.println("Starting test");
		
		db = new SDB(testDir, DBConfig.LARGE);
		final int COUNT = 2000000;
		
		long start = System.nanoTime();
		List<Future<?>> futures = new ArrayList<Future<?>>();
		for(int t = 0; t < N_THREADS; t++) {
			final int finalT = t;
			futures.add(es.submit(new Runnable() {

				@Override
				public void run() {
					try {
						final SampleValue value = new SampleValue();
	                    StringBuilder user = new StringBuilder();
	                    for (int i = finalT; i < COUNT; i += N_THREADS) {
	                        value.ee = i;
	                        value.gg = i;
	                        value.ii = i;
	                        db.put(users(user, i).getBytes(), value.toBytes(), AbstractMapTable.NO_TIMEOUT);
	                    }
	                    
	                    for (int i = finalT; i < COUNT; i += N_THREADS) {
	            			byte[] result = db.get(users(user, i).getBytes());
	            			assertNotNull(result);
	            			SampleValue value2 = SampleValue.fromBytes(result);
	                        assertEquals(i, value2.ee);
	                        assertEquals(i, value2.gg, 0.0);
	                        assertEquals(i, value2.ii);
	                    }
	                    
	                    
	                    for (int i = finalT; i < COUNT; i += N_THREADS)
                            assertNotNull(db.get(users(user, i).getBytes()));
	                    
	                    for (int i = finalT; i < COUNT; i += N_THREADS)
	                        db.delete(users(user, i).getBytes());
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				
			}));
		}
		
		for (Future<?> future : futures) {
            future.get();
		}
		
        long time = System.nanoTime() - start;
        System.out.printf("Put/get %,d K operations per second%n",
                (int) (COUNT * 4 * 1e6 / time));
        es.shutdown();
		
	}
	
	@After
	public void clear() throws IOException {
		if (db != null) {
			db.close();
			db.destory();
		}
	}

}
