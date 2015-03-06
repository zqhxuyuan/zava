package com.ctriposs.tsdb.iterator;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import com.ctriposs.tsdb.ISeekIterator;
import com.ctriposs.tsdb.InternalKey;
import com.ctriposs.tsdb.manage.FileManager;
import com.ctriposs.tsdb.table.MemTable;
import com.ctriposs.tsdb.util.ByteUtil;

public class MemSeekIterator implements ISeekIterator<InternalKey, byte[]> {
	
	private ConcurrentSkipListMap<InternalKey, byte[]> dataMap;
	private Iterator<Entry<InternalKey, byte[]>> curSeeIterator;
	private Entry<InternalKey, byte[]> curEntry;
	private InternalKey curSeekKey;
	private FileManager fileManager;
	private long fileNumber;
	public MemSeekIterator(FileManager fileManager,MemTable memTable,long fileNumber){
		this.dataMap = memTable.getAllConcurrentSkipList();
		this.fileManager = fileManager;
		this.fileNumber = fileNumber;
		this.curSeeIterator = null;
		this.curSeekKey = null;
		this.curEntry = null;
	}

	@Override
	public boolean hasNext() {

		if(curEntry != null){
			if(curEntry.getKey().getCode() ==  curSeekKey.getCode()){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean hasPrev() {
		if(curEntry != null){
			if(curEntry.getKey().getCode() ==  curSeekKey.getCode()){
				return true;
			}
		}
		return false;
	}

	@Override
	public Entry<InternalKey, byte[]> next() {

		Entry<InternalKey, byte[]> entry = curEntry;
		if(curSeeIterator != null){
			if(curSeeIterator.hasNext()){
				curEntry = curSeeIterator.next();
			}else{
				curEntry = null;
			}			
			
		}
		return entry;
	}

	@Override
	public Entry<InternalKey, byte[]> prev() {
		Entry<InternalKey, byte[]> entry = curEntry;
		if(curSeeIterator != null){
			if(curEntry != null){
				curEntry = dataMap.lowerEntry(curEntry.getKey());
			}
		}
		return entry;

	}

	@Override
	public void seek(String table, String column, long time) throws IOException {
		int code = ByteUtil.ToInt(fileManager.getCode(table),fileManager.getCode(column));		
		seek(code, time);
	}
	
	@Override	
	public void seek(int code, long time) throws IOException {

		curSeekKey = new InternalKey(code, time);		
		Entry<InternalKey, byte[]> entry = dataMap.lowerEntry(curSeekKey);
		ConcurrentNavigableMap<InternalKey, byte[]> subMap = null;
		if(entry == null){
			subMap = dataMap.subMap(curSeekKey, new InternalKey(code, Long.MAX_VALUE));
		}else{
			subMap = dataMap.subMap(entry.getKey(), new InternalKey(code, Long.MAX_VALUE));
		}
		
		curSeeIterator = subMap.entrySet().iterator();
		curEntry = null;
		while(curSeeIterator.hasNext()){
			curEntry = curSeeIterator.next();
			if(curEntry.getKey().getCode()==code){
				break;
			}
		}
		
		if(curEntry != null){
			if(curEntry.getKey().getCode()!=code){
				curEntry = null;
			}
		}
	}
	
	
	@Override
	public String table() {
		if(curEntry != null){
			return fileManager.getName(curEntry.getKey().getTableCode());
		}
		return null;
	}

	@Override
	public String column() {

		if (curEntry != null) {
			return fileManager.getName(curEntry.getKey().getColumnCode());
		}
		return null;
	}

	@Override
	public InternalKey key() {

		if (curEntry != null) {
			return curEntry.getKey();
		}
		return null;
	}

	@Override
	public long time() {
		if (curEntry != null) {
			return curEntry.getKey().getTime();
		}
		return 0;
	}

	@Override
	public byte[] value() throws IOException {
		if (curEntry != null) {
			return curEntry.getValue();
		}

		return null;
	}

	@Override
	public boolean valid() {
		if (curEntry == null) {
			return false;
		} else {
			return true;
		}
	}


	@Override
	public void close() throws IOException {
		dataMap = null;
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException("unsupport remove operation!");
	}

	@Override
	public long priority() {
		return fileNumber;
	}

}
