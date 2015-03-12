package com.ctriposs.sdb.table;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.xerial.snappy.Snappy;

import com.ctriposs.sdb.utils.BytesUtil;
import com.ctriposs.sdb.utils.FileUtil;
import com.ctriposs.sdb.utils.MMFUtil;
import com.google.common.base.Preconditions;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

public abstract class AbstractSortedMapTable extends AbstractMapTable {

	public static final String BLOOM_FLITER_FILE_SUFFIX = ".bloom";
	public static final float FALSE_POSITIVE_PROBABILITY = 0.001F;
	public static final int MAX_ALLOWED_NUMBER_OF_ENTRIES = Integer.MAX_VALUE / INDEX_ITEM_LENGTH;

	protected final ByteBuffer indexBuf = ByteBuffer.allocate(INDEX_ITEM_LENGTH);

	protected BloomFilter<byte[]> bloomFilter;
	protected String bloomFilterFile;

	protected MappedByteBuffer indexMappedByteBuffer;

	public AbstractSortedMapTable(String dir, int level, long createdTime, int expectedInsertions)
			throws IOException {
		this(dir, (short)0, level, createdTime, expectedInsertions);
	}

	public AbstractSortedMapTable(String dir, short shard, int level, long createdTime, int expectedInsertions)
			throws IOException {
		super(dir, shard, level, createdTime);
		this.bloomFilterFile = this.dir + this.fileName + BLOOM_FLITER_FILE_SUFFIX;
		this.createNewBloomFilter(expectedInsertions);

		initToAppendIndexAndOffset();

		int mapIndexFileSize = INDEX_ITEM_LENGTH * expectedInsertions;
		indexMappedByteBuffer = this.indexChannel.map(MapMode.READ_WRITE, 0, mapIndexFileSize);
	}

	public AbstractSortedMapTable(String dir, String fileName) throws IOException, ClassNotFoundException {
		super(dir, fileName);
		this.bloomFilterFile = this.dir + this.fileName + BLOOM_FLITER_FILE_SUFFIX;
		this.reloadSavedBloomFilter();

		initToAppendIndexAndOffset();

		int mapIndexFileSize = (int) this.indexChannel.size();
		indexMappedByteBuffer = this.indexChannel.map(MapMode.READ_WRITE, 0, mapIndexFileSize);
	}

	void initToAppendIndexAndOffset() throws IOException {
		ByteBuffer longBuf = ByteBuffer.allocate(SIZE_OF_LONG_IN_BYTES);
		this.metaChannel.read(longBuf, TO_APPEND_INDEX_OFFSET);
		int index = longBuf.getInt(0);
		this.toAppendIndex = new AtomicInteger(index);

		this.metaChannel.read(longBuf, TO_APPEND_DATA_FILE_OFFSET);
		long offset = longBuf.getLong(0);
		this.toAppendDataFileOffset = new AtomicLong(offset);
	}


	private void createNewBloomFilter(int expectedInsertions) throws IOException {
		bloomFilter = BloomFilter.create(Funnels.byteArrayFunnel(), expectedInsertions, FALSE_POSITIVE_PROBABILITY);
		this.persistBloomFilter();
	}

