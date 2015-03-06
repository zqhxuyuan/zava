package com.ctriposs.tsdb.storage;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import com.ctriposs.tsdb.util.ByteUtil;

public class CodeItem implements Serializable, Comparable<CodeItem> {

	public static final int CODE_ITEM_SIZE = (Integer.SIZE + Long.SIZE + Long.SIZE + Long.SIZE + Integer.SIZE) / Byte.SIZE;

	public static final int CODE_OFFSET = 0;
	public static final int MIN_TIME_OFFSET = 4;
	public static final int MAX_TIME_OFFSET = 12;
	public static final int TIME_OFFSET_OFFSET = 20;
	public static final int TIME_COUNT_OFFSET = 28;
	
	private int code;
	private long minTime;
	private long maxTime;
	private long timeOffSet;
	private AtomicInteger timeCount;

	public CodeItem(byte[] bytes) {
		this(bytes, 0);
	}

	public CodeItem(byte[] bytes, int offSet) {
		this.code = ByteUtil.ToInt(bytes, offSet + CODE_OFFSET);
		this.minTime = ByteUtil.ToLong(bytes, offSet + MIN_TIME_OFFSET);
		this.maxTime = ByteUtil.ToLong(bytes, offSet + MAX_TIME_OFFSET);
		this.timeOffSet = ByteUtil.ToLong(bytes, offSet + TIME_OFFSET_OFFSET);
		this.timeCount = new AtomicInteger(ByteUtil.ToInt(bytes, offSet + TIME_COUNT_OFFSET));
	}
	
	public CodeItem(int code, long timeOffset, long minTime, long maxTime) {
		this.code = code;
		this.timeOffSet = timeOffset;
		this.minTime = minTime;
		this.maxTime = maxTime;
		this.timeCount = new AtomicInteger(0);
	}
	
	public byte[] toByte(){
		byte[] bytes = new byte[CodeItem.CODE_ITEM_SIZE];
		System.arraycopy(ByteUtil.toBytes(code), 0, bytes, CodeItem.CODE_OFFSET, 4);
		System.arraycopy(ByteUtil.toBytes(minTime), 0, bytes, CodeItem.MIN_TIME_OFFSET, 8);
		System.arraycopy(ByteUtil.toBytes(maxTime), 0, bytes, CodeItem.MAX_TIME_OFFSET, 8);
		System.arraycopy(ByteUtil.toBytes(timeOffSet), 0, bytes, CodeItem.TIME_OFFSET_OFFSET, 8);
		System.arraycopy(ByteUtil.toBytes(timeCount.get()), 0, bytes, CodeItem.TIME_COUNT_OFFSET, 4);
		return bytes;
	}
	
	public void addTimeItem(long time){
		if(time<minTime){
			minTime = time;
		}else{
			if(time>maxTime){
				maxTime = time;
			}
		}
		timeCount.incrementAndGet();
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public long getMinTime() {
		return minTime;
	}

	public void setMinTime(long minTime) {
		this.minTime = minTime;
	}

	public long getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(long maxTime) {
		this.maxTime = maxTime;
	}

	public long getTimeOffSet() {
		return timeOffSet;
	}

	public void setTimeOffSet(long timeOffSet) {
		this.timeOffSet = timeOffSet;
	}

	public int getTimeCount() {
		return timeCount.get();
	}

	public boolean contain(long time){
		if(time>=minTime&&time<=maxTime){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public int compareTo(CodeItem o) {
		if(code == o.code){
			return 0;
		}else{
			if(code < o.code){
				return -1;
			}else{
				return 1;
			}
		}
	}
	
}
