package com.ctriposs.tsdb.iterator;

import java.io.IOException;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListSet;

import com.ctriposs.tsdb.ISeekIterator;
import com.ctriposs.tsdb.InternalKey;
import com.ctriposs.tsdb.manage.FileManager;
import com.ctriposs.tsdb.util.ByteUtil;

public class SeekIteratorAdapter implements ISeekIterator<InternalKey, byte[]>{
	
	private ConcurrentSkipListSet<ISeekIterator<InternalKey, byte[]>> itSet;
	private Direction direction;
	private ISeekIterator<InternalKey, byte[]> curIt;
	private FileManager fileManager;
	private long curSeekTime;
	private int curSeekCode;
	
	public SeekIteratorAdapter(FileManager fileManager, ISeekIterator<InternalKey, byte[]>... its) {

		this.itSet = new ConcurrentSkipListSet<ISeekIterator<InternalKey, byte[]>>(fileManager.getIteratorComparator());
		addIterator(its);
		this.fileManager = fileManager;
		this.direction = Direction.forward;
		this.curIt = null;
	}
	
	public void addIterator(ISeekIterator<InternalKey, byte[]>... its) {
		 Collections.addAll(itSet, its);
	}


	@Override
	public boolean hasNext() {

		boolean result = false;
		if(curIt!=null&&curIt.hasNext()){
			result = true;
		}else{
			if(curIt != null){
				curIt.next();
			}
			
			if(itSet != null) {
				for (ISeekIterator<InternalKey, byte[]> it : itSet) {
					if(it.hasNext()) {
						result = true;
	                    break;
					}
				}
			}
			if(result){
				findSmallest();
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
			if(itSet != null) {
				for (ISeekIterator<InternalKey, byte[]> it : itSet) {
					if(it.hasPrev()) {
						result = true;
	                    break;
					}
				}
			}
			if(result){
				findLargest();
			}
		}

		return result;
	}

	@Override
	public Entry<InternalKey, byte[]> next() {
		if (direction != Direction.forward) {
			for (ISeekIterator<InternalKey, byte[]> it : itSet) {

				if (it != curIt) {
					try {
						it.seek(curSeekCode,curSeekTime);						
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
			findSmallest();
			direction = Direction.forward;
		}
		Entry<InternalKey, byte[]> entry = curIt.next();
		findSmallest();
		if(entry != null){
			curSeekTime = entry.getKey().getTime();
		}
		return entry;
	}

	@Override
	public Entry<InternalKey, byte[]> prev() {
		if(direction != Direction.reverse){
			for(ISeekIterator<InternalKey, byte[]> it:itSet){
				if(curIt != it){
					try {
						it.seek(curSeekCode,curSeekTime);						
					} catch (IOException e) {			
						throw new RuntimeException(e);
					}
				}
			}
			findLargest();
			direction = Direction.reverse;
		}
		Entry<InternalKey, byte[]> entry = curIt.prev();
		findLargest();
		if(entry != null){
			curSeekTime = entry.getKey().getTime();
		}
		return entry;		
	}

	
	@Override
	public void seek(String table, String column, long time) throws IOException {
		seek(ByteUtil.ToInt(fileManager.getCode(table),fileManager.getCode(column)), time);
	}
	
	@Override
	public void seek(int code, long time) throws IOException {
		this.curSeekCode = code;
		this.curSeekTime = time;
		
		if(!itSet.isEmpty()){
			for(ISeekIterator<InternalKey, byte[]> it:itSet){
				it.seek(curSeekCode,time);
			}		
			findSmallest();
			direction = Direction.forward;
		}
		
	}
	
	private void findSmallest(){
		if(!itSet.isEmpty()){
			ISeekIterator<InternalKey, byte[]> smallest = null;
			for(ISeekIterator<InternalKey, byte[]> it:itSet){
				if(it.valid()){
					if(smallest == null){
						smallest = it;
					}else if(fileManager.compare(smallest.key(), it.key())>0){
						smallest = it;
					}else if(fileManager.compare(smallest.key(), it.key())==0){
						while(it.hasNext()){
							it.next();
							int diff = fileManager.compare(smallest.key(),it.key());
							if(0==diff){
								continue;
							}else{
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
	
	private void findLargest(){
		if(!itSet.isEmpty()){
			ISeekIterator<InternalKey, byte[]> largest = null;
			for(ISeekIterator<InternalKey, byte[]> it:itSet){
				if(it.valid()){
					if(largest == null){
						largest = it;
					}else if(fileManager.compare(largest.key(), it.key())<0){
						largest = it;
					}else if(fileManager.compare(largest.key(), it.key())==0){
						while(it.hasPrev()){
							it.prev();
							int diff = fileManager.compare(largest.key(),it.key());
							if(0==diff){
								continue;
							}else{
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

	@Override
	public String table() {
		if(curIt != null){
			return curIt.table();
		}
		return null;
	}

	@Override
	public String column() {
		if(curIt != null){
			return curIt.column();
		}
		return null;
	}

	@Override
	public long time() {
		if(curIt != null){
			return curIt.key().getTime();
		}
		return 0;
	}

	@Override
	public byte[] value() throws IOException {
		if(curIt != null){
			return curIt.value();
		}
		return null;
	}

	@Override
	public boolean valid() {
		if(curIt==null){
			return false;
		}else{
			return curIt.valid();
		}
	}

	@Override
	public void close() throws IOException{
		
		if(!itSet.isEmpty()){
			for(ISeekIterator<InternalKey, byte[]> it:itSet){
				it.close();
			}
		}
	}

	@Override
	public InternalKey key() {
		if(curIt != null){
			return curIt.key();
		}
		return null;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("unsupport remove operation!");
	}
	
	enum Direction{
		forward,reverse
	}

	@Override
	public long priority() {
		return 0;
	}

}
