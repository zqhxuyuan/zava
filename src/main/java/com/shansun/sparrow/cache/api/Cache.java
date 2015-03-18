package com.shansun.sparrow.cache.api;

/**
 * @author lanbo <br>
 * @version 1.0 <br>
 * @date 2012-8-29
 */
public interface Cache<K, V> {

	/**
	 * 获取缓存值，如果缓存中没有，同步调用{$link loader#load}载入内存，并返回加载后的内容
	 * 
	 * @param key
	 * @param loader
	 * @return
	 */
	V get(K key, CacheLoader<K, V> loader);

	/**
	 * 获取缓存值，如果缓存中没有则返回null
	 * 
	 * @param key
	 * @return
	 */
	V getIfPresent(K key);

	/**
	 * 放置缓存
	 * 
	 * @param key
	 * @param value
	 */
	void put(K key, V value);

	/**
	 * 将key对应的缓存内容置为无效
	 * 
	 * @param key
	 */
	void invalid(K key);

	/**
	 * 获取缓存堆大小，有些缓存体可能不支持size()方法，如Tair等
	 * 
	 * @return
	 */
	long size();
}
