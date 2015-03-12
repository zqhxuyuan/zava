package com.ctriposs.sdb;

public class DBConfig {
	
	public static final DBConfig SMALL = new DBConfig().setShardNumber((short)1);
	public static final DBConfig DEFAULT = new DBConfig().setShardNumber((short)4);
	public static final DBConfig BIG = new DBConfig().setShardNumber((short)8);
	public static final DBConfig LARGE = new DBConfig().setShardNumber((short)16);
	public static final DBConfig HUGE = new DBConfig().setShardNumber((short)32);
	
	private short shardNumber = 4;
	
	private boolean compressionEnabled = true;
	private boolean localityEnabled = false;
	
	public boolean isCompressionEnabled() {
		return compressionEnabled;
	}
	
	public boolean isLocalityEnabled() {
		return this.localityEnabled;
	}
	
	/**
	 * Important: shard number can't be changed for an existing DB.
	 * 
	 * @return shard number
	 */
	public short getShardNumber() {
		return shardNumber;
	}

	/**
	 * Enable snappy compression for value
	 * 
	 * @param compressionEnabled
	 * @return Session DB configuration
	 */
	public DBConfig setCompressionEnabled(boolean compressionEnabled) {
		this.compressionEnabled = compressionEnabled;
		return this;
	}
	
	private DBConfig setShardNumber(short shardNumber) {
		this.shardNumber = shardNumber;
		return this;
	}
	
	/**
	 * Enable data access locality, if enabled, when a key/value entry is found in Level 2 FCMapTable,
	 * it will be moved to current active HashMapTable for locality. 
	 * 
	 * @param localityEnabled
	 * @return Session DB configuration
	 */
	public DBConfig setLocalityEnabled(boolean localityEnabled) {
		this.localityEnabled = localityEnabled;
		return this;
	}

}
