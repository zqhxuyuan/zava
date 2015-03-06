package com.ctriposs.tsdb.table;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.ctriposs.tsdb.ILogWriter;
import com.ctriposs.tsdb.InternalKey;
import com.ctriposs.tsdb.iterator.MemSeekIterator;
import com.ctriposs.tsdb.manage.FileManager;
import com.ctriposs.tsdb.storage.CodeItem;
import com.ctriposs.tsdb.storage.Head;
import com.ctriposs.tsdb.storage.TimeItem;

public class MemTable {

	public final static long MAX_MEM_SIZE = 256 * 1024 * 1024L;
	public final static long MINUTE = 1000 * 60;

	private final ConcurrentHashMap<Long, ConcurrentSkipListMap<InternalKey, byte[]>> table;
	private final long maxMemTableSize;
	private final AtomicLong used = new AtomicLong(Head.HEAD_SIZE);
	private Lock lock = new ReentrantLock();
	private InternalKeyComparator internalKeyComparator;
	private ILogWriter logWriter;
	private long fileNumber;
	
	public MemTable(String dir, long fileNumber, long capacity, long maxMemTableSize, InternalKeyComparator internalKeyComparator) throws IOException {
		this.table = new ConcurrentHashMap<Long, ConcurrentSkipListMap<InternalKey, byte[]>>();
		this.maxMemTableSize = maxMemTableSize;
		this.internalKeyComparator = internalKeyComparator;
		this.logWriter = new MapFileLogWriter(dir, fileNumber, capacity);
		this.fileNumber = fileNumber;
	}
	
	public MemTable(File file, long fileNumber, InternalKeyComparator internalKeyComparator) throws IOException {
		this.table = new ConcurrentHashMap<Long, ConcurrentSkipListMap<InternalKey, byte[]>>();
		this.maxMemTableSize = MAX_MEM_SIZE;
		this.internalKeyComparator = internalKeyComparator;
		this.logWriter = new MapFileLogWriter(file);
		this.fileNumber = fileNumber;
	}

	public boolean isEmpty() {
		return table.isEmpty();
	}

	public long getUsed() {
		return used.get();
	}

	public static long format(long time) {
		return time/MINUTE*MINUTE;
	}

	public boolean add(InternalKey key, byte value[]) throws IOException {
		boolean result = true;

		int length = value.length + CodeItem.CODE_ITEM_SIZE + TimeItem.TIME_ITEM_SIZE;
		if (used.addAndGet(length) > maxMemTableSize) {
			result = false;
		} else {
			long ts = format(key.getTime());
			ConcurrentSkipListMap<InternalKey, byte[]> slot = table.get(ts);
					
			if(slot == null) {
				try {
					lock.lock();
					slot = table.get(ts);
					if(slot == null) {
						slot = new ConcurrentSkipListMap<InternalKey, byte[]>(internalKeyComparator);
						table.put(ts, slot);
					}
				} finally {
					lock.unlock();
				}
			}
			logWriter.add(key.getCode(), key.getTime(), value);
			slot.put(key, value);
		}

		return result;
	}
	
	public byte[] getValue(InternalKey key){
		long ts = format(key.getTime());
		ConcurrentSkipListMap<InternalKey, byte[]> slot = table.get(ts);
		if(slot != null) {
			return slot.get(key);
		} else {
			return null;
		}
	}
	
	public ConcurrentHashMap<Long, ConcurrentSkipListMap<InternalKey, byte[]>> getTable(){
		return this.table;
	}
	
	public ConcurrentSkipListMap<InternalKey, byte[]> getAllConcurrentSkipList(){
		ConcurrentSkipListMap<InternalKey, byte[]> result = new ConcurrentSkipListMap<InternalKey, byte[]>(internalKeyComparator);
		
		for(ConcurrentSkipListMap<InternalKey, byte[]> value:table.values()){
			result.putAll(value);
		}
		
		return result;
	}

	public void close() throws IOException{
		logWriter.close();
	}
	
	public String getLogFile(){
		return logWriter.getName();
	}
	
	public long getFileNumber(){
		return fileNumber;
	}
	
	public MemSeekIterator iterator(FileManager fileManager){
		return new MemSeekIterator(fileManager, this,-fileNumber);
	}
}
