package com.ctriposs.tsdb;

import java.util.Map.Entry;

public class InternalEntry implements Entry<InternalKey, byte[]> {
	private InternalKey key;
	private byte[] value;
	
	public InternalEntry(InternalKey key,byte[] value){
		this.key = key;
		this.value = value;
	}

	@Override
	public InternalKey getKey() {
		return key;
	}

	@Override
	public byte[] getValue() {
		return value;
	}

	@Override
	public byte[] setValue(byte[] value) {
		byte[] old = value;
		this.value = value;
		return old;
	}
}