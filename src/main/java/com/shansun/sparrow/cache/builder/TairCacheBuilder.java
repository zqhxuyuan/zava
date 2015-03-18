package com.shansun.sparrow.cache.builder;

/**
 * @author lanbo <br>
 * @version 1.0 <br>
 * @date 2012-8-29
 */
public class TairCacheBuilder<K, V> extends CacheBuilder<K, V> {

	private TairCacheBuilder() {
	}

	public static TairCacheBuilder<Object, Object> newBuilder() {
		return new TairCacheBuilder<Object, Object>();
	}
}
