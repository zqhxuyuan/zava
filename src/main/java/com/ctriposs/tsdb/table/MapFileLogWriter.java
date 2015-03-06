package com.ctriposs.tsdb.table;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import com.ctriposs.tsdb.ILogWriter;
import com.ctriposs.tsdb.common.IStorage;
import com.ctriposs.tsdb.common.MapFileStorage;
import com.ctriposs.tsdb.storage.FileName;
import com.ctriposs.tsdb.util.ByteUtil;

public class MapFileLogWriter implements ILogWriter {
	
	private IStorage storage;
	private AtomicInteger current;
	
	public MapFileLogWriter(String dir, long fileNumber, long capacity) throws IOException {
		this.current = new AtomicInteger(0);
		this.storage = new MapFileStorage(dir, System.currentTimeMillis(), FileName.logFileName(fileNumber), capacity);
	}
	
	public MapFileLogWriter(String dir, String fileName, long capacity) throws IOException {
		this.current = new AtomicInteger(0);
		this.storage = new MapFileStorage(dir, System.currentTimeMillis(), fileName, capacity);
	}
	
	public MapFileLogWriter(File file) throws IOException {
		this.current = new AtomicInteger(0);
		this.storage = new MapFileStorage(file);
	}

	@Override
	public void close() throws IOException {
		this.storage.close();
	}

	@Override
	public void add(int code, long time, byte[] value) throws IOException {
		int metaOffset = current.getAndAdd(16 + value.length);
		storage.put(metaOffset, ByteUtil.toBytes(code));
		storage.put(metaOffset + 4, ByteUtil.toBytes(time));
		storage.put(metaOffset + 12, ByteUtil.toBytes(value.length));
		storage.put(metaOffset + 16, value);
	}

    public boolean add(String name, short code) throws IOException {
        byte[] nameBytes = ByteUtil.ToBytes(name);
        int offset = current.getAndAdd(6 + nameBytes.length);
        if(current.get()>MemTable.MAX_MEM_SIZE){
        	return false;
        }
        storage.put(offset, ByteUtil.toBytes(code));
        storage.put(offset + 2, ByteUtil.toBytes(nameBytes.length));
        storage.put(offset + 6, nameBytes);
        return true;
    }
    
    
	@Override
	public String getName() {
		return storage.getName();
	}

	@Override
	public int getLength() {
		return current.get();
	}
}
