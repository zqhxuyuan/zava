package com.ctriposs.sdb.table;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.xerial.snappy.Snappy;

import com.ctriposs.sdb.utils.MMFUtil;
import com.google.common.base.Preconditions;

/**
 * In memory hashmap backed by memory mapped WAL(Write Ahead Log)
 *
 * @author bulldog
 *
 */
public class HashMapTable extends AbstractMapTable {

	private AtomicBoolean immutable = new AtomicBoolean(true);

	private ConcurrentHashMap<ByteArrayWrapper, InMemIndex> hashMap;
	protected ThreadLocalByteBuffer localDataMappedByteBuffer;
	protected ThreadLocalByteBuffer localIndexMappedByteBuffer;

	private boolean compressionEnabled = true;

	// Create new
	public HashMapTable(String dir, int level, long createdTime)
			throws IOException {
		super(dir, level, createdTime);
		mapIndexAndDataFiles();
		initToAppendIndexAndOffset();
	}

	public HashMapTable(String dir, short shard, int level, long createdTime)
			throws IOException {
		super(dir, shard, level, createdTime);
		mapIndexAndDataFiles();
		initToAppendIndexAndOffset();
	}

	// Load existing
	public HashMapTable(String dir, String fileName)
			throws IOException {
		super(dir, fileName);
		mapIndexAndDataFiles();
		initToAppendIndexAndOffset();
	}

	public void setCompressionEnabled(boolean enabled) {
		this.compressionEnabled = enabled;
	}

	private void mapIndexAndDataFiles() throws IOException {
		MappedByteBuffer indexMappedByteBuffer = this.indexChannel.map(MapMode.READ_WRITE, 0, this.indexChannel.size());
		localIndexMappedByteBuffer = new ThreadLocalByteBuffer(indexMappedByteBuffer);
		MappedByteBuffer dataMappedByteBuffer = this.dataChannel.map(MapMode.READ_WRITE, 0, this.dataChannel.size());
		localDataMappedByteBuffer = new ThreadLocalByteBuffer(dataMappedByteBuffer);
	}

	public Set<Map.Entry<ByteArrayWrapper, InMemIndex>> getEntrySet() {
		ensureNotClosed();
		return this.hashMap.entrySet();
	}

	private void initToAppendIndexAndOffset() throws IOException {
		this.hashMap = new ConcurrentHashMap<ByteArrayWrapper, InMemIndex>(INIT_INDEX_ITEMS_PER_TABLE);
		toAppendIndex = new AtomicInteger(0);
		toAppendDataFileOffset = new AtomicLong(0);
		int index = 0;
		MMFMapEntryImpl mapEntry = new MMFMapEntryImpl(index, this.localIndexMappedByteBuffer.get(), this.localDataMappedByteBuffer.get());
		while(mapEntry.isInUse()) {
			toAppendIndex.incrementAndGet();
			long nextOffset = mapEntry.getItemOffsetInDataFile() + mapEntry.getKeyLength() + mapEntry.getValueLength();
			toAppendDataFileOffset.set(nextOffset);
			InMemIndex inMemIndex = new InMemIndex(index);
			// populate in memory skip list
			hashMap.put(new ByteArrayWrapper(mapEntry.getKey()), inMemIndex);
			index++;
			mapEntry = new MMFMapEntryImpl(index, this.localIndexMappedByteBuffer.get(), this.localDataMappedByteBuffer.get());
		}
	}

	// for testing
	public IMapEntry appendNew(byte[] key, byte[] value, long timeToLive, long createdTime) throws IOException {
		Preconditions.checkArgument(key != null && key.length > 0, "Key is empty");
		Preconditions.checkArgument(value != null && value.length > 0, "value is empty");
		return this.appendNew(key, Arrays.hashCode(key), value, timeToLive, createdTime, false, false);
	}

	private IMapEntry appendTombstone(byte[] key) throws IOException {
		Preconditions.checkArgument(key != null && key.length > 0, "Key is empty");
		return this.appendNew(key, Arrays.hashCode(key), new byte[] {0}, NO_TIMEOUT, System.currentTimeMillis(), true, false);
	}

	private IMapEntry appendNewCompressed(byte[] key, byte[] value, long timeToLive, long createdTime) throws IOException {
		Preconditions.checkArgument(key != null && key.length > 0, "Key is empty");
		Preconditions.checkArgument(value != null && value.length > 0, "value is empty");
		return this.appendNew(key, Arrays.hashCode(key), value, timeToLive, createdTime, false, true);
	}

