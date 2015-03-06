package com.ctriposs.tsdb.iterator;

import java.io.IOException;
import java.util.Map.Entry;

import com.ctriposs.tsdb.DBConfig;
import com.ctriposs.tsdb.InternalEntry;
import com.ctriposs.tsdb.InternalKey;
import com.ctriposs.tsdb.common.IFileIterator;
import com.ctriposs.tsdb.common.IStorage;
import com.ctriposs.tsdb.storage.CodeBlock;
import com.ctriposs.tsdb.storage.CodeItem;
import com.ctriposs.tsdb.storage.Head;
import com.ctriposs.tsdb.storage.TimeBlock;
import com.ctriposs.tsdb.storage.TimeItem;

public class FileSeekIterator implements IFileIterator<InternalKey, byte[]> {

	private IStorage storage;
	private int maxCodeBlockIndex = -2;
	private int maxTimeBlockIndex = -2;
	private int curCodeBlockIndex = -1;
	private int curTimeBlockIndex = -1;
	
	private Entry<InternalKey, byte[]> curEntry;
	private TimeBlock curTimeBlock;
	private CodeBlock curCodeBlock;
	private CodeItem curCodeItem;
	private Head head;
	private long fileNumber;
	
	public FileSeekIterator(IStorage storage)throws IOException {
		this.storage = storage;
		byte[] bytes = new byte[Head.HEAD_SIZE];
		this.storage.get(0, bytes);
		this.head = new Head(bytes);
		this.maxCodeBlockIndex = (head.getCodeCount() + DBConfig.BLOCK_MAX_COUNT-1)/DBConfig.BLOCK_MAX_COUNT - 1;
		nextCodeBlock();
		this.curCodeItem = curCodeBlock.current();
		this.curEntry = null;
		this.curTimeBlock = null;
	}
	
	public FileSeekIterator(IStorage storage, long fileNumber)throws IOException {
		this(storage);
		this.fileNumber = fileNumber;
	}
	
	@Override
	public boolean hasNext() {
		if(curTimeBlockIndex <= maxTimeBlockIndex){
			if(curTimeBlock != null){
				if(!curTimeBlock.hasNext()){
					try{
						nextTimeBlock();
						if(curTimeBlock == null){
							return false;
						}else{
							if(curCodeItem != null){
								readEntry(curCodeItem.getCode(), curTimeBlock.current(), true);
								return true;
							}else{
								return false;
							}
						}
					}catch(IOException e){
						throw new RuntimeException(e);
					}

				}else{
					return true;
				}
			}else{
				
				try{
					nextTimeBlock();
					if(curTimeBlock == null){
						return false;
					}else{
						if(curCodeItem != null){
							readEntry(curCodeItem.getCode(), curTimeBlock.current(), true);
							return true;
						}else{
							return false;
						}
					}
				}catch(IOException e){
					throw new RuntimeException(e);
				}

			}
		}
		return false;
	}
	
	@Override
	public boolean hasPrev() {
		if(curTimeBlockIndex >= 0){
			if(curTimeBlock != null){
				if(!curTimeBlock.hasPrev()){
					try{
						prevTimeBlock();
						if(curTimeBlock == null){
							return false;
						}else{
							if(curCodeItem != null){
								readEntry(curCodeItem.getCode(), curTimeBlock.last(), true);
								return true;
							}else{
								return false;
							}
						}
					}catch(IOException e){
						throw new RuntimeException(e);
					}

				}else{
					return true;
				}		
				
			}else{
				try{
					prevTimeBlock();
					if(curTimeBlock == null){
						return false;
					}else{
						if(curCodeItem != null){
							readEntry(curCodeItem.getCode(), curTimeBlock.last(), true);
							return true;
						}else{
							return false;
						}
					}
				}catch(IOException e){
					throw new RuntimeException(e);
				}

			}
		}
		
		return false;
	}

