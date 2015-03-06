package com.ctriposs.bigcache.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Test;

import com.ctriposs.bigcache.BigCache;
import com.ctriposs.bigcache.CacheConfig;
import com.ctriposs.bigcache.ICache;
import com.ctriposs.bigcache.CacheConfig.StorageMode;

public class BigCachePerfTest {
	
	private static String testDir = "d:/sample/hello/bcache_perf_test";
	
	private ICache<String> cache;
	
	static final int N_THREADS = 128;
	
	@Test
	public void testPut() throws IOException, ClassNotFoundException {
		int count = 1000000;
		
    	CacheConfig config = new CacheConfig();
    	config.setStorageMode(StorageMode.OffHeapPlusFile); // use offheap memory + file mode
        cache = new BigCache<String>(testDir, config);
		
		long start = System.nanoTime();
		
		final SampleValue value = new SampleValue();
		StringBuilder user = new StringBuilder();
		for(int i = 0; i < count; i++) {
			value.ee = i;
			value.gg = i;
			value.ii = i;
			cache.put(users(user, i), value.toBytes());
		}
		for(int i = 0; i < count; i++) {
			byte[] result = cache.get(users(user, i));
			assertNotNull(result);
			SampleValue value2 = SampleValue.fromBytes(result);
			assertEquals(i, value2.ee);
			assertEquals(i, value2.gg, 0.0);
			assertEquals(i, value2.ii);
		}
		for(int i = 0; i < count; i++) {
			byte[] result = cache.get(users(user, i));
			assertNotNull(result);
		}
        for (int i = 0; i < count; i++) {
            cache.delete(users(user, i));
        }
        long time = System.nanoTime() - start;
        System.out.printf("Put/get %,d K operations per second%n",
                (int) (count * 4 * 1e6 / time));
        
		for(int i = 0; i < count; i++) {
			byte[] result = cache.get(users(user, i));
			assertNull(result);
		}
	}
	
	@Test
	public void testMultThreadsPut() throws ExecutionException, InterruptedException, IOException {
		ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		
		System.out.println("Starting test");
		
    	CacheConfig config = new CacheConfig();
    	config.setStorageMode(StorageMode.OffHeapPlusFile); // use offheap memory + file mode
        cache = new BigCache<String>(testDir, config);
		final int COUNT = 4000000;
		
		long start = System.nanoTime();
		List<Future<?>> futures = new ArrayList<Future<?>>();
		for(int t = 0; t < N_THREADS; t++) {
			final int finalT = t;
			futures.add(es.submit(new Runnable() {

				public void run() {
					try {
						final SampleValue value = new SampleValue();
	                    StringBuilder user = new StringBuilder();
	                    for (int i = finalT; i < COUNT; i += N_THREADS) {
	                        value.ee = i;
	                        value.gg = i;
	                        value.ii = i;
	                        cache.put(users(user, i), value.toBytes());
	                    }
	                    
	                    for (int i = finalT; i < COUNT; i += N_THREADS) {
	            			byte[] result = cache.get(users(user, i));
	            			assertNotNull(result);
	            			SampleValue value2 = SampleValue.fromBytes(result);
	                        assertEquals(i, value2.ee);
	                        assertEquals(i, value2.gg, 0.0);
	                        assertEquals(i, value2.ii);
	                    }
	                    
	                    
	                    for (int i = finalT; i < COUNT; i += N_THREADS)
                            assertNotNull(cache.get(users(user, i)));
	                    
	                    for (int i = finalT; i < COUNT; i += N_THREADS)
	                        cache.delete(users(user, i));
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
        
        StringBuilder user = new StringBuilder();
		for(int i = 0; i < COUNT; i++) {
			byte[] result = cache.get(users(user, i));
			assertNull(result);
		}
		
	}
	
	@After
	public void clear() throws IOException {
		if (cache != null) {
			cache.close();
		}
	}
	
	public static String users(StringBuilder user, int i) {
        user.setLength(0);
        user.append("user:");
        user.append(i);
        return user.toString();
    }

}
