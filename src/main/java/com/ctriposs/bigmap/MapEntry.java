package com.ctriposs.bigmap;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import com.ctriposs.bigmap.page.IMappedPage;
import com.ctriposs.bigmap.page.IMappedPageFactory;;

/**
 * Helper class representing an entry pointing to the memory mapped map entry.
 * 
 * @author bulldog
 *
 */
public class MapEntry {
	
	final static int INDEX_ITEM_DATA_PAGE_INDEX_OFFSET = 0;
	final static int INDEX_ITEM_DATA_SLOT_OFFSET_OFFSET = 8;
	final static int INDEX_ITEM_DATA_SLOT_LENGTH_OFFSET = 12;
    final static int INDEX_ITEM_MAP_ENTRY_KEY_LENGTH_OFFSET = 16;
	final static int INDEX_ITEM_MAP_ENTRY_VALUE_LENGTH_OFFSET = 20;
	final static int INDEX_ITEM_MAP_ENTRY_CREATED_TIME_OFFSET = 24;
	final static int INDEX_ITEM_MAP_ENTRY_LAST_ACCESS_TIME_OFFSET = 32;
	final static int INDEX_ITEM_MAP_ENTRY_TIME_TO_LIVE_OFFSET = 40;
	final static int INDEX_ITEM_MAP_ENTRY_STATUS = 60;
	
	private IMappedPage indexPage;
	private IMappedPageFactory dataPageFactory;
	private long index;
	private int indexItemOffset;
	private int realLength;
	
	public MapEntry(long index, int realLength, int indexItemOffset, IMappedPage indexPage, IMappedPageFactory dataPageFactory) {
		this.index = index;
		this.realLength = realLength;
		this.indexItemOffset = indexItemOffset;
		this.indexPage = indexPage;
		this.dataPageFactory = dataPageFactory;
	}
	
	public MapEntry(long index, int indexItemOffset, IMappedPage indexPage, IMappedPageFactory dataPageFactory) {
		this.index = index;
		this.indexItemOffset = indexItemOffset;
		this.indexPage = indexPage;
		this.dataPageFactory = dataPageFactory;
		this.realLength = this.getKeyLength() + this.getValueLength();
	}
	
	public long getIndex() {
		return this.index;
	}
	
	/**
	 * key length + value length
	 * @return length
	 */
	public int getRealEntryLength() {
		return this.realLength;
	}
	
	public int getSlotSize() {
		return indexPage.getLocal().getInt(indexItemOffset + INDEX_ITEM_DATA_SLOT_LENGTH_OFFSET);
	}
	
	public int getKeyLength() {
		return indexPage.getLocal().getInt(indexItemOffset + INDEX_ITEM_MAP_ENTRY_KEY_LENGTH_OFFSET);
	}
	
	public void putKeyLength(int keyLength) {
		indexPage.getLocal().putInt(indexItemOffset + INDEX_ITEM_MAP_ENTRY_KEY_LENGTH_OFFSET, keyLength);
		indexPage.setDirty(true);
	}
	
	public int getValueLength() {
		return indexPage.getLocal().getInt(indexItemOffset + INDEX_ITEM_MAP_ENTRY_VALUE_LENGTH_OFFSET);
	}
	
	public void putValueLength(int valueLength) {
		indexPage.getLocal().putInt(indexItemOffset + INDEX_ITEM_MAP_ENTRY_VALUE_LENGTH_OFFSET, valueLength);
		indexPage.setDirty(true);
	}
	
	public long getCreatedTime() {
		return indexPage.getLocal().getLong(indexItemOffset + INDEX_ITEM_MAP_ENTRY_CREATED_TIME_OFFSET);
	}
	
	public void putCreatedTime(long createdTime) {
		indexPage.getLocal().putLong(indexItemOffset + INDEX_ITEM_MAP_ENTRY_CREATED_TIME_OFFSET, createdTime);
		indexPage.setDirty(true);
	}
	
	public long getLastAccessedTime() {
		return indexPage.getLocal().getLong(indexItemOffset + INDEX_ITEM_MAP_ENTRY_LAST_ACCESS_TIME_OFFSET);
	}
	
	public void putLastAccessedTime(long lastAccessedTime) {
		indexPage.getLocal().putLong(indexItemOffset + INDEX_ITEM_MAP_ENTRY_LAST_ACCESS_TIME_OFFSET, lastAccessedTime);
		indexPage.setDirty(true);
	}
	
	public long getTimeToLive() {
		return indexPage.getLocal().getLong(indexItemOffset + INDEX_ITEM_MAP_ENTRY_TIME_TO_LIVE_OFFSET);
	}
	
	public void putTimeToLive(long ttlInMs) {
		indexPage.getLocal().putLong(indexItemOffset + INDEX_ITEM_MAP_ENTRY_TIME_TO_LIVE_OFFSET, ttlInMs);
		indexPage.setDirty(true);
	}
	
