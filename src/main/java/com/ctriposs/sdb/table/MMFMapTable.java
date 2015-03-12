package com.ctriposs.sdb.table;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.Arrays;

import com.ctriposs.sdb.utils.MMFUtil;
import com.google.common.base.Preconditions;

/**
 *
 * Memory mapped map table accompanied with bloom filter
 *
 * @author bulldog
 *
 */
public class MMFMapTable extends AbstractSortedMapTable {

	protected MappedByteBuffer dataMappedByteBuffer;

	// Create new
	public MMFMapTable(String dir, int level, long createdTime, int expectedInsertions, int mergeWays)
			throws IOException {
		this(dir, (short)0, level, createdTime, expectedInsertions, mergeWays);
	}

	public MMFMapTable(String dir, short shard, int level, long createdTime, int expectedInsertions, int mergeWays)
			throws IOException {
		super(dir, shard, level, createdTime, expectedInsertions);

		int mapDataFileSize = INIT_DATA_FILE_SIZE * mergeWays;
		dataMappedByteBuffer = this.dataChannel.map(MapMode.READ_WRITE, 0, mapDataFileSize);
	}

	// Load existing
	public MMFMapTable(String dir, String fileName)
			throws IOException, ClassNotFoundException {
		super(dir, fileName);

		int mapDataFileSize = (int) this.dataChannel.size();
		dataMappedByteBuffer = this.dataChannel.map(MapMode.READ_WRITE, 0, mapDataFileSize);
	}

	public void reMap() throws IOException {
		super.reMap();
		MMFUtil.unmap(dataMappedByteBuffer);
		this.dataChannel.truncate(toAppendDataFileOffset.get());
		dataMappedByteBuffer = this.dataChannel.map(MapMode.READ_ONLY, 0, this.dataChannel.size());
	}

	// for testing
	public IMapEntry appendNew(byte[] key, byte[] value, long timeToLive) throws IOException {
		return this.appendNew(key, Arrays.hashCode(key), value, timeToLive, System.currentTimeMillis(), false, false);
	}

	@Override
	public IMapEntry appendNew(byte[] key, int keyHash, byte[] value, long timeToLive, long createdTime, boolean markDelete, boolean compressed) throws IOException {
		ensureNotClosed();
		Preconditions.checkArgument(key != null && key.length > 0, "Key is empty");
		Preconditions.checkArgument(value != null && value.length > 0, "value is empty");
		Preconditions.checkArgument(this.toAppendIndex.get() < MAX_ALLOWED_NUMBER_OF_ENTRIES,
				"Exceeded max allowed number of entries(" + MAX_ALLOWED_NUMBER_OF_ENTRIES + ")!");

		appendLock.lock();
		try {
			// write index metadata
			indexBuf.clear();
			indexBuf.putLong(IMapEntry.INDEX_ITEM_IN_DATA_FILE_OFFSET_OFFSET, toAppendDataFileOffset.get());
			indexBuf.putInt(IMapEntry.INDEX_ITEM_KEY_LENGTH_OFFSET, key.length);
			indexBuf.putInt(IMapEntry.INDEX_ITEM_VALUE_LENGTH_OFFSET, value.length);
			indexBuf.putLong(IMapEntry.INDEX_ITEM_TIME_TO_LIVE_OFFSET, timeToLive);
			indexBuf.putLong(IMapEntry.INDEX_ITEM_CREATED_TIME_OFFSET, createdTime);
			indexBuf.putInt(IMapEntry.INDEX_ITEM_KEY_HASH_CODE_OFFSET, keyHash);
			byte status = 1; // mark in use
			if (markDelete) {
				status = (byte) (status + 2); // binary 11
			}
			if (compressed && !markDelete) {
				status = (byte) (status + 4);
			}
			indexBuf.put(IMapEntry.INDEX_ITEM_STATUS, status); // mark in use

			int offsetInIndexFile = INDEX_ITEM_LENGTH * toAppendIndex.get();
			this.indexMappedByteBuffer.position(offsetInIndexFile);
			//indexBuf.rewind();
			this.indexMappedByteBuffer.put(indexBuf);

			// write key/value
			this.dataMappedByteBuffer.position((int)toAppendDataFileOffset.get());
			this.dataMappedByteBuffer.put(ByteBuffer.wrap(key));
			this.dataMappedByteBuffer.position((int)toAppendDataFileOffset.get() + key.length);
			this.dataMappedByteBuffer.put(ByteBuffer.wrap(value));

			// update guarded condition
			this.bloomFilter.put(key);

			int dataLength = key.length + value.length;
			// commit/update offset & index
			toAppendDataFileOffset.addAndGet(dataLength);
			int appendedIndex = toAppendIndex.get();
			toAppendIndex.incrementAndGet();
			return new MMFMapEntryImpl(appendedIndex, this.indexMappedByteBuffer, this.dataMappedByteBuffer);
		}
		finally {
			appendLock.unlock();
		}
	}

	@Override
	public IMapEntry getMapEntry(int index) {
		ensureNotClosed();
		Preconditions.checkArgument(index >= 0, "index (%s) must be equal to or greater than 0", index);
		Preconditions.checkArgument(!isEmpty(), "Can't get map entry since the map is empty");
		return new MMFMapEntryImpl(index, this.indexMappedByteBuffer, this.dataMappedByteBuffer);
	}


	@Override
	public void close() throws IOException {
		MMFUtil.unmap(dataMappedByteBuffer);
		dataMappedByteBuffer = null;
		super.close();
	}
}
