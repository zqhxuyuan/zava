package com.ctriposs.tsdb.table;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.ctriposs.tsdb.ILogReader;
import com.ctriposs.tsdb.InternalKey;
import com.ctriposs.tsdb.common.IStorage;
import com.ctriposs.tsdb.common.MapFileStorage;
import com.ctriposs.tsdb.util.ByteUtil;

public class MapFileLogReader implements ILogReader{
	
	private IStorage storage;
	private AtomicInteger current;
	private InternalKeyComparator internalKeyComparator;
	private long fileNumber;
	private File file;
	
	public MapFileLogReader(File file, long fileNumber, InternalKeyComparator internalKeyComparator) throws IOException {
		this.current = new AtomicInteger(0);
		this.storage = new MapFileStorage(file);
		this.internalKeyComparator = internalKeyComparator;
		this.fileNumber = fileNumber;
		this.file = file;
	}

	@Override
	public void close() throws IOException {
		storage.close();
	}

	@Override
	public String getName() {
		return storage.getName();
	}
	
	@Override
	public MemTable getMemTable() throws IOException {
		MemTable memTable = new MemTable(file, fileNumber, internalKeyComparator);
		while(current.get() < file.length()) {
			byte[] bytes = new byte[16];
			storage.get(current.getAndAdd(16), bytes);
			int code = ByteUtil.ToInt(bytes, 0);
			if(code==0) {
				break;
			}
			long time = ByteUtil.ToLong(bytes, 4);
			int valueLen = ByteUtil.ToInt(bytes, 12);
			bytes = new byte[valueLen];
			storage.get(current.getAndAdd(valueLen), bytes);
			memTable.add(new InternalKey(code,time), bytes);
		}
		return memTable;
	}
	
	@Override
	public Map<String, Short> getNameMap() throws IOException {
		 Map<String, Short> map = new HashMap<String,Short>();
		 
			while(current.get()<file.length()){
				byte[] bytes = new byte[6];
				storage.get(current.getAndAdd(6), bytes);
				short code = ByteUtil.ToShort(bytes, 0);
				if(code ==0){
					break;
				}
				int nameLen = ByteUtil.ToInt(bytes, 2);
				bytes = new byte[nameLen];
				storage.get(current.getAndAdd(nameLen), bytes);
				map.put(new String(bytes), code);
			}
		return map;
	}

}