	public boolean isReleased() {
		byte status = indexPage.getLocal().get(indexItemOffset + INDEX_ITEM_MAP_ENTRY_STATUS);
		return (status & (1 << 1)) != 0;
	}
	
	public void markReleased() {
		byte status = indexPage.getLocal().get(indexItemOffset + INDEX_ITEM_MAP_ENTRY_STATUS);
		status = (byte) (status | ( 1 << 1));
		indexPage.getLocal().put(indexItemOffset + INDEX_ITEM_MAP_ENTRY_STATUS, status);
		indexPage.setDirty(true);
	}
	
	public boolean isInUse() {
		byte status = indexPage.getLocal().get(indexItemOffset + INDEX_ITEM_MAP_ENTRY_STATUS);
		return (status & (1 << 1)) == 0;
	}
	
	public void MarkInUse() {
		byte status = indexPage.getLocal().get(indexItemOffset + INDEX_ITEM_MAP_ENTRY_STATUS);
	    status = (byte) (status & ~( 1 << 1));
		indexPage.getLocal().put(indexItemOffset + INDEX_ITEM_MAP_ENTRY_STATUS, status);
		indexPage.setDirty(true);
	}
	
	public boolean isAllocated() {
		byte status = indexPage.getLocal().get(indexItemOffset + INDEX_ITEM_MAP_ENTRY_STATUS);
		return (status & 1) != 0;
	}
	
	public void MarkAllocated() {
		byte status = indexPage.getLocal().get(indexItemOffset + INDEX_ITEM_MAP_ENTRY_STATUS);
		status = (byte) (status | 1);
		indexPage.getLocal().put(indexItemOffset + INDEX_ITEM_MAP_ENTRY_STATUS, status);
		indexPage.setDirty(true);
	}
	
	public byte[] getEntryKey() throws IOException {
		long dataPageIndex = indexPage.getLocal().getLong(indexItemOffset + INDEX_ITEM_DATA_PAGE_INDEX_OFFSET);
		int dataSlotOffset = indexPage.getLocal().getInt(indexItemOffset + INDEX_ITEM_DATA_SLOT_OFFSET_OFFSET);
		int entryKeyOffset = dataSlotOffset;
		int entryKeyLength = this.getKeyLength();
		IMappedPage dataPage = dataPageFactory.acquirePage(dataPageIndex);
		byte[] value = dataPage.getLocal(entryKeyOffset, entryKeyLength);
		return value;
	}
	
	public void putEntryKey(byte[] entryKey) throws IOException {
		long dataPageIndex = indexPage.getLocal().getLong(indexItemOffset + INDEX_ITEM_DATA_PAGE_INDEX_OFFSET);
		int dataSlotOffset = indexPage.getLocal().getInt(indexItemOffset + INDEX_ITEM_DATA_SLOT_OFFSET_OFFSET);
		int entryKeyOffset = dataSlotOffset;
		IMappedPage dataPage = dataPageFactory.acquirePage(dataPageIndex);
		ByteBuffer dataItemBuffer = dataPage.getLocal(entryKeyOffset);
		dataItemBuffer.put(entryKey);
		dataPage.setDirty(true);
	}
	
	public byte[] getEntryValue() throws IOException {
		long dataPageIndex = indexPage.getLocal().getLong(indexItemOffset + INDEX_ITEM_DATA_PAGE_INDEX_OFFSET);
		int dataSlotOffset = indexPage.getLocal().getInt(indexItemOffset + INDEX_ITEM_DATA_SLOT_OFFSET_OFFSET);
		int entryValueOffset = dataSlotOffset + this.getKeyLength();
		int entryValueLength = getValueLength();
		IMappedPage dataPage = dataPageFactory.acquirePage(dataPageIndex);
		byte[] value = dataPage.getLocal(entryValueOffset, entryValueLength);
		return value;
	}
	
	public void putEntryValue(byte[] entryValue) throws IOException {
		long dataPageIndex = indexPage.getLocal().getLong(indexItemOffset + INDEX_ITEM_DATA_PAGE_INDEX_OFFSET);
		int dataSlotOffset = indexPage.getLocal().getInt(indexItemOffset + INDEX_ITEM_DATA_SLOT_OFFSET_OFFSET);
		int entryValueOffset = dataSlotOffset + this.getKeyLength();
		IMappedPage dataPage = dataPageFactory.acquirePage(dataPageIndex);
		ByteBuffer dataItemBuffer = dataPage.getLocal(entryValueOffset);
		dataItemBuffer.put(entryValue);
		dataPage.setDirty(true);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (o == this) return true;
		if (getClass() != o.getClass()) return false;
		MapEntry other = (MapEntry)o;
		
		try {
			// equals only if the keys are equal
			return Arrays.equals(this.getEntryKey(), other.getEntryKey());
		} catch (IOException e) {
			throw new RuntimeException("fail to get entry key", e);
		}
	}
}