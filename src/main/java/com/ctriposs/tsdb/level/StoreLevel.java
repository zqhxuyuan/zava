package com.ctriposs.tsdb.level;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.ctriposs.tsdb.InternalKey;
import com.ctriposs.tsdb.common.IStorage;
import com.ctriposs.tsdb.common.Level;
import com.ctriposs.tsdb.common.MapFileStorage;
import com.ctriposs.tsdb.common.PureFileStorage;
import com.ctriposs.tsdb.iterator.MemSeekIterator;
import com.ctriposs.tsdb.manage.FileManager;
import com.ctriposs.tsdb.storage.DBWriter;
import com.ctriposs.tsdb.storage.FileMeta;
import com.ctriposs.tsdb.storage.FileName;
import com.ctriposs.tsdb.table.MemTable;

public class StoreLevel extends Level {

	private ArrayBlockingQueue<MemTable> memQueue;
	protected AtomicInteger fileCount = new AtomicInteger(0);
	private AtomicLong storeCounter = new AtomicLong(0);
	private AtomicLong storeErrorCounter = new AtomicLong(0);

	public StoreLevel(FileManager fileManager, int threads, int memCount, long interval) {
		super(fileManager, 0, interval, threads);
		this.memQueue = new ArrayBlockingQueue<MemTable>(memCount);		
		
		for(int i = 0; i < threads; i++){
			tasks[i] = new MemTask(i);
		}
	}

	public void addMemTable(MemTable memTable) throws Exception {
		if(memTable != null) {
			this.memQueue.put(memTable);
		}
	}

	@Override
	public byte[] getValue(InternalKey key) throws IOException{
		byte[] value = null;

		ConcurrentSkipListSet<MemTable> tableSet = new ConcurrentSkipListSet<MemTable>(fileManager.getMemTableComparator());

		for(MemTable table : memQueue) {
			tableSet.add(table);
		}
		
		for(Task task: tasks) {
			MemTable table = task.getMemTable();
			if(table != null){
				tableSet.add(table);
			}
		}
		
		for(MemTable table : tableSet) {
			value = table.getValue(key);
			if(value != null){
				return value;
			}
		}
		
		return getValueFromFile(key);
	}
	
	public List<MemSeekIterator> getAllMemSeekIterator(){
		List<MemSeekIterator> list = new ArrayList<MemSeekIterator>();
		for(MemTable table : memQueue) {
			list.add(table.iterator(fileManager));
		}
		for(Task task: tasks) {
			MemTable table = task.getMemTable();
			if(table != null){
				list.add(table.iterator(fileManager));
			}
		}
		return list;
	}

	class MemTask extends Task {
		
		private MemTable table = null;
		private Lock lock;
		public MemTask(int num) {
			super(num);
			this.lock = new ReentrantLock();
		}

		@Override
		public byte[] getValue(InternalKey key) {
			try{
				lock.lock();
				if(table != null){
					return table.getValue(key);
				}else{
					return null;
				}
			}finally{
				lock.unlock();
			}
		}
		

		@Override
		public MemTable getMemTable() {
			try{
				lock.lock();
				return table;
			}finally{
				lock.unlock();
			}
		}

		@Override
		public void process() throws Exception {
			try{
				lock.lock();
				table = memQueue.poll();
				if(table == null) {
					return;
				}
			}finally{
				lock.unlock();
			}
			
			for (Entry<Long, ConcurrentSkipListMap<InternalKey, byte[]>> entry : table.getTable().entrySet()) {
				try{
					fileCount.incrementAndGet();
					FileMeta fileMeta = storeFile(entry.getKey(), entry.getValue(), table.getFileNumber());
					if(fileMeta != null){
						add(entry.getKey(), fileMeta);		
					}
					fileCount.decrementAndGet();
				}catch(IOException e){
					//TODO
					e.printStackTrace();
					storeErrorCounter.incrementAndGet();
				}						
			}

			fileManager.delete(new File(table.getLogFile()));
			
		}

		private FileMeta storeFile(Long time, ConcurrentSkipListMap<InternalKey, byte[]> dataMap, long fileNumber) throws IOException {
			IStorage storage;
			if(fileCount.get() < 8) {
				storage = new MapFileStorage(fileManager.getStoreDir(), time, FileName.dataFileName(fileNumber,level), FILE_SIZE);
			} else {
				storage = new PureFileStorage(fileManager.getStoreDir(), time, FileName.dataFileName(fileNumber,level), FILE_SIZE);
			}
			
			int size = dataMap.size();
			DBWriter dbWriter = new DBWriter(storage, size, fileNumber);
			for(Entry<InternalKey, byte[]> entry : dataMap.entrySet()){
				dbWriter.add(entry.getKey(), entry.getValue());
			}	
			
			FileMeta fileMeta = null;	
			try{
				fileMeta = dbWriter.close();	
			}catch(Throwable t){
				t.printStackTrace();
				incrementStoreError();
				closeStorages.add(storage);
			}
			return fileMeta;
		}


	}
	
	public long getStoreCounter(){
		return storeCounter.get();
	}
	
	public long getStoreErrorCounter(){
		return storeErrorCounter.get();
	}

	@Override
	public void incrementStoreError() {
		storeErrorCounter.incrementAndGet();
		
	}

	@Override
	public void incrementStoreCount() {
		storeCounter.incrementAndGet();
	}

}
