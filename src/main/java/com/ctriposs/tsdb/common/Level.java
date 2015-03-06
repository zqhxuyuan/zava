package com.ctriposs.tsdb.common;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.ctriposs.tsdb.InternalKey;
import com.ctriposs.tsdb.iterator.FileSeekIterator;
import com.ctriposs.tsdb.iterator.LevelSeekIterator;
import com.ctriposs.tsdb.manage.FileManager;
import com.ctriposs.tsdb.storage.FileMeta;
import com.ctriposs.tsdb.storage.Head;
import com.ctriposs.tsdb.table.MemTable;
import com.ctriposs.tsdb.util.FileUtil;

public abstract class Level {

	public final static int MAX_SIZE = 3;
	public final static long FILE_SIZE = 256 * 1024 * 1024L;
	public final static int THREAD_COUNT = 3;
	
	protected ExecutorService executor = Executors.newFixedThreadPool(2);
	protected Task[] tasks;
	
	protected volatile boolean run = false;
	protected FileManager fileManager;
	protected int level;
	protected long interval;

    /** The list change lock. */
    private final Lock changeLock = new ReentrantLock();
    
    protected final ArrayBlockingQueue<File> deleteFiles = new ArrayBlockingQueue<File>(60);
    protected final ArrayBlockingQueue<IStorage> closeStorages = new ArrayBlockingQueue<IStorage>(60);
    protected final ArrayBlockingQueue<IFileIterator<InternalKey, byte[]>> closeIterators = new ArrayBlockingQueue<IFileIterator<InternalKey, byte[]>>(60);

    protected ConcurrentSkipListMap<Long, ConcurrentSkipListSet<FileMeta>> timeFileMap = new ConcurrentSkipListMap<Long, ConcurrentSkipListSet<FileMeta>>(new Comparator<Long>() {
        @Override
        public int compare(Long o1, Long o2) {
            return (int) (o1 - o2);
        }
    });

	public Level(FileManager fileManager, int level, long interval, int threads) {
		this.fileManager = fileManager;
		this.level = level;
		this.interval = interval;
		this.tasks = new Task[threads];
	}
	
	public void recoveryData() throws IOException {
		List<File> list = FileUtil.listFiles(new File(fileManager.getStoreDir()), level + "-dat");
		
		for(File file:list){
			IStorage storage = new MapFileStorage(file);
			byte[] bytes = new byte[Head.HEAD_SIZE];
			storage.get(0, bytes);
			Head head = new Head(bytes);
			if(head.getCodeCount() == 0||head.getTimeCount() == 0){
				file.renameTo(new File(file.getPath()+".backup"));
				continue;
			}
			String name[] = file.getName().split("[-|.]");
			long time = Long.parseLong(name[0]);
			long fileNumber = Long.parseLong(name[1]);
			FileMeta fileMeta = new FileMeta(fileNumber, file, head.getSmallest(),head.getLargest());
			add(time, fileMeta);
			storage.close();
			fileManager.upateFileNumber(fileNumber);
		}

	}
	
	public void start() {
		if(!run) {
            run = true;
            for (Task task : tasks) {
                executor.submit(task);
            }
		}
	}

	public void stop() {
		if(run) {
			run = false;
			executor.shutdownNow();
		}
	}

	public LevelSeekIterator iterator(){
		return new LevelSeekIterator(fileManager, this);
	}
	
	public void add(long time, FileMeta fileMeta) {
		ConcurrentSkipListSet<FileMeta> list = timeFileMap.get(time);
		if(list == null) {
			try{
				changeLock.lock();
				list = timeFileMap.get(time);
				if(list == null) {
					list = new ConcurrentSkipListSet<FileMeta>(fileManager.getFileMetaComparator());
					list.add(fileMeta);
					timeFileMap.put(time, list);
				}else{
					list.add(fileMeta);
				}
			} finally {
				changeLock.unlock();
			}
		}else{
			list.add(fileMeta);
		}
		
	}
	
	public void delete(long time, List<FileMeta> fileMetaList) {
		ConcurrentSkipListSet<FileMeta> list = timeFileMap.get(time);
		if(list != null) {
			for(FileMeta fileMeta:fileMetaList){
				list.remove(fileMeta);
			}
		}
	}
	
	public ConcurrentSkipListSet<FileMeta> getFiles(long time){
		return timeFileMap.get(format(time, interval));
	}
	
	
	public Long nearTime(long time,boolean isNext){
		time = format(time);
		ConcurrentSkipListSet<FileMeta> fileMetas = timeFileMap.get(time);
		
		if(fileMetas == null){
			Long nearTime = null;
			if(isNext){
				nearTime = timeFileMap.higherKey(time);
			}else{
				nearTime = timeFileMap.lowerKey(time);
			}
			if(nearTime != null){
				time = nearTime;
			}else{
				return null;
			}
		}
		return time;
	}
	