	private IMapEntry appendNew(byte[] key, int keyHash, byte[] value, long timeToLive, long createdTime, boolean markDelete, boolean compressed) throws IOException {
		ensureNotClosed();
		
		long tempToAppendIndex;
		long tempToAppendDataFileOffset;
		
		appendLock.lock();
		try {

			if (toAppendIndex.get() == INIT_INDEX_ITEMS_PER_TABLE) { // index overflow
				return null;
			}
			int dataLength = key.length + value.length;
			if (toAppendDataFileOffset.get() + dataLength > INIT_DATA_FILE_SIZE) { // data overflow
				return null;
			}
			
			tempToAppendIndex = toAppendIndex.get();
			tempToAppendDataFileOffset = toAppendDataFileOffset.get();

			// commit/update offset & index
			toAppendDataFileOffset.addAndGet(dataLength);
			toAppendIndex.incrementAndGet();
		}
		finally {
			appendLock.unlock();
		}
		
		// write index metadata
		ByteBuffer tempIndexBuf = ByteBuffer.allocate(INDEX_ITEM_LENGTH);
		tempIndexBuf.putLong(IMapEntry.INDEX_ITEM_IN_DATA_FILE_OFFSET_OFFSET, tempToAppendDataFileOffset);
		tempIndexBuf.putInt(IMapEntry.INDEX_ITEM_KEY_LENGTH_OFFSET, key.length);
		tempIndexBuf.putInt(IMapEntry.INDEX_ITEM_VALUE_LENGTH_OFFSET, value.length);
		tempIndexBuf.putLong(IMapEntry.INDEX_ITEM_TIME_TO_LIVE_OFFSET, timeToLive);
		tempIndexBuf.putLong(IMapEntry.INDEX_ITEM_CREATED_TIME_OFFSET, createdTime);
		tempIndexBuf.putInt(IMapEntry.INDEX_ITEM_KEY_HASH_CODE_OFFSET, keyHash);
		byte status = 1; // mark in use
		if (markDelete) {
			status = (byte) (status + 2); // binary 11
		}
		if (compressed && !markDelete) {
			status = (byte) (status + 4);
		}
		tempIndexBuf.put(IMapEntry.INDEX_ITEM_STATUS, status); // mark in use

		int offsetInIndexFile = INDEX_ITEM_LENGTH * (int)tempToAppendIndex;
		ByteBuffer localIndexBuffer = this.localIndexMappedByteBuffer.get();
		localIndexBuffer.position(offsetInIndexFile);
		//indexBuf.rewind();
		localIndexBuffer.put(tempIndexBuf);

		// write key/value
		ByteBuffer localDataBuffer = this.localDataMappedByteBuffer.get();
		localDataBuffer.position((int)tempToAppendDataFileOffset);
		localDataBuffer.put(ByteBuffer.wrap(key));
		localDataBuffer.position((int)tempToAppendDataFileOffset + key.length);
		localDataBuffer.put(ByteBuffer.wrap(value));

		this.hashMap.put(new ByteArrayWrapper(key), new InMemIndex((int)tempToAppendIndex));
		
		return new MMFMapEntryImpl((int)tempToAppendIndex, localIndexBuffer, localDataBuffer);
	}

	@Override
	public IMapEntry getMapEntry(int index) {
		ensureNotClosed();
		Preconditions.checkArgument(index >= 0, "index (%s) must be equal to or greater than 0", index);
		Preconditions.checkArgument(!isEmpty(), "Can't get map entry since the map is empty");
		return new MMFMapEntryImpl(index, this.localIndexMappedByteBuffer.get(), this.localDataMappedByteBuffer.get());
	}

	@Override
	public GetResult get(byte[] key) throws IOException {
		ensureNotClosed();
		Preconditions.checkArgument(key != null && key.length > 0, "Key is empty");
		GetResult result = new GetResult();
		InMemIndex inMemIndex = this.hashMap.get(new ByteArrayWrapper(key));
		if (inMemIndex == null) return result;
		
		IMapEntry mapEntry = this.getMapEntry(inMemIndex.getIndex());
		if (mapEntry.isCompressed()) {
			result.setValue(Snappy.uncompress(mapEntry.getValue()));
		} else {
			result.setValue(mapEntry.getValue());
		}
		if (mapEntry.isDeleted()) {
			result.setDeleted(true);
			return result;
		}
		if (mapEntry.isExpired()) {
			result.setExpired(true);
			return result;
		}
		result.setLevel(this.getLevel());
		result.setTimeToLive(mapEntry.getTimeToLive());
		result.setCreatedTime(mapEntry.getCreatedTime());

		return result;
	}

	public void markImmutable(boolean immutable) {
		this.immutable.set(immutable);
	}

	public boolean isImmutable() {
		return this.immutable.get();
	}

	public boolean put(byte[] key, byte[] value, long timeToLive, long createdTime, boolean isDelete) throws IOException {
		ensureNotClosed();
		Preconditions.checkArgument(key != null && key.length > 0, "Key is empty");
		Preconditions.checkArgument(value != null && value.length > 0, "value is empty");

		IMapEntry mapEntry = null;
		if (isDelete) {
			// make a tombstone
			mapEntry = this.appendTombstone(key);
		} else {
			mapEntry = this.compressionEnabled ?
					this.appendNewCompressed(key, Snappy.compress(value), timeToLive, createdTime) : this.appendNew(key, value, timeToLive, createdTime);
		}

		if (mapEntry == null) { // no space
			return false;
		}

		return true;
	}

	public void put(byte[] key, byte[] value, long timeToLive, long createdTime) throws IOException {
		this.put(key, value, timeToLive, createdTime, false);
	}

	public void delete(byte[] key) throws IOException {
		this.appendTombstone(key);
	}

	public int getRealSize() {
		return this.hashMap.size();
}

	@Override
	public void close() throws IOException {
		if (this.localIndexMappedByteBuffer == null) return;
		if (this.localDataMappedByteBuffer == null) return;
		MMFUtil.unmap((MappedByteBuffer)this.localIndexMappedByteBuffer.getSourceBuffer());
		this.localIndexMappedByteBuffer = null;
		MMFUtil.unmap((MappedByteBuffer)this.localDataMappedByteBuffer.getSourceBuffer());
		this.localDataMappedByteBuffer = null;
		super.close();
	}
}
