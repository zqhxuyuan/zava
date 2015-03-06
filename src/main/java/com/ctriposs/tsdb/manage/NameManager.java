package com.ctriposs.tsdb.manage;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.ctriposs.tsdb.storage.FileName;
import com.ctriposs.tsdb.table.MapFileLogWriter;
import com.ctriposs.tsdb.table.MemTable;
import com.ctriposs.tsdb.util.FileUtil;

public class NameManager {

	private Map<String,Short> nameMap = new ConcurrentHashMap<String,Short>();
	private Map<Short,String> codeMap = new ConcurrentHashMap<Short,String>();
	private Lock lock = new ReentrantLock();
	private AtomicInteger maxCode = new AtomicInteger(0);
    private MapFileLogWriter fileWriter;
    private boolean hasData = false;
    private String dir;

	public NameManager(String dir) throws IOException {
		this.dir = dir;
        this.fileWriter = new MapFileLogWriter( dir, FileName.nameFileName(0),  MemTable.MAX_MEM_SIZE) ;
	}
	
	public short getCode(String name) throws IOException {
		Short code = nameMap.get(name);
		if(code == null){
			try{
				lock.lock();
				code = nameMap.get(name);
				if(code==null){
					code = (short) maxCode.incrementAndGet();
					nameMap.put(name, code);
					codeMap.put(code, name);  
					if(!fileWriter.add(name, code)){
						fileWriter.close();
						fileWriter = new MapFileLogWriter( dir, FileName.nameFileName(0),  MemTable.MAX_MEM_SIZE) ;	
						fileWriter.add(name, code);
					}
					hasData = true;
				}
			} finally {
				lock.unlock();
			}
		}

		return code;
	}
	
	public String getName(short code){
		return codeMap.get(code);
	}
	
	public void add(String name,short code){
		nameMap.put(name, code);
		codeMap.put(code, name);  
		maxCode.incrementAndGet();
	}
	
	public void close() throws IOException{
		fileWriter.close();
		if(!hasData){
			FileUtil.forceDelete(new File(fileWriter.getName()));
		}
	}
}
