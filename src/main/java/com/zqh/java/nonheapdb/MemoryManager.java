package com.zqh.java.nonheapdb;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryManager {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MemoryManager.class);

	private DBCache.BucketManager map;
	private List<MemoryBuffer> buffers; // memory blocks
	private int capacity;
	private long maxbytes;
	private AtomicInteger dfcount;
	private ReentrantReadWriteLock lock;

	public MemoryManager(DBCache.BucketManager bm, int capacity, long maxbytes) {
		this.map = bm;
		this.buffers = new ArrayList<MemoryBuffer>(128);
		this.lock = new ReentrantReadWriteLock();
		this.capacity = capacity;
		this.maxbytes = maxbytes;
		this.dfcount = new AtomicInteger();
		newBuffer();
		LOGGER.info("MemoryManger init ok, maxbytes: {}, capacity: {}",
				maxbytes, capacity);
	}

	private MemoryBuffer newBuffer() {
		if (this.maxbytes > 0 && this.allocated() >= this.maxbytes) {
			return null;
		}
		MemoryBuffer buf = MemoryBuffer.makeNew(this.capacity, true);
		buffers.add(buf);
		return buf;
	}

	public int bsize() {
		return this.buffers.size();
	}

	public int fpsize() {
		int sz = 0;

		for (MemoryBuffer b : buffers) {
			sz += b.fpsize();
		}
		return sz;
	}

	public int dfcount() {
		return this.dfcount.get();
	}

	public long used() {
		long ub = 0;
		for (MemoryBuffer b : this.buffers) {
			ub += b.used();
		}
		return ub;
	}

	public int reccount() {
		int rcnt = 0;
		synchronized (buffers) {
			for (MemoryBuffer b : this.buffers) {
				rcnt += b.count();
			}
		}
		return rcnt;
	}

	public long allocated() {
		return 1L * buffers.size() * capacity;
	}

	private MemoryBuffer getMemoryBuffer(int length) {

		int blen = buffers.size();
		MemoryBuffer b = buffers.get(blen - 1);

		if (b.remaining() < length) {
			// add remain to fp
			if (b.remaining() > 0) {
				b.remainToRecord(blen - 1);
			}
			// new buffer
			b = newBuffer();
		}
		return b;
	}

	private RecordIndex findFreeBlock(int length) {
		RecordIndex rec = null;

		for (int i = 0; i < this.buffers.size(); i++) {
			MemoryBuffer b = this.buffers.get(i);
			rec = b.findFreeBlock(length);
			if (rec == null) {
				if (b.canDefragment()) {
					b.defragment(this);
					this.dfcount.addAndGet(1);
					rec = b.findFreeBlock(length);
				}
			}

			if (rec != null) {
				break;
			}
		}

		return rec;
	}

	public boolean put(final String key, final byte[] value) {

		this.lock.writeLock().lock();

		try {
			if (this.getRecord(key, false) != null) {
				return false;
			}
			
			long next = this.map.getBucket(key);
			Record rec = new Record();
			rec.setData(value).setKey(key).setNext(next);

			ByteBuffer nbuf = rec.getBuffer();
			int length = nbuf.limit();

			if (length > Record.MAXRECORDSIZE) {
				LOGGER.info("MemoryManger put failed, size: {}, maxsize: {}",
						length, Record.MAXRECORDSIZE);
				return false;
			}
			
			RecordIndex ridx = findFreeBlock(length);
			if (ridx != null) {
				MemoryBuffer b = buffers.get(ridx.index());
				b.putData(nbuf, ridx.offset());
			} else {
				MemoryBuffer b = getMemoryBuffer(length);
				if (b != null) {
					ridx = b.putData(nbuf);
					ridx.setIndex(buffers.size() - 1);
				}
			}
			if (ridx != null) {
				this.map.setBucket(key, ridx.getBucket());
				return true;
			}
			return false;
		} finally {
			this.lock.writeLock().unlock();
		}
	}

	/*
	 * shift record to up offset. start from start to end. update bucket next
	 */
	public void shiftRecord(MemoryBuffer b, int offset, int start, int end) {
		int i = start;
		while (i < end) {
			//find record index
			Record rec = getRecord(b, i);
			RecordIndex ridx = rec.getIndex();
			assert ridx.offset() >= offset;
			ridx.setOffset(ridx.offset() - offset);
			if (rec.getParent() == null) {
				map.setBucket(rec.getKey(), ridx.getBucket());
			} else {
				MemoryBuffer pb = buffers.get(rec.getParent().index());
				pb.putLong(ridx.getBucket(), rec.getParent().offset() + 1);
			}
			i += ridx.capacity();
		}
		assert i == end;
	}

	/*
	 * get record in map by key
	 */
	public Record getRecord(String key) {
		this.lock.readLock().lock();
		try {
			return getRecord(key, true);
		} finally {
			this.lock.readLock().unlock();
		}
	}

	private Record getRecord(RecordIndex ridx) {
		MemoryBuffer b = this.buffers.get(ridx.index());
		return getRecordInner(b, ridx.offset()).setIndex(ridx);
	}

	private void getData(Record rec) {
		if (rec.getData() == null) {
			RecordIndex ridx = rec.getIndex();
			MemoryBuffer b = this.buffers.get(ridx.index());
			byte[] buf = b.getData(rec.getInfo().vsize, ridx.offset()
					+ rec.getInfo().hsize() + rec.getInfo().ksize);
			rec.setData(buf);
		}
	}

	private Record getRecord(String key, boolean hasData) {
		long index = this.map.getBucket(key);
		if (index == 0) {
			return null;
		}
		RecordIndex pidx = null;
		while (index != 0) {
			RecordIndex ridx = new RecordIndex(index);

			Record rec = getRecord(ridx);
			assert !rec.isFree();
			if (rec.getKey().equals(key)) {
				rec.setParent(pidx);
				if (hasData) {
					getData(rec);
				}
				return rec;
			}
			pidx = ridx;
			index = rec.getNext();
		}
		return null;
	}

	private Record getRecordInner(MemoryBuffer b, int offset) {
		Record rec = new Record();
		byte[] buf = b.getData(Record.READUNIT, offset);
		ByteBuffer nbuf = ByteBuffer.wrap(buf);
		int hsize = rec.setBuffer(nbuf);
		if (rec.getKey() == null) {
			String key = new String(b.getData(rec.getInfo().ksize, offset
					+ hsize));
			rec.setKey(key);
		}
		return rec;
	}
	
	public Record getRecord(MemoryBuffer b, int offset) {
		Record rec = getRecordInner(b, offset);
		return getRecord(rec.getKey(), false);
	}

	/*
	 * remove record in map by key
	 */
	public boolean removeRecord(String key) {
		this.lock.writeLock().lock();
		try {
			Record rec = getRecord(key, false);
			if (rec == null) {
				return false;
			}
			RecordIndex idx = rec.getIndex();
			if (rec.getParent() == null) {
				map.setBucket(key, rec.getNext());
			} else {
				MemoryBuffer pb = buffers.get(rec.getParent().index());
				pb.putLong(rec.getNext(), rec.getParent().offset() + 1);
			}
			MemoryBuffer b = buffers.get(idx.index());
			if (idx.index() == buffers.size() - 1) {
				if (b.mergeLastRecord(rec.getIndex(), rec.getInfo().used())) {
					return true;
				}
			}
			b.removeRecord(rec.getIndex(), rec.getInfo().used());
			return true;
		} finally {
			this.lock.writeLock().unlock();
		}
	}

	public boolean existRecord(String key) {
		this.lock.readLock().lock();
		try {
			return getRecord(key, false) != null;
		} finally {
			this.lock.readLock().unlock();
		}
	}
	/*
	 * dump
	 */
	public String dumpBuffer(MemoryBuffer buf) {
		return buf.hexDump();
	}
}
