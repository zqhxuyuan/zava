package com.ctriposs.sdb.table;

import java.io.IOException;

public interface IMapEntry {
	
	final static int INDEX_ITEM_IN_DATA_FILE_OFFSET_OFFSET = 0;
	final static int INDEX_ITEM_KEY_LENGTH_OFFSET = 8;
	final static int INDEX_ITEM_VALUE_LENGTH_OFFSET = 12;
	final static int INDEX_ITEM_TIME_TO_LIVE_OFFSET = 16;
	final static int INDEX_ITEM_CREATED_TIME_OFFSET = 24;
	final static int INDEX_ITEM_KEY_HASH_CODE_OFFSET = 32;
	final static int INDEX_ITEM_STATUS = 36;
	
	int getIndex();
	
    byte[] getKey() throws IOException;
	
	byte[] getValue() throws IOException;
	
	int getKeyHash() throws IOException;
	
	long getTimeToLive() throws IOException;
	
	long getCreatedTime() throws IOException;

	boolean isDeleted() throws IOException;
	
	void markDeleted() throws IOException;
	
	boolean isInUse() throws IOException;
	
	boolean isExpired() throws IOException;
	
	boolean isCompressed() throws IOException;
}
