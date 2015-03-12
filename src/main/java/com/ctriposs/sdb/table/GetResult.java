package com.ctriposs.sdb.table;

public class GetResult {
	
	private byte[] value;
	
	private boolean deleted;
	
	private boolean expired;
	
	private int Level;
	
	private long timeToLive;
	
	private long createdTime;

	public boolean isFound() {
		return value != null;
	}
	
	public boolean isDeleted() {
		return deleted;
	}
	
	public boolean isExpired() {
		return expired;
	}
	
	public byte[] getValue() {
		return value;
	}
	
	void setValue(byte[] value) {
		this.value = value;
	}
	
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	void setExpired(boolean expired) {
		this.expired = expired;
	}

	public int getLevel() {
		return Level;
	}

	public void setLevel(int level) {
		Level = level;
	}

	public long getTimeToLive() {
		return timeToLive;
	}

	void setTimeToLive(long timeToLive) {
		this.timeToLive = timeToLive;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
}
