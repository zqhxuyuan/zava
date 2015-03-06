package com.ctriposs.tsdb;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.ctriposs.tsdb.common.Level;
import com.ctriposs.tsdb.iterator.MemSeekIterator;
import com.ctriposs.tsdb.iterator.SeekIteratorAdapter;
import com.ctriposs.tsdb.level.StoreLevel;
import com.ctriposs.tsdb.manage.FileManager;
import com.ctriposs.tsdb.manage.NameManager;
import com.ctriposs.tsdb.table.InternalKeyComparator;
import com.ctriposs.tsdb.table.MapFileLogReader;
import com.ctriposs.tsdb.table.MemTable;
import com.ctriposs.tsdb.util.FileUtil;

public class DBEngine implements IDB {

	/** The engine config*/
	private DBConfig config;
	
	/** The active memory table*/
	private MemTable memTable;
	
	/** Store memory table to file*/
	private StoreLevel storeLevel;
	
	/** Compact files */
	private Map<Integer, Level> compactLevelMap;
	
	/** Manage the file by time sequence */
	private FileManager fileManager;
	
	/** Map name to code for table and column*/
	private NameManager nameManager;
	
	/** The comparator for key*/
	private InternalKeyComparator internalKeyComparator;
	
	/** The memory table change lock. */
	private final Lock lock = new ReentrantLock();
	
	/** The hit counter. */
    private AtomicLong hitCounter = new AtomicLong();

	/** The miss counter. */
	private AtomicLong missCounter = new AtomicLong();

    /** The get counter. */
	private AtomicLong getCounter = new AtomicLong();

    /** The put counter. */
    private AtomicLong putCounter = new AtomicLong();

    /** The delete counter. */
    private AtomicLong deleteCounter = new AtomicLong();
    
	
	public DBEngine(DBConfig config) throws IOException {
		this.config = config;
		if(config.getInternalKeyComparator() == null){
			this.internalKeyComparator = new InternalKeyComparator();
		}else{
			this.internalKeyComparator = config.getInternalKeyComparator();
		}
		
		FileUtil.cleanDirectory(new File(config.getDBDir()));
		
		
		this.nameManager = new NameManager(config.getDBDir());
		this.fileManager = new FileManager(config.getDBDir(),config.getMaxPeriod(), internalKeyComparator, nameManager);
		
		this.memTable = new MemTable(config.getDBDir(), fileManager.getFileNumber(), config.getFileCapacity(), config.getMaxMemTableSize(), internalKeyComparator);
		this.storeLevel = new StoreLevel(fileManager, config.getStoreThread(), config.getMaxMemTable(), MemTable.MINUTE);
		this.compactLevelMap = new LinkedHashMap<Integer, Level>();
		this.storeLevel.start();

        //this.compactLevelMap.put(1, new CompactLevel(fileManager, this.storeLevel, 1, 4 * 60 * 1000, 1));
		//initialize compact level
		for(Entry<Integer,Level> entry : compactLevelMap.entrySet()){
			//entry.getValue().recoveryData();
			entry.getValue().start();
		}
		//this.fileManager.recoveryName();
		//this.storeLevel.recoveryData();
		
		//recoveryLog();
		
	}
	
	private void recoveryLog()throws IOException{
		List<File> files = FileUtil.listFiles(new File(fileManager.getStoreDir()), "log");
		
		for(File file:files){
			if(file.getPath().equals(memTable.getLogFile())){
				continue;
			}
			String name[] = file.getName().split("[-|.]");
			long fileNumber = Long.parseLong(name[1]);
			fileManager.upateFileNumber(fileNumber);
			ILogReader logReader = new MapFileLogReader(file,fileNumber,internalKeyComparator);
			try {
				MemTable memTable = logReader.getMemTable();
				memTable.close();
				logReader.close();
				if(!memTable.isEmpty()){
					storeLevel.addMemTable(memTable);
				}else{
					
					FileUtil.forceDelete(file);
					
				}
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
		
	}

	@Override
	public void put(String tableName, String colName, long time, byte[] value) throws IOException{
		
		putCounter.incrementAndGet();
		
		InternalKey key = new InternalKey(nameManager.getCode(tableName), nameManager.getCode(colName), time);
		if(!memTable.add(key, value)) {
			try {
				lock.lock();
				if(!memTable.add(key, value)){
					try {
						memTable.close();
						storeLevel.addMemTable(memTable);
						memTable = new MemTable(config.getDBDir(), fileManager.getFileNumber(), config.getFileCapacity(), config.getMaxMemTableSize(), internalKeyComparator);
						memTable.add(key, value);					
					} catch (Exception e) {
						throw new IOException(e);
					}
				}
			} finally {
				lock.unlock();
			}
		}
	}

	@Override
	public byte[] get(String tableName, String colName, long time) throws IOException {
		getCounter.incrementAndGet();
		
		InternalKey key = new InternalKey(nameManager.getCode(tableName), nameManager.getCode(colName), time);
		byte[] value = memTable.getValue(key);
		if(value == null) {
			value = storeLevel.getValue(key);
			if(value == null){
				for(Entry<Integer,Level> entry:compactLevelMap.entrySet()){
					value = entry.getValue().getValue(key);
					if(value != null){
						return value;
					}
				}
			}
		}

		return value;
	}

	@Override
	public void delete(long afterTime) throws IOException {
		deleteCounter.incrementAndGet();
		this.storeLevel.delete(afterTime);
		for(Entry<Integer,Level> entry:compactLevelMap.entrySet()){
			entry.getValue().delete(afterTime);
		}
	}
	
	@Override
	public ISeekIterator<InternalKey, byte[]> iterator() {

		SeekIteratorAdapter it = new SeekIteratorAdapter(fileManager, storeLevel.iterator());
		
		for(Entry<Integer,Level> entry:compactLevelMap.entrySet()){
			it.addIterator(entry.getValue().iterator());
		}
		
		for(MemSeekIterator memit:storeLevel.getAllMemSeekIterator()){
			it.addIterator(memit);
		}
		
		it.addIterator(memTable.iterator(fileManager));
		return it;
	}

	@Override
	public void close() throws IOException {
		nameManager.close();
		storeLevel.stop();
		
		for(Entry<Integer,Level> entry:compactLevelMap.entrySet()){
			entry.getValue().stop();
		}
	}

	public long getHitCounter() {
		return hitCounter.get();
	}

	public long getMissCounter() {
		return missCounter.get();
	}

	public long getGetCounter() {
		return getCounter.get();
	}

	public long getPutCounter() {
		return putCounter.get();
	}

	public long getDeleteCounter() {
		return deleteCounter.get();
	}

	public long getStoreCounter(int level){
		if(level == 0){
			return storeLevel.getStoreCounter();
		}else{
			Level sLevel = compactLevelMap.get(level);
			if(sLevel != null){
				return sLevel.getStoreCounter();
			}else{
				return 0;
			}
		}
	}
	
	public long getStoreErrorCounter(int level){

		if(level == 0){
			return storeLevel.getStoreErrorCounter();
		}else{
			Level sLevel = compactLevelMap.get(level);
			if(sLevel != null){
				return sLevel.getStoreErrorCounter();
			}else{
				return 0;
			}
		}
	}
}
