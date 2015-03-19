package com.github.believe3301.nonheapdb;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Cache提供比MemoryManager更上层的服务
 * MemoryManager添加等操作还有和底层的Record交互.
 * Cache就只和key,value交互.不需要知道底层的数据结构
 */
public class DBCache {

	private MemoryManager manager;
	private BucketManager bm;
	private Metrics metrics;
	private long maxbytes;
	private int blocksize;

	public DBCache() {
		this(16);
	}

	public DBCache(int hashpower) {
		this(hashpower, Util.Mb(32));
	}

	public DBCache(int hashpower, int blocksize) {
		this(hashpower, blocksize, -1);
	}

	public DBCache(int hashpower, int blocksize, long maxbytes) {
		this.blocksize = Util.align(blocksize, 1024);
		if (this.maxbytes < 0) {
			this.maxbytes = -1;
		} else {
			this.maxbytes = Util.align(maxbytes, this.blocksize);
		}
		this.metrics = new Metrics();
		this.bm = new BucketManager(hashpower);
		this.manager = new MemoryManager(bm, this.blocksize, this.maxbytes);
	}

	public boolean exist(String key) {
		return this.manager.existRecord(key);
	}

	public boolean put(String key, byte[] value) {
		this.metrics.incrPutCmds();
		if (value.length > blocksize) {
			this.metrics.incrPutFails();
			return false;
		}
		boolean ret = false;
		if (ret = this.manager.put(key, value)) {
			this.metrics.incrRecords();
		} else {
			this.metrics.incrPutFails();
		}
		return ret;
	}

	public byte[] get(String key) {
		Record rec = this.manager.getRecord(key);
		if (rec != null) {
			this.metrics.incrGetHits();
			return rec.getData();
		}
		this.metrics.incrGetMisses();
		return null;
	}

	public void remove(String key) {
		this.metrics.incrRemoveCmds();
		if (!this.manager.removeRecord(key)) {
			this.metrics.incrRemoveFails();
		}
	}

	public String info() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("get_totals: %d\r\n", this.metrics.getRecords()));
		sb.append(String.format("get_hits: %d\r\n", this.metrics.getHits()));
		sb.append(String.format("get_misses: %d\r\n", this.metrics.getMisses()));
		sb.append(String.format("put_cmds: %d\r\n", this.metrics.getPutCmds()));
		sb.append(String.format("put_fails: %d\r\n", this.metrics.getPutFails()));
		sb.append(String.format("remove_cmds: %d\r\n", this.metrics.getRemoveCmds()));
		sb.append(String.format("remove_fails: %d\r\n", this.metrics.getRemoveFails()));
		sb.append(String.format("current_records: %d\r\n", this.manager.reccount()));
		sb.append(String.format("allocated_bytes: %d\r\n", this.manager.allocated()));
		sb.append(String.format("used_bytes: %d\r\n", this.manager.used()));
		sb.append(String.format("fp_size: %d\r\n", this.manager.fpsize()));
		sb.append(String.format("defragment_count: %d\r\n", this.manager.dfcount()));
		return sb.toString();
	}

    //不考虑hash冲突的Map桶策略
	public static class BucketManager {
		private int capacity;
		private long[] buckets;
		
		public BucketManager(int hashpower) {
			this.capacity = 1 << hashpower;
			this.buckets = new long[this.capacity];
		}

        //根据key获取在哪个buckets桶里
		private int getPos(String key) {
			int h = key.hashCode();
            //0x7FFFFFFF=0111 1111 | 1111 1111 | 1111 1111 | 1111 1111
            //key进行hash之后是int类型=4个字节=4*8=32位
            //为什么最高位不是F,而是7? 因为这个数字就是Integer.MAX_VALUE
			return (h & 0x7FFFFFFF) % capacity;
		}
		
		public long getBucket(String key) {
			return buckets[getPos(key)];
		}
		
		public void setBucket(String key, long index) {
            //会不会有冲突呢? getPos(key)对于不同的key可能会落到同一个bucket中
            //index是什么值: RecordIndex.getBucket()的值.
            //实际上代表了RecordIndex这个对象! 它封装了一条记录的index(在哪个内存块里),capacity(容量),offset(在内存块里的偏移量)
            //解决冲突的方式是通过Record parent字段. --> MemeoryManager.getRecord()调用了Record.setParent

            //setBucket保存的是最近放入的key的RecordIndex.getBucket()的值. 如果这个bucket先前有数据,也会覆盖.
			buckets[getPos(key)] = index;
		}
	}

	//解决有冲突的buckets. 使用二维数组
	static class BucketManager2 {
		private long[][] segments;
		private int capacity;
		private final ReentrantReadWriteLock[] segmentLocks = new ReentrantReadWriteLock[16];
        {
			for (int i = 0; i < 16; i++)
				segmentLocks[i] = new ReentrantReadWriteLock();
		}

		public BucketManager2(int hashpower) {
			hashpower = Math.max(hashpower, 12);
			this.capacity = 1 << (hashpower - 4);
			this.segments = new long[16][];
		}

		public long getBucket(String key) {
			int h = key.hashCode();
			int segment = h >>> 28;

			ReentrantReadWriteLock l = segmentLocks[segment];
			try {
				l.readLock().lock();
				long[] buckets = segments[segment];
				if (buckets != null) {
					int pos = (h & 0x0FFFFFFF) % capacity;
					return buckets[pos];
				}
				return 0L;
			} finally {
				l.readLock().unlock();
			}
		}

		public void setBucket(String key, long index) {
			int h = key.hashCode();
			int segment = h >>> 28;

			ReentrantReadWriteLock l = segmentLocks[segment];
			try {
				l.writeLock().lock();
				long[] buckets = segments[segment];
				if (buckets == null) {
					buckets = new long[this.capacity];
					segments[segment] = buckets;
				}
				int pos = (h & 0x0FFFFFFF) % capacity;
				buckets[pos] = index;
			} finally {
				l.writeLock().unlock();
			}
		}
	}


	static class Metrics {
		private AtomicInteger gethits;
		private AtomicInteger getmisses;
		private AtomicInteger putcmds;
		private AtomicInteger putfails;
		private AtomicInteger removecmds;
		private AtomicInteger removefails;
		private AtomicInteger rectotals;

		public Metrics() {
			this.gethits = new AtomicInteger();
			this.getmisses = new AtomicInteger();
			this.putcmds = new AtomicInteger();
			this.putfails = new AtomicInteger();
			this.removecmds = new AtomicInteger();
			this.removefails = new AtomicInteger();
			this.rectotals = new AtomicInteger();
		}

		public int incrGetHits() {
			return this.gethits.addAndGet(1);
		}

		public int getHits() {
			return this.gethits.get();
		}

		public int incrGetMisses() {
			return this.getmisses.addAndGet(1);
		}

		public int getMisses() {
			return this.getmisses.get();
		}

		public int incrPutCmds() {
			return this.putcmds.addAndGet(1);
		}

		public int getPutCmds() {
			return this.putcmds.get();
		}

		public int incrPutFails() {
			return this.putfails.addAndGet(1);
		}

		public int getPutFails() {
			return this.putfails.get();
		}

		public int incrRemoveCmds() {
			return this.removecmds.addAndGet(1);
		}

		public int getRemoveCmds() {
			return this.removecmds.get();
		}

		public int incrRemoveFails() {
			return this.removefails.addAndGet(1);
		}

		public int getRemoveFails() {
			return this.removefails.get();
		}

		public int incrRecords() {
			return this.rectotals.addAndGet(1);
		}

		public int getRecords() {
			return this.rectotals.get();
		}
	}
}
