package com.shansun.sparrow.cache.builder;

/**
 * @author lanbo <br>
 * @version 1.0 <br>
 * @date 2012-8-29
 */
public class CacheFactory {

	public static LocalCacheBuilder<Object, Object> newLocalCacheBuilder() {
		return LocalCacheBuilder.newBuilder();
	}

	public static TairCacheBuilder<Object, Object> newTairCacheBuilder() {
		return TairCacheBuilder.newBuilder();
	}
}
