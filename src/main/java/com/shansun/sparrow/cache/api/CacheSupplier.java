package com.shansun.sparrow.cache.api;

/**
 * @author lanbo <br>
 * @version 1.0 <br>
 * @date 2012-8-29
 */
public interface CacheSupplier<K, V> {

	/**
	 * 放置缓存到相应的缓存体
	 * 
	 * @param key
	 * @param value
	 */
	void supply(K key, V value);
}
