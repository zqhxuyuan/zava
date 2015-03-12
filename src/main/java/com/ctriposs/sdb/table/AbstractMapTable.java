package com.ctriposs.sdb.table;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctriposs.sdb.utils.FileUtil;
import com.google.common.base.Preconditions;

public abstract class AbstractMapTable implements Closeable, Comparable<AbstractMapTable> {

	static final Logger log = LoggerFactory.getLogger(AbstractMapTable.class);

	static int SIZE_OF_LONG_IN_BYTES = 8;
	static int SIZE_OF_INT_IN_BYTES = 4;

	public final static int INIT_INDEX_ITEMS_PER_TABLE = 128 * 1024;
	// length in bytes of an index item
	final static int INDEX_ITEM_LENGTH = 40;
	// size in bytes of initial index file
	final static int INIT_INDEX_FILE_SIZE = INDEX_ITEM_LENGTH * INIT_INDEX_ITEMS_PER_TABLE;
	// size in bytes of initial data file
	final static int INIT_DATA_FILE_SIZE = 128 * 1024 * 1024;
	final static int META_FILE_SIZE = 1 + SIZE_OF_INT_IN_BYTES + SIZE_OF_LONG_IN_BYTES;
	final static int TO_APPEND_INDEX_OFFSET = 1;
	final static int TO_APPEND_DATA_FILE_OFFSET = 1 + SIZE_OF_INT_IN_BYTES;

	public final static int NO_TIMEOUT = -1;

	protected RandomAccessFile metaRaf;
	protected RandomAccessFile indexRaf;
	protected RandomAccessFile dataRaf;
	protected FileChannel metaChannel;
	protected FileChannel dataChannel;
	protected FileChannel indexChannel;

	protected boolean usable = true;
	protected boolean closed = false;

	protected String dir;
	protected String fileName;

	protected String metaFile;
	protected String indexFile;
	protected String dataFile;

	protected AtomicInteger toAppendIndex;
	protected AtomicLong toAppendDataFileOffset;
	protected final Lock appendLock = new ReentrantLock();

	// the level of the map store, start from 0, incremental.
	private int level;
	// the shard of the map store, start form 0, incremental.
	private short shard;
	// when this map store was created
	private long createdTime;

	public static final String DATA_FILE_SUFFIX = ".data";
	public static final String INDEX_FILE_SUFFIX = ".index";
	public static final String META_FILE_SUFFIX = ".meta";

	public AbstractMapTable(String dir, int shard, int level, long createdTime) throws IOException {
		this.commonInit(dir, shard + "-" + level + "-" + createdTime);
	}

	public AbstractMapTable(String dir, int level, long createdTime) throws IOException {
		this(dir, 0, level, createdTime);
	}

	public AbstractMapTable(String dir, String fileName) throws IOException {
		this.commonInit(dir, fileName);
	}

	private void commonInit(String dir, String fileName) throws IOException {
		Preconditions.checkNotNull(dir);
		Preconditions.checkNotNull(fileName);

		this.fileName = fileName;

		this.initLevelAndCreatedTime(this.fileName);

		File dirFile = new File(dir);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		this.dir = dir;
		if (!this.dir.endsWith(File.separator)) {
			this.dir += File.separator;
		}

		this.metaFile = this.dir + this.fileName + META_FILE_SUFFIX;
		metaRaf = new RandomAccessFile(metaFile, "rw");
		if (metaRaf.length() <= 0) metaRaf.setLength(META_FILE_SIZE);
		metaChannel = metaRaf.getChannel();
		ByteBuffer byteBuf = ByteBuffer.allocate(1);
		metaChannel.read(byteBuf, 0);
		usable = (byteBuf.get(0) & 2) == 2;

		initIndexAndDataChannel(this.fileName);
	}

	private void initLevelAndCreatedTime(String fileName) {
		String[] parts = fileName.split("-");
		shard = Short.parseShort(parts[0]);
		level = Integer.parseInt(parts[1]);
		createdTime = Long.parseLong(parts[2]);
	}

	private void initIndexAndDataChannel(String fileName) throws IOException {
		this.indexFile = this.dir + fileName + INDEX_FILE_SUFFIX;
		indexRaf = new RandomAccessFile(indexFile, "rw");
		if (indexRaf.length() <= 0) indexRaf.setLength(INIT_INDEX_FILE_SIZE + INDEX_ITEM_LENGTH); // plus one padding
		indexChannel = indexRaf.getChannel();

		this.dataFile = this.dir + fileName + DATA_FILE_SUFFIX;
		dataRaf = new RandomAccessFile(dataFile, "rw");
		if (dataRaf.length() <= 0) dataRaf.setLength(INIT_DATA_FILE_SIZE);
		dataChannel = dataRaf.getChannel();
	}

	public abstract IMapEntry getMapEntry(int index);

	public String getFileName() {
		return this.fileName;
	}

	public int getAppendedSize() {
		return toAppendIndex.get();
	}

	public long getBackFileSize() throws IOException {
		ensureNotClosed();
		return this.indexChannel.size() + this.dataChannel.size();
	}

	public boolean isEmpty() {
		return toAppendIndex.get() == 0L;
	}

	public boolean isUsable() {
		return this.usable;
	}

	@Override
	public void close() throws IOException {
		if (this.metaChannel != null) {
			this.metaChannel.close();
			this.metaChannel = null;
		}
		if (this.metaRaf != null) {
			this.metaRaf.close();
			this.metaRaf = null;
		}
		if (this.indexChannel != null) {
			this.indexChannel.close();
			this.indexChannel = null;
		}
		if (this.indexRaf != null) {
			this.indexRaf.close();
			this.indexRaf = null;
		}
		if (this.dataChannel != null) {
			this.dataChannel.close();
			this.dataChannel = null;
		}
		if (this.dataRaf != null) {
			this.dataRaf.close();
			this.dataRaf = null;
		}
		this.closed = true;
	}

	@Override
	public int compareTo(AbstractMapTable mt) {
		if (level < mt.getLevel()) return -1;
		else if (level > mt.getLevel()) return 1;
		else {
			if (createdTime > mt.getCreatedTime()) return -1;
			else if (createdTime < mt.getCreatedTime()) return 1;
			else return 0;
		}
	}

	public int getLevel() {
		return level;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public short getShard() {
		return shard;
	}

	public void markUsable(boolean usable) throws IOException {
		ensureNotClosed();
		ByteBuffer byteBuf = ByteBuffer.allocate(1);
		this.metaChannel.read(byteBuf, 0);
		byte status = byteBuf.get(0);
		if (usable) {
			status |= 2; // set 1
		} else {
			status &= ~2; // set 0
		}
		this.metaChannel.write(ByteBuffer.wrap(new byte[] {status}), 0);
		this.usable= usable;
	}

	public void delete() {
		Preconditions.checkArgument(closed, "Can't delete not closed map table!");
		if (!FileUtil.deleteFile(this.metaFile)) {
			log.warn("fail to delete meta file " + this.metaFile + ", please delete it manully");
		}
		if (!FileUtil.deleteFile(this.indexFile)) {
			log.warn("fail to delete index file " + this.indexFile + ", please delete it manully");
		}
		if (!FileUtil.deleteFile(this.dataFile)) {
			log.warn("fail to delete data file " + this.dataFile + ", please delete it manully");
		}
	}

	public abstract GetResult get(byte[] key) throws IOException;

	protected void ensureNotClosed() {
		if (closed) {
			throw new IllegalStateException("You can't work on a closed map table.");
		}
	}
}
