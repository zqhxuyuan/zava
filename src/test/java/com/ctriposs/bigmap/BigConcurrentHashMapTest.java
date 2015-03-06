package com.ctriposs.bigmap;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Test;

import com.ctriposs.bigmap.utils.FileUtil;

public class BigConcurrentHashMapTest {
	
	private static String testDir = TestUtil.TEST_BASE_DIR + "bigmap/unit/big_concurrent_hashmap_test";
	
	private BigConcurrentHashMapImpl map;
	
    /**
     * Create a map from Strings 1-5 to Strings "A"-"E".
     * @throws IOException 
     */
    private BigConcurrentHashMapImpl map5() throws IOException {
    	BigConfig config = new BigConfig().setInitialCapacity(5);
    	BigConcurrentHashMapImpl map = new BigConcurrentHashMapImpl(testDir, "map5", config);
        assertTrue(map.isEmpty());
        map.put("1".getBytes(), "A".getBytes());
        map.put("2".getBytes(), "B".getBytes());
        map.put("3".getBytes(), "C".getBytes());
        map.put("4".getBytes(), "D".getBytes());
        map.put("5".getBytes(), "E".getBytes());
        assertFalse(map.isEmpty());
        assertEquals(5, map.size());
        return map;
    }

	@Test
	public void testClear() throws IOException {
		map = map5();
        map.clear();
        assertEquals(map.size(), 0);
	}
	
    /**
     *  containsKey returns true for contained key
     * @throws IOException 
     */
	@Test
    public void testContainsKey() throws IOException {
    	map = map5();
        assertTrue(map.containsKey("1".getBytes()));
        assertFalse(map.containsKey("0".getBytes()));
    }
	
    /**
     *  get returns the correct element at the given key,
     *  or null if not present
     * @throws IOException 
     */
	@Test
    public void testGet() throws IOException {
        map = map5();
        String v = new String(map.get("1".getBytes()));
        assertEquals("A", v);
        assertNull(map.get("-1".getBytes()));
    }
	
    /**
     *  isEmpty is true of empty map and false for non-empty
     * @throws IOException 
     */
	@Test
    public void testIsEmpty() throws IOException {
        map = new BigConcurrentHashMapImpl(testDir, "testIsEmpty");
        assertTrue(map.isEmpty());
    }
	
    /**
     *   putIfAbsent works when the given key is not present
     * @throws IOException 
     */
	@Test
    public void testPutIfAbsent() throws IOException {
		map = map5();
        map.putIfAbsent("6".getBytes(), "Z".getBytes());
        assertTrue(map.containsKey("6".getBytes()));
    }
	
    /**
     *   putIfAbsent does not add the pair if the key is already present
     */
	@Test
    public void testPutIfAbsent2() throws IOException {
        map = map5();
        assertEquals("A", new String(map.putIfAbsent("1".getBytes(), "Z".getBytes())));
    }
	
    /**
     *   replace fails when the given key is not present
     * @throws IOException 
     */
	@Test
    public void testReplace() throws IOException {
        map = map5();
        assertNull(map.replace("6".getBytes(), "Z".getBytes()));
        assertFalse(map.containsKey("6".getBytes()));
    }
    
    /**
     *   replace succeeds if the key is already present
     * @throws IOException 
     */
    @Test
    public void testReplace2() throws IOException {
        map = map5();
        assertNotNull(map.replace("1".getBytes(), "Z".getBytes()));
        assertEquals("Z", new String(map.get("1".getBytes())));
    }
    
    /**
     * replace value fails when the given key not mapped to expected value
     * @throws IOException 
     */
    @Test
    public void testReplaceValue() throws IOException {
        map = map5();
        assertEquals("A", new String(map.get("1".getBytes())));
        assertFalse(map.replace("1".getBytes(), "Z".getBytes(), "Z".getBytes()));
        assertEquals("A", new String(map.get("1".getBytes())));
    }
    
    /**
     * replace value succeeds when the given key mapped to expected value
     * @throws IOException 
     */
    @Test
    public void testReplaceValue2() throws IOException {
        map = map5();
        assertEquals("A", new String(map.get("1".getBytes())));
        assertTrue(map.replace("1".getBytes(), "A".getBytes(), "Z".getBytes()));
        assertEquals("Z", new String(map.get("1".getBytes())));
    }
    
    /**
     *   remove removes the correct key-value pair from the map
     * @throws IOException 
     */
    @Test
    public void testRemove() throws IOException {
        map = map5();
        map.remove("5".getBytes());
        assertEquals(4, map.size());
        assertFalse(map.containsKey("5".getBytes()));
    }
	
    /**
     * remove(key,value) removes only if pair present
     * @throws IOException 
     */
    @Test
    public void testRemove2() throws IOException {
        map = map5();
        map.remove("5".getBytes(), "E".getBytes());
        assertEquals(4, map.size());
        assertFalse(map.containsKey("5".getBytes()));
        map.remove("4".getBytes(), "A".getBytes());
        assertEquals(4, map.size());
        assertTrue(map.containsKey("4".getBytes()));
    }
    
