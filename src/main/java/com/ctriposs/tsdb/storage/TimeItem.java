package com.ctriposs.tsdb.storage;

import java.io.Serializable;

import com.ctriposs.tsdb.util.ByteUtil;

public class TimeItem implements Serializable, Comparable<TimeItem> {

	public static final int TIME_ITEM_SIZE = (Integer.SIZE + Long.SIZE + Long.SIZE ) / Byte.SIZE;

	public static final int TIME_OFFSET = 0;
	public static final int VALUE_SIZE_OFFSET = 8;
	public static final int VALUE_OFFSET_OFFSET = 12; 
	
	private long time;
	private int valueSize;
	private long valueOffset;

	public TimeItem(byte[] bytes) {
		this(bytes, 0);
	}

	public TimeItem(byte[] bytes, int offSet) {
		this.time = ByteUtil.ToLong(bytes, offSet + TIME_OFFSET);
		this.valueSize = ByteUtil.ToInt(bytes, offSet + VALUE_SIZE_OFFSET);
		this.valueOffset = ByteUtil.ToLong(bytes, offSet + VALUE_OFFSET_OFFSET);
		
	}
	
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getValueSize() {
		return valueSize;
	}

	public void setValueSize(int valueSize) {
		this.valueSize = valueSize;
	}

	public long getValueOffset() {
		return valueOffset;
	}

	public void setValueOffset(long valueOffset) {
		this.valueOffset = valueOffset;
	}

	@Override
	public int compareTo(TimeItem o) {

		int diff = (int) (time - o.time);
		return diff;
	}

}
