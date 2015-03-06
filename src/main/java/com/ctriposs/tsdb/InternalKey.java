package com.ctriposs.tsdb;

import com.ctriposs.tsdb.storage.TimeItem;
import com.ctriposs.tsdb.util.ByteUtil;


public class InternalKey implements Comparable<InternalKey> {

	private int code;
	private long time;
	
	public InternalKey(short tableCode, short columnCode, long time) {
		this.code = ByteUtil.ToInt(tableCode, columnCode);
		this.time = time;
	}
	
	public InternalKey(int code,long time) {
		this.code = code;
		this.time = time;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public short getTableCode() {
		byte[] bytes = ByteUtil.toBytes(code);				
		return ByteUtil.ToShort(bytes, 0);
	}

	public short getColumnCode() {
		byte[] bytes = ByteUtil.toBytes(code);		
		return ByteUtil.ToShort(bytes, 2);
	}

	public int getCode(){
		return this.code;
	}

	public byte[] toTimeItemByte(int valueSize, long valueOffset){
		byte[] bytes = new byte[TimeItem.TIME_ITEM_SIZE];
		System.arraycopy(ByteUtil.toBytes(time), 0, bytes, TimeItem.TIME_OFFSET, 8);
		System.arraycopy(ByteUtil.toBytes(valueSize), 0, bytes, TimeItem.VALUE_SIZE_OFFSET, 4);
		System.arraycopy(ByteUtil.toBytes(valueOffset), 0, bytes, TimeItem.VALUE_OFFSET_OFFSET, 8);
		return bytes;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof InternalKey){
			InternalKey other = (InternalKey) o;
			if(code == other.code && time == other.time){
				return true;
			}
		}
		return false;
	}

	@Override
	public int compareTo(InternalKey o) {
		if( code == o.code){
			if(time == o.time){
				return 0;
			}else{
				if(time < o.time){
					return -1;
				}else{
					return 1;
				}
			}
		}else{
			if(code < o.code){
				return -1;
			}else{
				return 1;
			}
		}
		
	}
	
	@Override
	public String toString(){
		final StringBuilder sb = new StringBuilder();
		sb.append("InternalKey");
		sb.append("{code=").append(code);
        sb.append(", time=").append(time);
		sb.append('}');
		return sb.toString();
	}
}
