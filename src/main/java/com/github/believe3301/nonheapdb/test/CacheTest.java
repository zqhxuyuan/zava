package com.github.believe3301.nonheapdb.test;

import com.github.believe3301.nonheapdb.DBCache;
import com.github.believe3301.nonheapdb.Util;

import java.util.HashMap;

public class CacheTest extends BasedTest {

	public CacheTest(final String name) {
		super(name);
	}
	
	public void testBasic() {
		DBCache cache = new DBCache(16, Util.Mb(32), -1);
		String key = "test1";
		String value = "value1";
		String key2 = "test2";
		String value2 = "value2";
		String key3 = "test3";
		String value3 = "value444433234234";

		assertTrue(cache.put(key, value.getBytes()));
		assertTrue(cache.put(key2, value2.getBytes()));
		assertFalse(cache.put(key, value.getBytes()));
		assertFalse(cache.put(key2, value2.getBytes()));

		byte[] v2 = cache.get(key);
		assertEquals(value, new String(v2));
		System.out.printf("%s get value %s\n", key, new String(v2));

		v2 = cache.get(key2);
		assertEquals(value2, new String(v2));
		System.out.printf("%s get value %s\n", key2, new String(v2));

		cache.remove(key);
		v2 = cache.get(key);
		assertNull(v2);
		System.out.printf("remove %s\n", key);

		assertTrue(cache.put(key, value.getBytes()));
		System.out.printf("put key1\n");

		v2 = cache.get(key);
		assertEquals(value, new String(v2));

		cache.remove(key);
		v2 = cache.get(key);
		assertNull(v2);
		System.out.printf("remove %s\n", key);

		assertTrue(cache.put(key3, value3.getBytes()));
		System.out.printf("put key3\n");

		v2 = cache.get(key3);
		assertEquals(value3, new String(v2));

		System.out.println("-----------------------------------");
		System.out.println(cache.info());

		System.out.println("test basic ok\n");
	}

	public void testBenchmark() {
		DBCache cache = new DBCache(16, Util.Mb(32), -1);
		byte[] values = new byte[] { (byte) 0xAD };
		long ts = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			assertTrue(cache.put(this.generateKey(i), values));
		}
		long te = System.currentTimeMillis();
		System.out.printf("cache set 100w used %d ms\n", te -ts);

		ts = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			byte[] v2 = cache.get(this.generateKey(i));
			assertNotNull(String.format("(%d) not get value", i), v2);
			assertEqualContent(values, v2);
		}
		te = System.currentTimeMillis();
		System.out.printf("cache get 100w used %d ms\n", te -ts);
		
		System.out.println(cache.info());
	}
	
	public void testMap() {
		HashMap<String, byte[]> maps = new HashMap<String, byte[]>();
		byte[] values = new byte[] { (byte) 0xAD };
		long ts = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			assertNull(maps.put(this.generateKey(i), values));
		}
		long te = System.currentTimeMillis();
		System.out.printf("map set 100w used %d ms\n", te -ts);

		ts = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			byte[] v2 = maps.get(this.generateKey(i));
			assertNotNull(String.format("(%d) not get value", i), v2);
			assertEqualContent(values, v2);
		}
		te = System.currentTimeMillis();
		System.out.printf("map get 100w used %d ms\n", te -ts);
	}
}
