package com.shansun.sparrow.cache.api;

/**
 * @author lanbo <br>
 * @version 1.0 <br>
 * @date 2012-8-29
 */
public interface CacheLoader<K, V> {

	/**
	 * ¼ÓÔØ¼üÖµµ½»º´æ
	 * 
	 * @param key
	 * @return
	 */
	V load(K key);
}
