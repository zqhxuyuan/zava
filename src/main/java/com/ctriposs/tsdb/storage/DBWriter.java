package com.ctriposs.tsdb.storage;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import com.ctriposs.tsdb.InternalKey;
import com.ctriposs.tsdb.common.IStorage;

public class DBWriter {

	private IStorage storage;
	private long timeCount = 0;
	private long timeIndex = 0;
	private InternalKey smallest = null;
	private InternalKey largest = null;
	private AtomicLong valueOffset = null;
	private AtomicLong timeOffset = null;
	private long fileNumber;
	private Map<Integer,CodeItem> codeMap = new LinkedHashMap<Integer,CodeItem>();	
	
	public DBWriter(IStorage storage, long timeCount, long fileNumber) {
		this.storage = storage;
		this.timeCount = timeCount;
		this.valueOffset = new AtomicLong(Head.HEAD_SIZE + TimeItem.TIME_ITEM_SIZE * timeCount);
		this.timeOffset = new AtomicLong(Head.HEAD_SIZE);
		this.fileNumber = fileNumber;
	}
	
	public void add(InternalKey key, byte[] value)throws IOException {
		++timeIndex;
		if(timeIndex == 1) {
			smallest = key;
		}
		
		if(timeIndex > timeCount){
			throw new IOException("add item over timecount");
		}
		largest = key;
		
		// write time item
		long tOffset = timeOffset.getAndAdd(TimeItem.TIME_ITEM_SIZE);
		long vOffset = valueOffset.getAndAdd(value.length);
		storage.put(tOffset, key.toTimeItemByte(value.length, vOffset));
		// write value item
		storage.put(vOffset, value);
		//record start time offset when code change
		CodeItem codeItem = codeMap.get(key.getCode());
		if (codeItem == null) {
			codeItem = new CodeItem(key.getCode(), tOffset, key.getTime(), key.getTime());
			codeMap.put(key.getCode(), codeItem);
		}
		codeItem.addTimeItem(key.getTime());
	}
	
	private void writeCodeArea()throws IOException {
		for(Entry<Integer,CodeItem> entry: codeMap.entrySet()){
			long cOffset = valueOffset.getAndAdd(CodeItem.CODE_ITEM_SIZE);
			storage.put(cOffset, entry.getValue().toByte());
		}
	}
	
	private void writeHead(long codeOffset,int codeCount,long timeCount,InternalKey smallest,InternalKey largest)throws IOException {
		Head head = new Head(codeOffset, codeCount, timeCount, smallest, largest);
		storage.put(0, head.toByte());
	}
	
	public FileMeta close()throws IOException {
		long cOffset = valueOffset.get();
		writeCodeArea();
		writeHead(cOffset,codeMap.size(), timeIndex, smallest, largest);
		storage.close();
		return new FileMeta(fileNumber, new File(storage.getName()), smallest, largest);
	}
}
