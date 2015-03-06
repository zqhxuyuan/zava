package com.ctriposs.tsdb.storage;

import java.io.Serializable;

import com.ctriposs.tsdb.InternalKey;
import com.ctriposs.tsdb.util.ByteUtil;

public class Head implements Serializable {
	
	public static final int HEAD_SIZE = (Long.SIZE + Integer.SIZE + Long.SIZE + Integer.SIZE + Long.SIZE + Integer.SIZE + Long.SIZE) / Byte.SIZE;
	
	public static final int CODE_OFFSET_OFFSET = 0;
	public static final int CODE_COUNT_OFFSET = 8;
	public static final int TIME_COUNT_OFFSET = 12;
	public static final int MIN_CODE_OFFSET = 20;
	public static final int MIN_TIME_OFFSET = 24;
	public static final int MAX_CODE_OFFSET = 32;
	public static final int MAX_TIME_OFFSET = 36;
	
	private final long codeOffset;
	private final int codeCount;
	private final long timeCount;
    private final InternalKey smallest;
    private final InternalKey largest;
    
    public Head(long codeOffset, int codeCount, long timeCount, InternalKey smallest, InternalKey largest) {
    	this.codeOffset = codeOffset;
    	this.codeCount = codeCount;
    	this.timeCount = timeCount;
    	this.smallest = smallest;
    	this.largest = largest;
    }
    
    public Head(byte[] bytes){
    	this.codeOffset = ByteUtil.ToLong(bytes, CODE_OFFSET_OFFSET);
    	this.codeCount = ByteUtil.ToInt(bytes, CODE_COUNT_OFFSET);
    	this.timeCount = ByteUtil.ToLong(bytes, TIME_COUNT_OFFSET);
    	this.smallest = new InternalKey(ByteUtil.ToInt(bytes, MIN_CODE_OFFSET), ByteUtil.ToLong(bytes, MIN_TIME_OFFSET));
    	this.largest = new InternalKey(ByteUtil.ToInt(bytes, MAX_CODE_OFFSET), ByteUtil.ToLong(bytes, MAX_TIME_OFFSET));
    }
    

	public byte[] toByte(){
		byte[] bytes = new byte[HEAD_SIZE];
		System.arraycopy(ByteUtil.toBytes(codeOffset), 0, bytes, CODE_OFFSET_OFFSET, 8);
		System.arraycopy(ByteUtil.toBytes(codeCount), 0, bytes, CODE_COUNT_OFFSET, 4);
		System.arraycopy(ByteUtil.toBytes(timeCount), 0, bytes, TIME_COUNT_OFFSET, 8);
		System.arraycopy(ByteUtil.toBytes(smallest.getCode()), 0, bytes, MIN_CODE_OFFSET, 4);
		System.arraycopy(ByteUtil.toBytes(smallest.getTime()), 0, bytes, MIN_TIME_OFFSET, 8);
		System.arraycopy(ByteUtil.toBytes(largest.getCode()), 0, bytes, MAX_CODE_OFFSET, 4);
		System.arraycopy(ByteUtil.toBytes(largest.getTime()), 0, bytes, MAX_TIME_OFFSET, 8);	
		return bytes;
	}

	public long getCodeOffset() {
		return codeOffset;
	}

	public int getCodeCount() {
		return codeCount;
	}

	public long getTimeCount() {
		return timeCount;
	}

	public InternalKey getSmallest() {
		return smallest;
	}

	public InternalKey getLargest() {
		return largest;
	}

	public boolean containCode(int code){
		boolean result = false;
		if(code >= smallest.getCode()&&code <= largest.getCode()){
			result = true;
		}
		
		return result;
	}
	
	public boolean containKey(InternalKey key){
		boolean result = false;
		if(smallest.compareTo(key)<=0&&largest.compareTo(key)>=0){
			result = true;
		}
		return result;
	}
}
