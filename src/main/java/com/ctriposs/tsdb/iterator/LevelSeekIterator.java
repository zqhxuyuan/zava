package com.ctriposs.tsdb.iterator;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListSet;

import com.ctriposs.tsdb.ISeekIterator;
import com.ctriposs.tsdb.InternalKey;
import com.ctriposs.tsdb.common.IFileIterator;
import com.ctriposs.tsdb.common.Level;
import com.ctriposs.tsdb.common.PureFileStorage;
import com.ctriposs.tsdb.manage.FileManager;
import com.ctriposs.tsdb.storage.FileMeta;
import com.ctriposs.tsdb.util.ByteUtil;

public class LevelSeekIterator implements ISeekIterator<InternalKey, byte[]> {

	private FileManager fileManager;
	private ConcurrentSkipListSet<IFileIterator<InternalKey, byte[]>> itSet;
	private Direction direction;
	private Entry<InternalKey, byte[]> curEntry;
	private IFileIterator<InternalKey, byte[]> curIt;
	private long curSeekTime;
	private InternalKey seekKey;
	private Level level;

	public LevelSeekIterator(FileManager fileManager, Level level) {
		this.fileManager = fileManager;
		this.level = level;
		this.direction = Direction.forward;
		this.curEntry = null;
		this.curIt = null;
		this.itSet = null;
		this.curSeekTime = 0;
	}

	@Override
	public boolean hasNext() {

		boolean result = false;
		if(curIt!=null&&curIt.hasNext()){
			result = true; 
		}else{
		
			if (itSet != null) {
				for (IFileIterator<InternalKey, byte[]> it : itSet) {
					if (it.hasNext()) {
						result = true;
						break;
					}
				}
			}
	
			if (!result) {
				curSeekTime += level.getLevelInterval();

				try {
					if(nextIterators(curSeekTime)){
					
						if (null != itSet) {
							for (IFileIterator<InternalKey, byte[]> it : itSet) {
									it.seek(seekKey.getCode(), curSeekTime);
							}
							findSmallest();
							direction = Direction.forward;
							if(curIt!=null&&curIt.hasNext()){
								result = true;
							}
						}
					}else{
						return false;
					}
				} catch (IOException e) {
					result = false;
					throw new RuntimeException(e);
				}
				
			}else{
				if(curIt != null){
					curIt.next();
				}
				findSmallest();
				if(curIt!=null&&curIt.hasNext()){
					result = true;
				}
			}
		}
		return result;
	}

	@Override
	public boolean hasPrev() {
		boolean result = false;
		if(curIt!=null&&curIt.hasPrev()){
			result = true;
		}else{
			if (itSet != null) {
				for (IFileIterator<InternalKey, byte[]> it : itSet) {
					if (it.hasPrev()) {
						result = true;
						break;
					}
				}
			}
	
			if (!result) {
				curSeekTime -= level.getLevelInterval();
				try {
					if(prevIterators(curSeekTime)){
						if (null != itSet) {
							for (IFileIterator<InternalKey, byte[]> it : itSet) {
									if(curEntry != null){
										it.seek(seekKey.getCode(), curEntry.getKey().getTime());
									}else{
										it.seek(seekKey.getCode(), curSeekTime);
									}
							}
							findLargest();
							direction = Direction.reverse;
							if(curIt!=null&&curIt.hasNext()){
								result = true;
							}
						}
					}else{
						return false;
					}
				} catch (IOException e) {
					result = false;
					throw new RuntimeException(e);
				}
				
			}else{
				if(curIt != null){
					curIt.prev();
				}
				findLargest();
				if(curIt!=null&&curIt.hasPrev()){
					result = true;
				}
			}
		}

		return result;
	}

