package com.ctriposs.bigmap;

/**
 * Configuration for BigConcurrentHashMap
 * 
 * @author bulldog
 *
 */
public class BigConfig {
	
	private int initialCapacity = BigConcurrentHashMapImpl.DEFAULT_INITIAL_CAPACITY;
	private int concurrencyLevel = BigConcurrentHashMapImpl.DEFAULT_CONCURRENCY_LEVEL;
	private float loadFactor = BigConcurrentHashMapImpl.DEFAULT_LOAD_FACTOR;
	private long purgeIntervalInMs = BigConcurrentHashMapImpl.DEFAULT_PURGE_INTERVAL;
	private boolean reloadOnStartup = BigConcurrentHashMapImpl.DEFAULT_RELOAD_ON_STARTUP;
	
	public boolean isReloadOnStartup() {
		return reloadOnStartup;
	}

	public BigConfig setReloadOnStartup(boolean reloadOnStartup) {
		this.reloadOnStartup = reloadOnStartup;
		return this;
	}
	
	public int getInitialCapacity() {
		return initialCapacity;
	}

	public BigConfig setInitialCapacity(int initialCapacity) {
		this.initialCapacity = initialCapacity > BigConcurrentHashMapImpl.MAXIMUM_CAPACITY ? BigConcurrentHashMapImpl.MAXIMUM_CAPACITY : initialCapacity;
		return this;
	}

	public int getConcurrencyLevel() {
		return concurrencyLevel;
	}
	
	public BigConfig setConcurrencyLevel(int concurrencyLevel) {
		this.concurrencyLevel = concurrencyLevel > BigConcurrentHashMapImpl.MAX_SEGMENTS ? BigConcurrentHashMapImpl.MAX_SEGMENTS : concurrencyLevel;
		return this;
	}
	
	public float getLoadFactor() {
		return loadFactor;
	}

	public BigConfig setLoadFactor(float loadFactor) {
		this.loadFactor = loadFactor;
		return this;
	}

	public long getPurgeIntervalInMs() {
		return purgeIntervalInMs;
	}
	
	public BigConfig setPurgeIntervalInMs(long purgeIntervalInMs) {
		this.purgeIntervalInMs = purgeIntervalInMs;
		return this;
	}
}
