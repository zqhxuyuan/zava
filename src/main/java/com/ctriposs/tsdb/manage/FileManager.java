package com.ctriposs.tsdb.manage;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import com.ctriposs.tsdb.ILogReader;
import com.ctriposs.tsdb.ISeekIterator;
import com.ctriposs.tsdb.InternalKey;
import com.ctriposs.tsdb.common.IFileIterator;
import com.ctriposs.tsdb.storage.FileMeta;
import com.ctriposs.tsdb.table.InternalKeyComparator;
import com.ctriposs.tsdb.table.MapFileLogReader;
import com.ctriposs.tsdb.table.MemTable;
import com.ctriposs.tsdb.util.FileUtil;

public class FileManager {

	public final static long MAX_FILE_SIZE = 2*1024*1024*1024L;
	public final static int MAX_FILES = 30; 

	private String dir;
	private AtomicLong maxFileNumber = new AtomicLong(1L); 
	private InternalKeyComparator internalKeyComparator;
    private NameManager nameManager;
    private long maxPeriod; 
    private int levelNum = 1;
    
	private Comparator<FileMeta> fileMetaComparator = new Comparator<FileMeta>(){

		@Override
		public int compare(FileMeta o1, FileMeta o2) {
			
			return (int) (o2.getFileNumber() - o1.getFileNumber());
		}
		
	};
	
	private Comparator<ISeekIterator> iteratorComparator = new Comparator<ISeekIterator>(){

		@Override
		public int compare(ISeekIterator o1,ISeekIterator o2) {
			if(o2.priority() == o1.priority()){
				return 0;
			}else{
				if(o2.priority() < o1.priority()){
					return 1;
				}else{
					return -1;
				}
			}
		}
	};
	
	private Comparator<IFileIterator> fileIteratorComparator = new Comparator<IFileIterator>(){

		@Override
		public int compare(IFileIterator o1,IFileIterator o2) {
			
			if(o2.priority() == o1.priority()){
				return 0;
			}else{
				if(o2.priority() < o1.priority()){
					return -1;
				}else{
					return 1;
				}
			}
		}
	};
   
	private Comparator<MemTable> memTableComparator = new Comparator<MemTable>() {

		@Override
		public int compare(MemTable o1, MemTable o2) {
			
			return (int)(o2.getFileNumber()-o1.getFileNumber());
		}
	};
	
	
	public FileManager(String dir,long maxPeriod, InternalKeyComparator internalKeyComparator, NameManager nameManager){
		this.dir = dir;
		this.internalKeyComparator = internalKeyComparator;
		this.nameManager = nameManager;
		this.maxPeriod = maxPeriod;
	}
	
	public void setLevel(int levelNum){
		this.levelNum = levelNum;
	}
	
	public int getLevelNum(){
		return this.levelNum;
	}
	
	public int compare(InternalKey o1, InternalKey o2){
		return internalKeyComparator.compare(o1,o2);
	}

	public void delete(File file)throws IOException {
		FileUtil.forceDelete(file);
	}
	
	public String getStoreDir(){
		return dir;
	}
	
	public long getFileNumber(){
		return maxFileNumber.getAndIncrement();
	}
	
	public void upateFileNumber(long fileNumber){
		long l = maxFileNumber.get();
		if(fileNumber>l){
			maxFileNumber.set(fileNumber);
		}
	}
	
    public short getCode(String name) throws IOException {
        return nameManager.getCode(name);
    }

    public String getName(short code) {
        return nameManager.getName(code);
    }

    public InternalKeyComparator getInternalKeyComparator() {
        return internalKeyComparator;
    }

	public long getMaxPeriod() {
		return maxPeriod;
	}
	
	public void recoveryName()throws IOException {
		List<File> list = FileUtil.listFiles(new File(dir),"name");
		for(File file:list){
			ILogReader logReader = new MapFileLogReader(file,0,internalKeyComparator);
			boolean delete = false;
			for(Entry<String,Short>entry:logReader.getNameMap().entrySet()){
				nameManager.add(entry.getKey(), entry.getValue());
				delete = true;
			}
			logReader.close();
			if(delete){
				FileUtil.forceDelete(file);
			}
			
		}
	}
	
	public Comparator<FileMeta> getFileMetaComparator(){
		return fileMetaComparator;
	}

	public Comparator<ISeekIterator> getIteratorComparator(){
		return iteratorComparator;
	}
	
	public Comparator<IFileIterator> getFileIteratorComparator(){
		return fileIteratorComparator;
	}
	
	public Comparator<MemTable> getMemTableComparator(){
		return memTableComparator;
	}
}
