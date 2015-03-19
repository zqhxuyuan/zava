/**
 * 
 */
package com.github.seanlinwang.fkv;

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

    //一条记录的格式是:标记位|key|value|分隔符
    private int keyLength;
    private int valueLength;
    private int recordLength;
    private int maxRecordSize;

    //字节数组和store的实现类FkvFileStore是息息相关的.因为最终是将buffer的数据写到文件中
    private FkvStore store;
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
        //put时如果缓存里没有,则新增并放到缓存里.如果缓存里已经有,则更新!
        try {
            writeLock.lock();
            Record record = this.activeCache.get(key);
            if (record == null) {  //新增
                putNewRecord(key, value);
            } else { //更新
                record.setValue(value);
                //定位到这条记录的offset,然后再定位到value的开始位置,覆盖value数据
                this.store.put(record.getIndex() + STATUS_LENGTH + keyLength, value.getBytes());
            }
        } finally {
            writeLock.unlock();
        }
    }

    private void putNewRecord(String key, String value) {
        int index;
        // no deleted record 没有要删除的记录,直接追加在文件末尾, 否则先写在删除的位置.
        if (this.deletedCache.isEmpty()) {
            index = endIndex;
            endIndex += this.recordLength;
        } else {
            //为什么可以先在delete的位置开始覆盖数据(尽管key不相同)?因为key,value的长度都是固定的!
            //被删除的数据占用的空间和要写入的数据的空间是一样的,所以可以直接覆盖掉被标记为删除的记录.
            Record deletedRecord = this.deletedCache.pop();
            index = deletedRecord.getIndex();
        }
        // 一条新记录的诞生,经过了create, store, cache三个过程: 构造新对象,存储新对象,索引新对象
        Record newRecord = createNewRecord(key, value, index);
        storeNewRecord(newRecord); // first store record
        cacheNewRecord(key, newRecord); // second cache record
    }

    /**
     * 创建一条记录
     * @param key
     * @param value
     * @param index 实际上是Record在文件中的offset
     * @return
     */
    private Record createNewRecord(String key, String value, int index) {
        Record newRecord = new Record();
        newRecord.setValue(value);
        newRecord.setKey(key);
        newRecord.setIndex(index);
        return newRecord;
    }

    // 写到文件里
    private void storeNewRecord(Record newRecord) {
        //每条记录的第一个字节是标记位
        writeBuffer[0] = STATUS_ACTIVE;
        byte[] key = newRecord.getKey().getBytes();
        //复制新创建的记录的key到writeBuffer的第二个字节(索引从0开始)开始
        System.arraycopy(key, 0, writeBuffer, 1, key.length);
        byte[] value = newRecord.getValue().getBytes();
        //复制value到writeBuffer中
        System.arraycopy(value, 0, writeBuffer, 1 + key.length, value.length);
        //writeBuffer的长度=this.recordLength,在初始化时指定
        writeBuffer[writeBuffer.length - 1] = ENDER;
        //放进FileStore里
        store.put(newRecord.getIndex(), writeBuffer);
    }

    // 放进缓存里
    private void cacheNewRecord(String key, Record newRecord) {
        this.activeCache.put(key, newRecord);
    }

    @Override
    public int size() {
        return this.activeCache.size();
    }

	@Override
	public void delete(String key) {
		try {
			writeLock.lock();
			Record r = this.activeCache.get(key);
			if (r != null) {
                //从active map中移出
				Record deletedRecord = this.activeCache.remove(key);
                //添加到delete queue中
				this.deletedCache.add(deletedRecord);
                //更新store file的标记: 定位到文件的index位置,覆盖这个位置的数据
				this.store.put(deletedRecord.getIndex(), STATUS_DELETE);
			}
		} finally {
			writeLock.unlock();
		}
	}

    // 反序列化. 在构造函数时调用. 还原数据到activeCache和deletedCache中
	protected void deserial() {
		if (this.store.isNeedDeserial()) {
			byte[] recordBuf = new byte[recordLength];
			int index = 0;
            // 循环每条记录
			while (this.store.remaining() > 0) {
				store.get(recordBuf);
				if (isValidRecord(recordBuf)) {
					byte[] keyBuf = new byte[keyLength];
					System.arraycopy(recordBuf, STATUS_LENGTH, keyBuf, 0, keyLength);
					byte[] valueBuf = new byte[valueLength];
					System.arraycopy(recordBuf, STATUS_LENGTH + keyLength, valueBuf, 0, valueLength);
                    // 还原文件里的记录
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

    public Record getRecord(String key) {
        Record record = null;
        record = this.activeCache.get(key);
        return record;
    }

	@Override
	public String get(String key) {
        //获取数据时,直接从缓存中获取
		Record record = this.activeCache.get(key);
        //如果缓存中没有,那就是没有了.因为在put的时候有放进去,一定放到缓存里.不在缓存里的,一定没有put过
		if (record == null) return null;
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

    // 判断是否有效: 标记位必须是0或1, 最后一个字节必须是分隔符\n
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
    public void close() throws IOException {
        this.store.close();
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
}