	public int getFileSize(){
		return timeFileMap.size();
	}

	public long format(long time, long interval) {
		return time/interval*interval;
	}
	
	public long format(long time) {
		return time/interval*interval;
	}
	
    public ConcurrentSkipListMap<Long, ConcurrentSkipListSet<FileMeta>> getTimeFileMap() {
        return timeFileMap;
    }
	
    public int getLevelNum(){
    	return level;
    }

    public long getLevelInterval() {
        return interval;
    }

	public void delete(long afterTime) throws IOException {
		for (Entry<Long, ConcurrentSkipListSet<FileMeta>> entry : timeFileMap.entrySet()) {
			if (entry.getKey() < afterTime) {
				ConcurrentSkipListSet<FileMeta> list = timeFileMap.remove(entry
						.getKey());
				for (FileMeta meta : list) {
					try {
						fileManager.delete(meta.getFile());
						list.remove(meta);
					} catch (IOException e) {
						deleteFiles.add(meta.getFile());
						throw e;
					}
				}
			}
		}
	}
	
	protected byte[] getValueFromFile(InternalKey key)throws IOException{
		long ts = key.getTime();
		ConcurrentSkipListSet<FileMeta> list = getFiles(format(ts, interval));
		Map<FileSeekIterator,IStorage> itSet = new HashMap<FileSeekIterator,IStorage>();
		try{

			if(list != null) {
				for(FileMeta fileMeta : list) {
					if(fileMeta.contains(key)){
						IStorage storage = new PureFileStorage(fileMeta.getFile());
						FileSeekIterator it = new FileSeekIterator(storage);
						itSet.put(it, storage);
						it.seek(key.getCode(),key.getTime());
	
						while(it.hasNext()){
							int diff = fileManager.compare(key,it.key());
							if(0==diff){
								return it.value();
							}else if(diff < 0){
								break;
							}
							it.next();
						}
					}
				}
			}
		}finally{
			for(Entry<FileSeekIterator,IStorage> entry :itSet.entrySet()){
				try{
					entry.getKey().close();
				}catch(Throwable t){
					closeStorages.add(entry.getValue());
					t.printStackTrace();
					incrementStoreError();
				}
			}
		}
		return null;
	}
	
	public abstract class Task implements Runnable {

		protected int num;

		public Task(int num) {
			this.num = num;
		}
		
		@Override
		public void run() {
			while(run) {
				try {
					incrementStoreCount();
					purgeFiles();
					clear();
					process();
					Thread.sleep(500);
				} catch (Throwable e) {
					//TODO
					e.printStackTrace();
					incrementStoreError();
				}
			}
		}
		
		private void purgeFiles(){
			//close iterators
			IFileIterator<InternalKey, byte[]> iterator = closeIterators.poll();
			if(iterator != null){
				try{
					iterator.close();
				}catch(Throwable t){
					t.printStackTrace();
					incrementStoreError();
					closeIterators.add(iterator);
				}
			}
			//close storages
			IStorage storage = closeStorages.poll();
			if(storage != null){
				try{
					storage.close();
				}catch(Throwable t){
					t.printStackTrace();
					incrementStoreError();
					closeStorages.add(storage);
				}
			}
			//delete files
			File file = deleteFiles.poll();
			if(file != null){		
				try{
					FileUtil.forceDelete(file);
				}catch(Throwable t){
					t.printStackTrace();
					incrementStoreError();
					deleteFiles.add(file);
				}
			}
		}
		
		private void clear(){
			for(Entry<Long, ConcurrentSkipListSet<FileMeta>> entry:timeFileMap.entrySet()){
				long time = entry.getKey();
				if (time % tasks.length == num) {
					ConcurrentSkipListSet<FileMeta> list = entry.getValue();
					if(list.size()==0){
						try{
							changeLock.lock();
							list = timeFileMap.get(time);
							if(list != null) {
								if(list.size()==0){
									timeFileMap.remove(time, list);
								}
							}
						} finally {
							changeLock.unlock();
						}
					}
				}
			}
		}

        public abstract byte[] getValue(InternalKey key);
        
        public abstract MemTable getMemTable();

		public abstract void process() throws Exception;

	}
	
	public abstract void incrementStoreError();
	
	public abstract void incrementStoreCount();
	
	public abstract long getStoreErrorCounter();
	
	public abstract long getStoreCounter();

	public abstract byte[] getValue(InternalKey key) throws IOException;

}