	@Override
	public Entry<InternalKey, byte[]> next() {
		if (direction != Direction.forward) {
			for (IFileIterator<InternalKey, byte[]> it : itSet) {

				if (it != curIt) {
					try {
						if (it.hasNext()) {
							it.seek(seekKey.getCode(), curSeekTime);
						}
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
			findSmallest();
			direction = Direction.forward;
		}
		curEntry = curIt.next();
		findSmallest();
		return curEntry;
	}

	@Override
	public Entry<InternalKey, byte[]> prev() {
		if (direction != Direction.reverse) {
			if(itSet == null){
				try {
					nextIterators(curEntry.getKey().getTime());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			for (IFileIterator<InternalKey, byte[]> it : itSet) {
				if (curIt != it) {
					try {
						if (it.hasNext()) {
							it.seek(seekKey.getCode(), curSeekTime);
						}
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
			findLargest();
			direction = Direction.reverse;
		}
		curEntry = curIt.prev();
		findLargest();
		return curEntry;
	}

	@Override
	public void seek(String table, String column, long time) throws IOException {		
		int code = ByteUtil.ToInt(fileManager.getCode(table),fileManager.getCode(column));		
		seek(code, time);
	}
	

	@Override
	public void seek(int code, long time) throws IOException {
		
		seekKey = new InternalKey(code, time);

		if(!nextIterators(time)){
			prevIterators(time);
		}

		if (null != itSet) {
			for (IFileIterator<InternalKey, byte[]> it : itSet) {				
				it.seek(seekKey.getCode(), time);
			}
			findSmallest();
			curEntry = curIt.current();
			direction = Direction.forward;
		}
	}

	private void findSmallest() {
		if (null != itSet) {
			IFileIterator<InternalKey, byte[]> smallest = null;
			for (IFileIterator<InternalKey, byte[]> it : itSet) {
				if (it.valid()) {
					if (smallest == null) {
						smallest = it;
					} else if (fileManager.compare(smallest.key(), it.key()) > 0) {
						smallest = it;
					} else if (fileManager.compare(smallest.key(), it.key()) == 0) {
						//filter the same key after lower level
						while (it.hasNext()) {
							it.next();
							int diff = fileManager.compare(smallest.key(),it.key());
							if (0 == diff) {
								continue;
							} else {
								break;
							}
						}
					}
				}
			}
			if(smallest != null){
				curIt = smallest;
			}
		}
	}

	private void findLargest() {
		if (null != itSet) {
			IFileIterator<InternalKey, byte[]> largest = null;
			for (IFileIterator<InternalKey, byte[]> it : itSet) {
				if (it.valid()) {
					if (largest == null) {
						largest = it;
					} else if (fileManager.compare(largest.key(), it.key()) < 0) {
						largest = it;
					} else if (fileManager.compare(largest.key(), it.key()) == 0) {
						//filter the same key after lower level
						while (it.hasPrev()) {
							it.prev();
							int diff = fileManager.compare(largest.key(),it.key());
							if (0 == diff) {
								continue;
							} else {
								break;
							}
						}
					}
				}
			}
			if(largest != null){
				curIt = largest;
			}
		}
	}


	private ConcurrentSkipListSet<IFileIterator<InternalKey, byte[]>> getIterators(long time, boolean isNext) throws IOException {
		
		while (true) {
			Long nearTime = level.nearTime(time, isNext);
			if(nearTime == null){
				break;
			}else{
				curSeekTime = nearTime;
				ConcurrentSkipListSet<FileMeta> metaSet = level.getFiles(nearTime);
				if (metaSet != null && metaSet.size() > 0) {
					ConcurrentSkipListSet<IFileIterator<InternalKey, byte[]>> set = new ConcurrentSkipListSet<IFileIterator<InternalKey, byte[]>>(fileManager.getFileIteratorComparator());
					for (FileMeta meta : metaSet) {
						set.add(new FileSeekIterator(new PureFileStorage(meta.getFile()), meta.getFileNumber()));
					}
					return set;
				} 
			}
		}
		return null;
	}
	
	private boolean nextIterators(long time) throws IOException{
		ConcurrentSkipListSet<IFileIterator<InternalKey, byte[]>> set = getIterators(time,true);
		if(set != null){
			close();
			itSet = set;
			return true;
		}
		return false;
	}
	
	private boolean prevIterators(long time) throws IOException{
		ConcurrentSkipListSet<IFileIterator<InternalKey, byte[]>> set = getIterators(time,false);
		if(set != null){
			close();
			itSet = set;
			return true;
		}
		return false;
	}
	

	@Override
	public String table() {
		if (curEntry != null) {
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

		if (null != itSet) {
			for (IFileIterator<InternalKey, byte[]> it : itSet) {
				it.close();
			}
		}
	}

	@Override
	public InternalKey key() {
		if (curEntry != null) {
			return curEntry.getKey();
		}
		return null;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("unsupport remove operation!");
	}

	enum Direction {
		forward, reverse
	}

	@Override
	public long priority() {
		return level.getLevelNum();
	}


}
