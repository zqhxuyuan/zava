package com.github.believe3301.nonheapdb.test;

import com.github.believe3301.nonheapdb.DBCache;
import com.github.believe3301.nonheapdb.Util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CacheConcurrentTest extends BasedTest {

	private Map<String, byte[]> maps;

	public CacheConcurrentTest(final String name) {
		super(name);
		maps = new HashMap<String, byte[]>();
	}

	public void testConcurrent() throws InterruptedException,
			ExecutionException {
		final DBCache cache = new DBCache(16, Util.Mb(8), -1);
		final BasedTest cs = this;
		Callable<Integer> task = new Callable<Integer>() {
			public Integer call() {
				Random ran = new Random();
				ran.setSeed(System.currentTimeMillis() ^ System.nanoTime());
				for (int i = 0; i < 100000; i++) {
					int k = ran.nextInt(100000000);
					String key = cs.generateKey(k);
					synchronized (maps) {
						//get
						if (maps.containsKey(key)) {
							assertEqualContent(maps.get(key), cache.get(key));
							//remove
							if(k % 5 == 0){
								cache.remove(key);
								maps.remove(key);
								assertNull(cache.get(key));
							}
							continue;
						}

						int vsize = 0;
						//put
						if(k % 3 == 0) {
							vsize = ran.nextInt(256) + 512;
						} else {
							vsize = ran.nextInt(256) + 1;
						}
						byte[] value = cs.generateTestData(vsize);
						assertTrue(cache.put(key, value));
						maps.put(key, value);
						
						//remove
						if(k % 5 == 0){
							cache.remove(key);
							maps.remove(key);
							assertNull(cache.get(key));
						}
					}
				}
				return 0;
			}
		};

		List<Callable<Integer>> tasks = Collections.nCopies(10, task);
		ExecutorService executor = Executors.newFixedThreadPool(10);
		List<Future<Integer>> futures = executor.invokeAll(tasks);
		assertEquals(futures.size(), 10);
		for (Future<Integer> future : futures) {
			future.get();
		}

		// check data
		for (String key : maps.keySet()) {
			byte[] value = cache.get(key);
			assertNotNull(value);
			assertEqualContent(maps.get(key), value);
		}

		executor.shutdown();
		
		System.out.println(cache.info());
	}
}
