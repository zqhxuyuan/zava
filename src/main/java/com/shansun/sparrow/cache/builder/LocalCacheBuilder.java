package com.shansun.sparrow.cache.builder;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.Weigher;

/**
 * @author lanbo <br>
 * @version 1.0 <br>
 * @date 2012-8-29
 */
public class LocalCacheBuilder<K, V> extends CacheBuilder<K, V> {
	com.google.common.cache.CacheBuilder<Object, Object>	builder	= com.google.common.cache.CacheBuilder.newBuilder();

	private LocalCacheBuilder() {
		super();
	}

	public static LocalCacheBuilder<Object, Object> newBuilder() {
		return new LocalCacheBuilder<Object, Object>();
	}

	public int hashCode() {
		return builder.hashCode();
	}

	public boolean equals(Object obj) {
		return builder.equals(obj);
	}

	public com.google.common.cache.CacheBuilder<Object, Object> initialCapacity(int initialCapacity) {
		return builder.initialCapacity(initialCapacity);
	}

	public com.google.common.cache.CacheBuilder<Object, Object> concurrencyLevel(int concurrencyLevel) {
		return builder.concurrencyLevel(concurrencyLevel);
	}

	public com.google.common.cache.CacheBuilder<Object, Object> maximumSize(long size) {
		return builder.maximumSize(size);
	}

	public com.google.common.cache.CacheBuilder<Object, Object> maximumWeight(long weight) {
		return builder.maximumWeight(weight);
	}

	public <K1, V1> com.google.common.cache.CacheBuilder<K1, V1> weigher(Weigher<? super K1, ? super V1> weigher) {
		return builder.weigher(weigher);
	}

	public com.google.common.cache.CacheBuilder<Object, Object> weakKeys() {
		return builder.weakKeys();
	}

	public com.google.common.cache.CacheBuilder<Object, Object> weakValues() {
		return builder.weakValues();
	}

	public com.google.common.cache.CacheBuilder<Object, Object> softValues() {
		return builder.softValues();
	}

	public com.google.common.cache.CacheBuilder<Object, Object> expireAfterWrite(long duration, TimeUnit unit) {
		return builder.expireAfterWrite(duration, unit);
	}

	public com.google.common.cache.CacheBuilder<Object, Object> expireAfterAccess(long duration, TimeUnit unit) {
		return builder.expireAfterAccess(duration, unit);
	}

	public com.google.common.cache.CacheBuilder<Object, Object> refreshAfterWrite(long duration, TimeUnit unit) {
		return builder.refreshAfterWrite(duration, unit);
	}

	public com.google.common.cache.CacheBuilder<Object, Object> ticker(Ticker ticker) {
		return builder.ticker(ticker);
	}

	public <K1, V1> com.google.common.cache.CacheBuilder<K1, V1> removalListener(RemovalListener<? super K1, ? super V1> listener) {
		return builder.removalListener(listener);
	}

	public com.google.common.cache.CacheBuilder<Object, Object> recordStats() {
		return builder.recordStats();
	}

	public <K1, V1> LoadingCache<K1, V1> build(CacheLoader<? super K1, V1> loader) {
		return builder.build(loader);
	}

	public <K1, V1> Cache<K1, V1> build() {
		return builder.build();
	}

	public String toString() {
		return builder.toString();
	}

}