    /**
     *   size returns the correct values
     * @throws IOException 
     */
    @Test
    public void testSize() throws IOException {
        map = map5();
        assertEquals(5, map.size());
        map.clear();
        map.close();
        
        map = new BigConcurrentHashMapImpl(testDir, "testSize");
        assertTrue(map.isEmpty());
    }
    
	@After
	public void clear() throws IOException {
		if (map != null) {
			map.close();
		}
		FileUtil.deleteDirectory(new File(testDir));
	}

	
    /**
     * Cannot create with negative capacity
     * @throws IOException 
     */
	@Test
    public void testConstructor1() throws IOException {
        try {
        	BigConfig config = new BigConfig().setInitialCapacity(-1).setLoadFactor(0).setConcurrencyLevel(1).setPurgeIntervalInMs(1000 * 60);
            map = new BigConcurrentHashMapImpl(testDir, "testConstructor1", config);
            shouldThrow();
        } catch(IllegalArgumentException e){}
    }
	
    /**
     * Cannot create with negative concurrency level
     */
	@Test
    public void testConstructor2() throws IOException {
        try {
        	BigConfig config = new BigConfig().setInitialCapacity(1).setLoadFactor(0).setConcurrencyLevel(-1).setPurgeIntervalInMs(1000 * 60);
            map = new BigConcurrentHashMapImpl(testDir, "testConstructor2", config);
            shouldThrow();
        } catch(IllegalArgumentException e){}
    }
	
	/**
     * Cannot create with only negative capacity
	 * @throws IOException 
     */
	@Test
    public void testConstructor3() throws IOException {
        try {
        	BigConfig config = new BigConfig().setInitialCapacity(-1);
            map = new BigConcurrentHashMapImpl(testDir, "testConstructor3", config);
            shouldThrow();
        } catch(IllegalArgumentException e){}
    }
    
    /**
     * containsValue(null) throws NPE
     * @throws IOException 
     */
	@Test
    public void testContainsValue_NullPointerException() throws IOException {
        try {
        	BigConfig config = new BigConfig().setInitialCapacity(5);
        	map = new BigConcurrentHashMapImpl(testDir, "testContainsValue_NullPointerException", config);;
            map.containsKey(null);
            shouldThrow();
        } catch(NullPointerException e){}
    }
    
    @Test
    public void testLoop() throws IOException {
        map = new BigConcurrentHashMapImpl(testDir, "testLoop");
        
        for(int i = 0; i < 100; i++) {
        	map.put(String.valueOf(i).getBytes(), ("item"+i).getBytes());
        }
        
        for(int i = 0; i < 100; i++) {
        	String key = String.valueOf(i);
        	String value = new String(map.get(key.getBytes()));
        	assertEquals("item" + i, value);
        }
    }
    
    /**
     * fail with message "should throw exception"
     */
    public void shouldThrow() {
        fail("Should throw exception");
    }
    
    public static class CachePutterRunnable implements Runnable {
    	private int start;
    	private int count;
    	private BigConcurrentHashMapImpl cache;
    	
    	public CachePutterRunnable(BigConcurrentHashMapImpl cache, int start, int count) {
    		this.cache = cache;
    		this.start = start;
    		this.count = count;
    	}

		@Override
		public void run() {
			for (int i = 0; i < count; i++) {
				cache.put(String.valueOf(start + i).getBytes(), ("A slightly longer string for testing " + (start + i)).getBytes());
			}
		}
    }
    
    @Test
    public void testConcurrency() throws IOException, InterruptedException {
    	map = new BigConcurrentHashMapImpl(testDir, "testConcurrency");
    	
    	final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(1000);
    	
    	insert(map, queue);
    	insert(map, queue);
    	
    	long startTime = System.currentTimeMillis();
    	int mismatch = 0;
    	for(int i = 0; i < 100000; i++) {
    		assertEquals("A slightly longer string for testing " + i, new String(map.get(String.valueOf(i).getBytes())));
			if (!("A slightly longer string for testing " + i).equals(new String(map.get(String.valueOf(i).getBytes())))){
				mismatch++;
			}
    	}
    	
		System.out.println("Mismatch :" + mismatch);
		long readCompleteTime = System.currentTimeMillis();
		
		System.out.println("Time to read: " + (readCompleteTime - startTime)
				+ " ms.");
    }
    
    private void insert(BigConcurrentHashMapImpl cache, ArrayBlockingQueue<Runnable> queue) throws InterruptedException {
    	ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 20, 10, TimeUnit.SECONDS, queue);
    	
    	threadPool.prestartAllCoreThreads();
    	long startTime = System.currentTimeMillis();
    	for(int i = 0; i < 20; i++) {
    		threadPool.execute(new CachePutterRunnable(cache, i * 5000, 5000));
    	}
    	threadPool.shutdown();
    	threadPool.awaitTermination(10, TimeUnit.MINUTES);
    	long putCompleteTime = System.currentTimeMillis();
    	
    	System.out.println("Time to insert: " + (putCompleteTime - startTime) + "ms.");
    }
}
