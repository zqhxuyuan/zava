package com.ctriposs.sdb.table;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * File channel map entry implementation
 * 
 * @author bulldog
 *
 */
public class FCMapEntryImpl implements IMapEntry {
	
	private int index;
	
	private FileChannel dataChannel;
	// index cached with memory mapped file
	private MappedByteBuffer indexMappedByteBuffer;
	
	// cache
	private byte[] key;
	private byte[] value;
	
	public FCMapEntryImpl(int index, MappedByteBuffer indexMappedByteBuffer, FileChannel dataChannel) {
		this.index = index;
		this.dataChannel = dataChannel;
		this.indexMappedByteBuffer = indexMappedByteBuffer;
	}

	long getItemOffsetInDataFile() throws IOException {
		int offsetInIndexFile = AbstractMapTable.INDEX_ITEM_LENGTH * index;
		return this.indexMappedByteBuffer.getLong(offsetInIndexFile);
	}
	
	private int getKeyLength() throws IOException {
		int offsetInIndexFile = AbstractMapTable.INDEX_ITEM_LENGTH * index + IMapEntry.INDEX_ITEM_KEY_LENGTH_OFFSET;
		return this.indexMappedByteBuffer.getInt(offsetInIndexFile);
	}
	
	@Override
	public int getKeyHash() throws IOException {
		int offsetInIndexFile = AbstractMapTable.INDEX_ITEM_LENGTH * index + IMapEntry.INDEX_ITEM_KEY_HASH_CODE_OFFSET;
		int hashCode = this.indexMappedByteBuffer.getInt(offsetInIndexFile);
		return hashCode;
	}
	
	private int getValueLength() throws IOException {
		int offsetInIndexFile = AbstractMapTable.INDEX_ITEM_LENGTH * index + IMapEntry.INDEX_ITEM_VALUE_LENGTH_OFFSET;
		return this.indexMappedByteBuffer.getInt(offsetInIndexFile);
	}
	
	@Override
	public byte[] getKey() throws IOException {
		if (key != null) return key;
		long itemOffsetInDataFile = this.getItemOffsetInDataFile();
		int keyLength = this.getKeyLength();
		ByteBuffer keyBuf = ByteBuffer.allocate(keyLength);
		this.dataChannel.read(keyBuf, itemOffsetInDataFile);
		key = keyBuf.array();
		return key;
	}


	@Override
	public byte[] getValue() throws IOException {
		if (value != null) return value;
		long itemOffsetInDataFile = this.getItemOffsetInDataFile();
		int keyLength = this.getKeyLength();
		int valueLength = this.getValueLength();
		ByteBuffer valueBuf = ByteBuffer.allocate(valueLength);
		this.dataChannel.read(valueBuf, itemOffsetInDataFile + keyLength);
		value = valueBuf.array();
		return value;
	}

	@Override
	public long getTimeToLive() throws IOException {
		int offsetInIndexFile = AbstractMapTable.INDEX_ITEM_LENGTH * index + IMapEntry.INDEX_ITEM_TIME_TO_LIVE_OFFSET;
		return this.indexMappedByteBuffer.getLong(offsetInIndexFile);
	}

	@Override
	public long getCreatedTime() throws IOException {
		int offsetInIndexFile = AbstractMapTable.INDEX_ITEM_LENGTH * index + IMapEntry.INDEX_ITEM_CREATED_TIME_OFFSET;
		return this.indexMappedByteBuffer.getLong(offsetInIndexFile);
	}

	@Override
	public boolean isDeleted() throws IOException {
		int offsetInIndexFile = AbstractMapTable.INDEX_ITEM_LENGTH * index + IMapEntry.INDEX_ITEM_STATUS;
		byte status = this.indexMappedByteBuffer.get(offsetInIndexFile);
		return (status & ( 1 << 1)) != 0;
	}

	@Override
	public void markDeleted() throws IOException {
		int offsetInIndexFile = AbstractMapTable.INDEX_ITEM_LENGTH * index + IMapEntry.INDEX_ITEM_STATUS;
		byte status = this.indexMappedByteBuffer.get(offsetInIndexFile);
		status = (byte) (status | 1 << 1);
		this.indexMappedByteBuffer.put((int)offsetInIndexFile, status);
	}

	@Override
	public boolean isExpired() throws IOException {
		long ttl = this.getTimeToLive();
		if (ttl > 0) {
			if (System.currentTimeMillis() - this.getCreatedTime() > ttl) return true;
		}
		return false;
	}

	@Override
	public boolean isInUse() throws IOException {
		int offsetInIndexFile = AbstractMapTable.INDEX_ITEM_LENGTH * index + IMapEntry.INDEX_ITEM_STATUS;
		byte status = this.indexMappedByteBuffer.get(offsetInIndexFile);
		return (status & 1) != 0;
	}

	@Override
	public int getIndex() {
		return this.index;
	}

	@Override
	public boolean isCompressed() throws IOException {
		int offsetInIndexFile = AbstractMapTable.INDEX_ITEM_LENGTH * index + IMapEntry.INDEX_ITEM_STATUS;
		byte status = this.indexMappedByteBuffer.get(offsetInIndexFile);
		return (status & ( 1 << 2)) != 0;
	}
}