	@Override
	public boolean hasNextCode() throws IOException {
		if(curCodeBlockIndex <= maxCodeBlockIndex){
			if(curCodeBlock != null){
				if(!curCodeBlock.hasNext()){
					try{
						nextCodeBlock();
					}catch(IOException e){
						throw new RuntimeException(e);
					}
					if(curCodeBlock == null){
						return false;
					}else{
						return true;
					}
				}else{
					return true;
				}
			}else{
				try{
					nextCodeBlock();
				}catch(IOException e){
					throw new RuntimeException(e);
				}
				if(curCodeBlock == null){
					return false;
				}else{
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean hasPrevCode() throws IOException {
		if(curCodeBlockIndex >= 0){
			if(curCodeBlock != null){
				if(!curCodeBlock.hasPrev()){
					try{
						prevCodeBlock();
					}catch(IOException e){
						throw new RuntimeException(e);
					}
					if(curCodeBlock == null){
						return false;
					}else{
						return true;
					}
				}else{
					return true;
				}
			}else{
				try{
					prevCodeBlock();
				}catch(IOException e){
					throw new RuntimeException(e);
				}
				if(curCodeBlock == null){
					return false;
				}else{
					return true;
				}
			}
		}
		return false;
	}

	private void nextCodeBlock() throws IOException{
		++curCodeBlockIndex;
		byte[] bytes = null;
		int count = 0;
		if(curCodeBlockIndex == maxCodeBlockIndex) {
			count = head.getCodeCount() - curCodeBlockIndex*DBConfig.BLOCK_MAX_COUNT;
		}else if(curCodeBlockIndex < maxCodeBlockIndex) {
			count = DBConfig.BLOCK_MAX_COUNT;		
		}else{
			curCodeBlock = null;
			return;
		}
		bytes = new byte[count*CodeItem.CODE_ITEM_SIZE];
		
		storage.get(head.getCodeOffset() + curCodeBlockIndex*DBConfig.BLOCK_MAX_COUNT*CodeItem.CODE_ITEM_SIZE, bytes);
		curCodeBlock = new CodeBlock(bytes, count);
		curCodeItem = curCodeBlock.current();
	}
	
	private void prevCodeBlock() throws IOException{
		--curCodeBlockIndex;
		byte[] bytes = null;
		int count = 0;
		if(curCodeBlockIndex == maxCodeBlockIndex){
			count = head.getCodeCount() - curCodeBlockIndex*DBConfig.BLOCK_MAX_COUNT;
		}else if(curCodeBlockIndex>=0){
			count = DBConfig.BLOCK_MAX_COUNT;		
		}else{
			curCodeBlock = null;
			return;
		}
		bytes = new byte[count*CodeItem.CODE_ITEM_SIZE];
		storage.get(head.getCodeOffset()+curCodeBlockIndex*DBConfig.BLOCK_MAX_COUNT*CodeItem.CODE_ITEM_SIZE, bytes);
		curCodeBlock = new CodeBlock(bytes, count);
		curCodeItem = curCodeBlock.last();
	}
	
	private void nextTimeBlock() throws IOException{
		
		++curTimeBlockIndex;
		byte[] bytes = null;
		int count = 0;
		if(curTimeBlockIndex == maxTimeBlockIndex){
			count = curCodeItem.getTimeCount() - curTimeBlockIndex*DBConfig.BLOCK_MAX_COUNT;
		}else if(curTimeBlockIndex < maxTimeBlockIndex){
			count = DBConfig.BLOCK_MAX_COUNT;		
		}else{
			curTimeBlock = null;
			return;
		}

		bytes = new byte[count*TimeItem.TIME_ITEM_SIZE];
		storage.get(curCodeItem.getTimeOffSet()+curTimeBlockIndex*DBConfig.BLOCK_MAX_COUNT*TimeItem.TIME_ITEM_SIZE, bytes);
		curTimeBlock = new TimeBlock(bytes, count);
	}
	
	private void prevTimeBlock() throws IOException{
		
		--curTimeBlockIndex;
		byte[] bytes = null;
		int count = 0;
		if(curTimeBlockIndex == maxTimeBlockIndex){
			count = curCodeItem.getTimeCount() - curTimeBlockIndex*DBConfig.BLOCK_MAX_COUNT;
		}else if(curTimeBlockIndex >= 0){
			count = DBConfig.BLOCK_MAX_COUNT;		
		}else{
			curTimeBlock = null;
			return;
		}
		bytes = new byte[count*TimeItem.TIME_ITEM_SIZE];
		storage.get(curCodeItem.getTimeOffSet()+curTimeBlockIndex*DBConfig.BLOCK_MAX_COUNT*TimeItem.TIME_ITEM_SIZE, bytes);
		curTimeBlock = new TimeBlock(bytes, count);
	}
	
	@Override
	public void seek(int code, long time) throws IOException {
		maxTimeBlockIndex = -2;
		curCodeBlockIndex = -1;
		curTimeBlockIndex = -1;
		curCodeItem = null;
		if (head.containCode(code)) {
			// read code area
			nextCodeBlock();
			find(code);

			// read time area
			if (curCodeItem != null) {
				curTimeBlockIndex = -1;
				nextTimeBlock();
				while (curTimeBlock.containTime(time) < 0) {
					nextTimeBlock();
					if (curTimeBlock == null) {
						break;
					}
				}
				if (curTimeBlock != null) {
					if (curTimeBlock.containTime(time) == 0) {
						curTimeBlock.seek(time);
					}
					readEntry(code, curTimeBlock.current(), true);

				} else {// pointer to last time block last time item
					curTimeBlockIndex = maxTimeBlockIndex + 1;
					prevTimeBlock();
					readEntry(code, curTimeBlock.last(), true);
				}
				return;

			}

		}
		curTimeBlockIndex = -1;
		maxTimeBlockIndex = -2;
	}

	@Override
	public void seekToFirst(int code,boolean isNext) throws IOException{
		maxTimeBlockIndex = -2;
		curCodeBlockIndex = -1;
		curTimeBlockIndex = -1;
		curCodeItem = null;
		if (head.containCode(code)) {
			// read code area
			nextCodeBlock();			
			find(code);
			
			// read time area
			if (curCodeItem != null) {
				if(isNext){
					nextTimeBlock();
					if (curTimeBlock != null) {						
						readEntry(curCodeItem.getCode(), curTimeBlock.current(), isNext);					
						return;
					}
				}else{
					curTimeBlockIndex = maxTimeBlockIndex + 1;
					prevTimeBlock();
					if (curTimeBlock != null) {						
						readEntry(curCodeItem.getCode(), curTimeBlock.last(), isNext);					
						return;
					}
				}
			}
		}
		curTimeBlockIndex = -1;
		maxTimeBlockIndex = -2;
	}

	private void find(int code)throws IOException {
		if (curCodeBlock != null) {
			while(curCodeBlock.containCode(code)<0){
				nextCodeBlock();
				if (curCodeBlock == null) {
					break;
				}
			}
			if(curCodeBlock!=null){
				if(!curCodeBlock.seek(code)){
					curCodeBlock = null;
				}
			}

		}

		if (curCodeBlock != null) {
			curCodeItem = curCodeBlock.current();
			if (curCodeItem != null) {
				maxTimeBlockIndex = (curCodeItem.getTimeCount() + DBConfig.BLOCK_MAX_COUNT-1)/ DBConfig.BLOCK_MAX_COUNT - 1;
				curTimeBlockIndex = -1;
			}
		}
	}
	
	@Override
	public boolean seekToCurrent(boolean isNext) throws IOException {

		// read code area
		if (curCodeBlock == null) {
			nextCodeBlock();
		}
		
		if(curCodeBlock != null){
			if(null == curCodeItem){
				if(isNext){
					curCodeItem = curCodeBlock.current();
				}else{
					curCodeItem = curCodeBlock.last();
				}
			}
		}
		
		// read time area
		if (curCodeItem != null) {
			maxTimeBlockIndex = (curCodeItem.getTimeCount() + DBConfig.BLOCK_MAX_COUNT - 1)/ DBConfig.BLOCK_MAX_COUNT - 1;
			if(isNext){
				curTimeBlockIndex = -1;
				nextTimeBlock();
			}else{
				curTimeBlockIndex = maxTimeBlockIndex + 1;
				prevTimeBlock();
			}
			if (curTimeBlock != null) {
				if(isNext){
					readEntry(curCodeItem.getCode(), curTimeBlock.current(), isNext);
				}else{
					readEntry(curCodeItem.getCode(), curTimeBlock.last(), isNext);
				}
				return true;
			}
		}

		curTimeBlockIndex = -1;
		maxTimeBlockIndex = -2;
		return false;
	}

	@Override
	public InternalKey key() {

		if(curEntry != null){
			return curEntry.getKey();
		}
		return null;
	}

	@Override
	public long time() {

		if(curEntry != null){
			return curEntry.getKey().getTime();
		}
		return 0;
	}

	@Override
	public byte[] value() throws IOException {

		if(curEntry != null){
			return curEntry.getValue();
		}
		return null;
	}

	@Override
	public boolean valid() {
		if(curEntry != null){
			return true;
		}
		return false;
	}

	private void readEntry(int code, TimeItem tItem, boolean isNext) throws IOException{
		if(tItem == null){
			if(isNext){
				curTimeBlockIndex = maxTimeBlockIndex + 1;
			}else{
				curTimeBlockIndex = -1;
			}
			curEntry = null;
		}else{
			InternalKey key = new InternalKey(code, tItem.getTime());
			byte[] value = new byte[tItem.getValueSize()];
			storage.get(tItem.getValueOffset(), value);
			curEntry = new InternalEntry(key, value);
		}
	}

	@Override
	public Entry<InternalKey, byte[]> next() {
		if(curTimeBlock == null){
			try{
				nextTimeBlock();
			}catch(IOException e){
				throw new RuntimeException(e);
			}
		}
		
		if(curTimeBlock != null){
			try{
				readEntry(curCodeItem.getCode(),curTimeBlock.next(),true);
			}catch(IOException e){
				throw new RuntimeException(e);
			}
		}else{
			curEntry = null;
		}
			
		return curEntry;
	}

	@Override
	public Entry<InternalKey, byte[]> prev() {
		if(curTimeBlock == null){
			try{
				prevTimeBlock();
			}catch(IOException e){
				throw new RuntimeException(e);
			}
		}
		
		if(curTimeBlock != null){
			try{
				readEntry(curCodeItem.getCode(),curTimeBlock.prev(),false);
			}catch(IOException e){
				throw new RuntimeException(e);
			}
		}else{
			curEntry = null;
		}
			
		return curEntry;
	}

	@Override
	public CodeItem nextCode() throws IOException {
		if(curCodeBlock == null){
			nextCodeBlock();
		}
		if(curCodeBlock != null){
			curCodeItem = curCodeBlock.next();
		}
		if(curCodeItem == null){
			nextCodeBlock();
			if(curCodeBlock != null){
				curCodeItem = curCodeBlock.next();
			}
		}
		
		return curCodeItem;
	}

	@Override
	public CodeItem currentCode() throws IOException{
		return curCodeItem;
	}
	
	@Override
	public CodeItem prevCode() throws IOException {
		if(curCodeBlock == null){
			prevCodeBlock();
		}
		if(curCodeBlock != null){
			curCodeItem = curCodeBlock.prev();
		}
		
		if(curCodeItem == null){
			prevCodeBlock();
			if(curCodeBlock != null){
				curCodeItem = curCodeBlock.prev();
			}
		}
		return curCodeItem;
	}

	@Override
	public void close() throws IOException {

		storage.close();
	}

	@Override
	public long timeItemCount() {
		return head.getTimeCount();
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException("unsupport remove operation!");
	}

	@Override
	public long priority() {
		return fileNumber;
	}

	@Override
	public Entry<InternalKey, byte[]> current() {
		return curEntry;
	}

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FileSeekIterator)) {
            return false;
        }

        FileSeekIterator iterator = (FileSeekIterator) obj;
        return this.storage == iterator.storage;
    }
}
