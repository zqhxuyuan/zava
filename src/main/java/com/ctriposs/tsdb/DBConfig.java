package com.ctriposs.tsdb;

import java.io.File;

import com.ctriposs.tsdb.level.CompactLevel;
import com.ctriposs.tsdb.level.StoreLevel;
import com.ctriposs.tsdb.table.InternalKeyComparator;
import com.ctriposs.tsdb.table.MemTable;
import com.ctriposs.tsdb.util.FileUtil;
import com.google.common.base.Preconditions;

public class DBConfig {
	public static final int BLOCK_MAX_COUNT = 200;
	
	private int maxMemTable = StoreLevel.MAX_SIZE;
	private int storeThread = StoreLevel.THREAD_COUNT;
    private long maxMemTableSize = MemTable.MAX_MEM_SIZE;
    private long maxPeriod = CompactLevel.MAX_PERIOD;
    private long fileCapacity = StoreLevel.FILE_SIZE;
    private InternalKeyComparator internalKeyComparator = new InternalKeyComparator();
	private String dir = null;	
	
	public DBConfig(String dir){
    	Preconditions.checkNotNull(dir, "storage data directory is null!");
       
		if (!dir.endsWith(File.separator)) {
			dir += File.separator;
		}
		// validate directory
		if (!FileUtil.isFilenameValid(dir)) {
			throw new IllegalArgumentException("Invalid storage data directory : " + dir);
		}

		this.dir = dir;
	}

	public int getMaxMemTable() {
		return maxMemTable;
	}

	public void setMaxMemTable(int maxMemTable) {
		this.maxMemTable = maxMemTable;
	}

	public int getStoreThread() {
		return storeThread;
	}

	public void setStoreThread(int storeThread) {
		this.storeThread = storeThread;
	}

	public long getMaxMemTableSize() {
		return maxMemTableSize;
	}

	public void setMaxMemTableSize(long maxMemTableSize) {
		this.maxMemTableSize = maxMemTableSize;
	}

	public long getMaxPeriod() {
		return maxPeriod;
	}

	public void setMaxPeriod(long maxPeriod) {
		this.maxPeriod = maxPeriod;
	}

	public String getDBDir() {
		return dir;
	}

	public long getFileCapacity() {
		return fileCapacity;
	}

	public void setFileCapacity(long fileCapacity) {
		this.fileCapacity = fileCapacity;
	}

	public InternalKeyComparator getInternalKeyComparator() {
		return internalKeyComparator;
	}

	public void setInternalKeyComparator(InternalKeyComparator internalKeyComparator) {
		this.internalKeyComparator = internalKeyComparator;
	}

}