	@SuppressWarnings("unchecked")
	private void reloadSavedBloomFilter() throws IOException, ClassNotFoundException {
		File file = new File(bloomFilterFile);
		Preconditions.checkArgument(file.exists() && file.length() > 0);
		InputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			bloomFilter = (BloomFilter<byte[]>)ois.readObject();
		} finally {
			ois.close();
			fis.close();
		}
	}

	public void persistBloomFilter() throws IOException {
		ensureNotClosed();
		File file = new File(this.bloomFilterFile);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(this.bloomFilter);
			oos.flush();
		} finally {
			oos.close();
			fos.close();
		}
	}

	// Search the key in the hashcode sorted array
	private IMapEntry binarySearch(byte[] key) throws IOException {
		int hashCode = Arrays.hashCode(key);
		int lo = 0; int slo = lo;
		int hi = this.getAppendedSize() - 1; int shi = hi;
		while (lo <= hi) {
			int mid = lo + (hi - lo) / 2;
			IMapEntry mapEntry = this.getMapEntry(mid);
			int midHashCode = mapEntry.getKeyHash();
			if (hashCode < midHashCode) hi = mid - 1;
			else if (hashCode > midHashCode) lo = mid + 1;
			else {
				if (BytesUtil.compare(key, mapEntry.getKey()) == 0) {
					return mapEntry;
				}
				// find left
				int index = mid - 1;
				while(index >= slo) {
					mapEntry = this.getMapEntry(index);
					if (hashCode != mapEntry.getKeyHash()) break;
					if (BytesUtil.compare(key, mapEntry.getKey()) == 0) {
						return mapEntry;
					}
					index--;
				}
				// find right
				index = mid + 1;
				while(index <= shi) {
					mapEntry = this.getMapEntry(index);
					if (hashCode != mapEntry.getKeyHash()) break;
					if (BytesUtil.compare(key, mapEntry.getKey()) == 0) {
						return mapEntry;
					}
					index++;
				}

				return null;
			}
		}
		return null;
	}

	@Override
	public GetResult get(byte[] key) throws IOException {
		ensureNotClosed();
		Preconditions.checkArgument(key != null && key.length > 0, "Key is empty");
		Preconditions.checkArgument(this.getAppendedSize() >= 1, "the map table is empty");
		GetResult result = new GetResult();

		// leverage bloom filter for guarded condition
		if (!this.bloomFilter.mightContain(key)) return result;

		IMapEntry mapEntry = this.binarySearch(key);
		if (mapEntry == null) return result;
		else {
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
			// hint for locality
			result.setLevel(this.getLevel());
			result.setTimeToLive(mapEntry.getTimeToLive());
			result.setCreatedTime(mapEntry.getCreatedTime());

			return result;
		}
	}

	public void persistToAppendIndex() throws IOException {
		ensureNotClosed();
		ByteBuffer intBuf = ByteBuffer.allocate(SIZE_OF_INT_IN_BYTES);
		intBuf.putInt(0, this.toAppendIndex.get());
		this.metaChannel.write(intBuf, TO_APPEND_INDEX_OFFSET);
	}

	public void persistToAppendDataFileOffset() throws IOException {
		ensureNotClosed();
		ByteBuffer longBuf = ByteBuffer.allocate(SIZE_OF_LONG_IN_BYTES);
		longBuf.putLong(0, this.toAppendDataFileOffset.get());
		this.metaChannel.write(longBuf, TO_APPEND_DATA_FILE_OFFSET);
	}

	public void saveMetadata() throws IOException {
		ensureNotClosed();
		this.persistToAppendIndex();
		this.persistToAppendDataFileOffset();
		this.persistBloomFilter();
	}

	public void reMap() throws IOException {
		ensureNotClosed();
		MMFUtil.unmap(indexMappedByteBuffer);
		this.indexChannel.truncate(INDEX_ITEM_LENGTH * toAppendIndex.get());
		indexMappedByteBuffer = this.indexChannel.map(MapMode.READ_ONLY, 0, this.indexChannel.size());
	}

	public abstract IMapEntry appendNew(byte[] key, int keyHash, byte[] value, long timeToLive, long lastAccessedTime, boolean markDelete, boolean compressed) throws IOException;

	@Override
	public void delete() {
		super.delete();
		if (!FileUtil.deleteFile(this.bloomFilterFile)) {
			log.warn("fail to delete bloom filer file " + this.bloomFilterFile + ", please delete it manully");
		}
	}

	@Override
	public void close() throws IOException {
		MMFUtil.unmap(indexMappedByteBuffer);
		indexMappedByteBuffer = null;
		super.close();
	}
}
