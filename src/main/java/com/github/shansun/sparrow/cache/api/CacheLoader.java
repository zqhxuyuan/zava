package com.github.shansun.sparrow.cache.api;

/**
 * @author lanbo <br>
 * @version 1.0 <br>
 * @date 2012-8-29
 */
public interface CacheLoader<K, V> {

	/**
	 * 加载键值到缓存
	 * 
	 * @param key
	 * @return
	 */
	V load(K key);
}
