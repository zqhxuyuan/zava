/**
 * 
 */
package com.zqh.java.fkv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Fixed length key-value store implement.
 * 
 * @author sean.wang
 * @since Nov 16, 2011
 */
public class FkvImpl implements Fkv {

    private FkvStore store;
    private int keyLength;
    private int valueLength;
    private int recordLength;
    private int maxRecordSize;
    private byte[] writeBuffer;

    private Map<String, Record> activeCache;
	private Deque<Record> deletedCache;
	private final Lock writeLock = new ReentrantLock();
	private int endIndex = 0;
	private static final byte STATUS_DELETE = '0';
	private static final byte STATUS_ACTIVE = '1';
	private static final byte ENDER = '\n';
	private static final int STATUS_LENGTH = 1;
	private static final int ENDER_LENGTH = 1;

	public FkvImpl(File dbFile, int fixedKeyLength, int fixedValueLength) throws IOException {
		this(dbFile, 0, fixedKeyLength, fixedValueLength);
	}

	public FkvImpl(File dbFile, int maxRecordSize, int keyLength, int valueLength) throws IOException {
		this.keyLength = keyLength;
		this.valueLength = valueLength;
		this.recordLength = STATUS_LENGTH + keyLength + valueLength + ENDER_LENGTH;
		this.writeBuffer = new byte[recordLength];
		this.maxRecordSize = maxRecordSize;
		// init store
		this.store = new FkvFileStore(dbFile, this.recordLength * maxRecordSize);
		// init active cache
		this.activeCache = new HashMap<String, Record>(maxRecordSize);
		// init deleted stack
		this.deletedCache = new ArrayDeque<Record>();
		deserial();
	}

	private void cacheNewRecord(String key, Record newRecord) {
		this.activeCache.put(key, newRecord);
	}

	@Override
	public void close() throws IOException {
		this.store.close();
	}

	private Record createNewRecord(String key, String value, int index) {
		Record newRecord = new Record();
		newRecord.setValue(value);
		newRecord.setKey(key);
		newRecord.setIndex(index);
		return newRecord;
	}

	@Override
	public void delete(String key) {
		try {
			writeLock.lock();
			Record r = this.activeCache.get(key);
			if (r != null) {
				Record deletedRecord = this.activeCache.remove(key);
				this.deletedCache.add(deletedRecord);
				this.store.put(deletedRecord.getIndex(), STATUS_DELETE);
			}
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public void clear() {
		try {
			writeLock.lock();
			Set<Entry<String, Record>> set = this.activeCache.entrySet();
			for (Entry<String, Record> entry : set) {
				Record deletedRecord = entry.getValue();
				this.deletedCache.add(deletedRecord);
				this.store.put(deletedRecord.getIndex(), STATUS_DELETE);
			}
			this.activeCache.clear();
		} finally {
			writeLock.unlock();
		}
	}

	protected void deserial() {
		if (this.store.isNeedDeserial()) {
			byte[] recordBuf = new byte[recordLength];
			int index = 0;
			while (this.store.remaining() > 0) {
				store.get(recordBuf);
				if (isValidRecord(recordBuf)) {
					byte[] keyBuf = new byte[keyLength];
					System.arraycopy(recordBuf, STATUS_LENGTH, keyBuf, 0, keyLength);
					byte[] valueBuf = new byte[valueLength];
					System.arraycopy(recordBuf, STATUS_LENGTH + keyLength, valueBuf, 0, valueLength);
					Record record = this.createNewRecord(new String(keyBuf), new String(valueBuf), index);
					if (isDelete(recordBuf)) {
						this.deletedCache.push(record);
					} else {
						this.activeCache.put(record.getKey(), record);
					}
					index += recordLength;
				} else {
					// reach ender, break
					break;
				}
			}
			this.endIndex = index;
		}
	}

	@Override
	public String get(String key) {
		Record record = this.activeCache.get(key);
		if (record == null) {
			return null;
		}
		return record.getValue();
	}

	public Map<String, Record> getActiveCache() {
		return this.activeCache;
	}

	public Deque<Record> getDeletedCache() {
		return this.deletedCache;
	}

	public int getDeletedSize() {
		return this.deletedCache.size();
	}

	public int getEndIndex() {
		return endIndex;
	}

	public int getKeyLength() {
		return keyLength;
	}

	public int getMaxRecordSize() {
		return maxRecordSize;
	}

	public Record getRecord(String key) {
		Record record = null;
		record = this.activeCache.get(key);
		return record;
	}

	public int getRecordLength() {
		return recordLength;
	}

	public FkvStore getStore() {
		return store;
	}

	public int getValueLength() {
		return valueLength;
	}

	private boolean isDelete(byte[] record) {
		if (record[0] == STATUS_DELETE) {
			return true;
		}
		return false;
	}

	private boolean isValidRecord(byte[] recordBuf) {
		if (recordBuf[0] != STATUS_ACTIVE && recordBuf[0] != STATUS_DELETE) {
			return false;
		}
		if (recordBuf[recordBuf.length - 1] != ENDER) {
			return false;
		}
		return true;
	}

	@Override
	public void put(String key, String value) {
		int keyLength = this.keyLength;
		if (key == null || key.getBytes().length != keyLength) {
			throw new IllegalArgumentException("key:" + key);
		}
		if (value == null || value.getBytes().length != this.valueLength) {
			throw new IllegalArgumentException("value:" + value);
		}
		if (size() >= this.maxRecordSize) {
			throw new StackOverflowError("key:" + key + " vlaue:" + value + " size:" + size());
		}
		try {
			writeLock.lock();
			Record record = this.activeCache.get(key);
			if (record == null) {  //新增
				putNewRecord(key, value);
			} else { //更新
				record.setValue(value);
				this.store.put(record.getIndex() + STATUS_LENGTH + keyLength, value.getBytes());
			}
		} finally {
			writeLock.unlock();
		}

	}

	private void putNewRecord(String key, String value) {
		int index;
		if (this.deletedCache.isEmpty()) { // no deleted record
			index = endIndex;
			endIndex += this.recordLength;
		} else {
			Record deletedRecord = this.deletedCache.pop();
			index = deletedRecord.getIndex();
		}
		Record newRecord = createNewRecord(key, value, index);
		storeNewRecord(newRecord); // first store record
		cacheNewRecord(key, newRecord); // second cache record
	}

	@Override
	public int size() {
		return this.activeCache.size();
	}

	private void storeNewRecord(Record newRecord) {
		writeBuffer[0] = STATUS_ACTIVE;
		byte[] key = newRecord.getKey().getBytes();
		System.arraycopy(key, 0, writeBuffer, 1, key.length);
		byte[] value = newRecord.getValue().getBytes();
		System.arraycopy(value, 0, writeBuffer, 1 + key.length, value.length);
		writeBuffer[writeBuffer.length - 1] = ENDER;
		store.put(newRecord.getIndex(), writeBuffer);
	}

}
