package com.github.believe3301.nonheapdb;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 内存管理: 管理所有的内存块(MemoryBuffer), 以及BucketManager桶的管理
 * 因为内存中有多个内存块,所以添加一条记录,需要BucketManager和MemoryBuffer协同,将记录添加到对应的内存块中.
 *
 */
public class MemoryManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(MemoryManager.class);

	private DBCache.BucketManager map;
	private List<MemoryBuffer> buffers; // memory blocks
	private int capacity;
	private long maxbytes;
	private AtomicInteger dfcount;
	private ReentrantReadWriteLock lock;

	public MemoryManager(DBCache.BucketManager bm, int capacity, long maxbytes) {
		this.map = bm;
		this.buffers = new ArrayList<MemoryBuffer>(128); //默认128个内存块.每个内存块都可以存放多条记录
		this.lock = new ReentrantReadWriteLock();
		this.capacity = capacity;
		this.maxbytes = maxbytes;
		this.dfcount = new AtomicInteger();
		newBuffer();
		LOGGER.info("MemoryManger init ok, maxbytes: {}, capacity: {}", maxbytes, capacity);
	}

	private MemoryBuffer newBuffer() {
		if (this.maxbytes > 0 && this.allocated() >= this.maxbytes) {
			return null;
		}
        //capacity是在构造MemoryManager时传入的.创建新的内存块时,每个内存块的大小都是事先设定好的.
		MemoryBuffer buf = MemoryBuffer.makeNew(this.capacity, true);
        //添加到列表中. 一个MemoryBuffer只是一个内存块,由MemoryManager管理所有的内存块.
		buffers.add(buf);
        //返回新创建的内存块
		return buf;
	}

    //有多少个内存块
	public int bsize() {
		return this.buffers.size();
	}

    //所有的空闲块.每个内存块都可能有多个空闲块.
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

    //总的占用内存字节数
	public long used() {
		long ub = 0;
		for (MemoryBuffer b : this.buffers) {
			ub += b.used();
		}
		return ub;
	}

    //总的记录数
	public int reccount() {
		int rcnt = 0;
		synchronized (buffers) {
			for (MemoryBuffer b : this.buffers) {
				rcnt += b.count();
			}
		}
		return rcnt;
	}

    //总共分配了多少空间.每个内存块MemoryBuffer的capacity都是一样的.
	public long allocated() {
		return 1L * buffers.size() * capacity;
	}

    //根据字节长度,返回可以写这条记录的内存块
	private MemoryBuffer getMemoryBuffer(int length) {
		int blen = buffers.size();
        //最后一个内存块,先前的内存块一定都是已经写满了.否则如果没有写满,不会新建内存块
		MemoryBuffer b = buffers.get(blen - 1);

        //剩余的空间不够
		if (b.remaining() < length) {
			// add remain to fp 因为不够写一条记录,把剩余的空间标记为空闲块
			if (b.remaining() > 0) {
				b.remainToRecord(blen - 1);
			}
			// new buffer 新创建一个内存块用来保存length这条数据
			b = newBuffer();
		}
        //剩余的空间足够,或者新建了一个内存块
		return b;
	}

    //寻找满足给定长度的空闲块
	private RecordIndex findFreeBlock(int length) {
		RecordIndex rec = null;
        //遍历所有的内存块
		for (int i = 0; i < this.buffers.size(); i++) {
			MemoryBuffer b = this.buffers.get(i);
            //返回当前内存块的可以写的那条记录的索引
			rec = b.findFreeBlock(length);
			if (rec == null) {
				if (b.canDefragment()) {
					b.defragment(this);
					this.dfcount.addAndGet(1); //整理了一次内存
					rec = b.findFreeBlock(length);
				}
			}
            //找到一个,则立即返回
			if (rec != null) {
				break;
			}
		}

		return rec;
	}

    // 添加一条记录: 首先构造Record,然后将记录添加到内存块中
    // 在put前从BucketManager map中获取key对应的bucket值,并在记录完成添加后更新bucket的值
	public boolean put(final String key, final byte[] value) {
		this.lock.writeLock().lock();
		try {
            //已经存在,不允许重复添加
			if (this.getRecord(key, false) != null) return false;

            //next是RecordIndex.getBucket()的值,代表了RecordIndex.这里不用对象的方式,用一个long数字,也可以计算出RecordIndex.
            //如果key对应的bucket里已经有数据了,则取出的next是前一个key放入的值.用类似指针的方式setNext.
            //setNext表示当前新添加的元素的下一个是原先存在的RecordIndex.getBucket().
            //最后在完成添加后,再做一次更新,表示当前是最新的!类似于添加元素到链表表头,返回表头元素.
            //如果bucket没有存在数据,则next=0.说明这个key在bucket中是第一个元素(链表头),既然是第一个元素,记录的parent=null
			long next = this.map.getBucket(key);
			Record rec = new Record();
            //注意:并没有在put的时候设置parent.也没有设置index. 都是在getRecord()时设置.
            //为的是确保写入数据时,不需要遍历冲突列表. 而只在get时,存在冲突列表时更新这2个字段.
            //因为写入数据时,我们实际上是不关心冲突列表的,只要能正常写入数据就可以了.
			rec.setData(value).setKey(key).setNext(next);

			ByteBuffer nbuf = rec.getBuffer();
			int length = nbuf.limit();
			if (length > Record.MAXRECORDSIZE) {
				LOGGER.info("MemoryManger put failed, size: {}, maxsize: {}", length, Record.MAXRECORDSIZE);
				return false;
			}
			
			RecordIndex ridx = findFreeBlock(length);
            //有空闲块,首先写在空闲块了.当然在findFreeBlock中确保返回能够满足写入length长度的空闲块
			if (ridx != null) {
				MemoryBuffer b = buffers.get(ridx.index());
				b.putData(nbuf, ridx.offset());
			} else {
                //没有空闲块,追加在当前内存块的后面(或者如果当前内存块空间不足,新建一个内存块)
				MemoryBuffer b = getMemoryBuffer(length);
				if (b != null) {
                    //将数据添加到内存块后,返回这条记录的索引
					ridx = b.putData(nbuf);
                    //内存块的数量是buffers.size(),最后一个内存块,即当前内存块的索引就是buffers.size()-1
					ridx.setIndex(buffers.size() - 1);
				}
			}
            //TODO 为什么要设置bucket?
			if (ridx != null) {
                //把RecordIndex.getBucket()设置到key中:buckets[bucket]=RecordIndex.getBucket()
                //所以前面map.getBucket(key)的返回值就是这里放进的RecordIndex.getBucket()
                //在DBCache.BucketManager的setBucket()方法如果key对应的bucket已经有数据,则会发生覆盖.我们在这里调用完成赋值或覆盖操作
				this.map.setBucket(key, ridx.getBucket());
				return true;
			}
			return false;
		} finally {
			this.lock.writeLock().unlock();
		}
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

    // get record by Buffer and Offet
    public Record getRecord(MemoryBuffer b, int offset) {
        Record rec = getRecordInner(b, offset);
        return getRecord(rec.getKey(), false);
    }

    // get record by RecordIndex
    private Record getRecord(RecordIndex ridx) {
        MemoryBuffer b = this.buffers.get(ridx.index());
        //这里获取到Record后,设置RecordIndex. 因为是链式调用,setIndex后返回的也是Record.
        return getRecordInner(b, ridx.offset())
                .setIndex(ridx);
    }

    // 面向字节,来获取记录.定位到MemoryBuffer的offset处,
    private Record getRecordInner(MemoryBuffer b, int offset) {
        //读取单元=64byte. 既然key,value的长度是不确定的.如何确定完整地读取一条记录?
        byte[] buf = b.getData(Record.READUNIT, offset);
        ByteBuffer nbuf = ByteBuffer.wrap(buf);

        Record rec = new Record();
        //调用Record的setBuffer,会将ByteBuffer中的字节数据为Record的各个字段赋值
        int hsize = rec.setBuffer(nbuf);
        if (rec.getKey() == null) {
            String key = new String(b.getData(rec.getInfo().ksize, offset + hsize));
            rec.setKey(key);
        }
        return rec;
    }

	private Record getRecord(String key, boolean hasData) {
        //index是Record的getBucket()的值,即代表了RecordIndex对象
        //但是究竟是不是我们要的那条记录的RecordIndex?不一定!
		long index = this.map.getBucket(key);
		if (index == 0) return null;

		RecordIndex pidx = null;
		while (index != 0) {
            //RecordIndex可以根据bucket反解析出其三个字段的值
			RecordIndex ridx = new RecordIndex(index);

			Record rec = getRecord(ridx);
			assert !rec.isFree();
            //在链表中找到key
			if (rec.getKey().equals(key)) {
                //TODO 在获取记录的时候,进行setParent操作! 这是什么道理?
                //put的时候如果key对应的bucket里已经有数据,则通过next当做指针:新添加的元素在表头.
                //在get获取数据时,由于数据存储散列冲突,当找到bucket后,还要遍历链表找到指定的元素.

                //在循环的过程中,要设置pidx,这样找到后,找到的记录的parent就是上一条记录!
                //这样的话,貌似只有找到记录的key才有parent字段.其他记录都没有啊.
                //如果找到的刚好是链表的第一个元素,不会执行if后面的,pidx没有被赋值过,则parent=null
				rec.setParent(pidx);
				if (hasData) {
					getData(rec);
				}
				return rec;
			}
            //还没找到? 获取当前记录的next作为下一个RecordIndex的bucket构造参数
			pidx = ridx; //下一记录的parent是这一条记录!
			index = rec.getNext();
		}
		return null;
	}

    private void getData(Record rec) {
        if (rec.getData() == null) {
            RecordIndex ridx = rec.getIndex();
            MemoryBuffer b = this.buffers.get(ridx.index());
            byte[] buf = b.getData(rec.getInfo().vsize,
                    //记录的offset + header + key的长度 就是value的offset
                    ridx.offset() + rec.getInfo().hsize() + rec.getInfo().ksize);
            rec.setData(buf);
        }
    }

	/*
	 * remove record in map by key
	 */
	public boolean removeRecord(String key) {
		this.lock.writeLock().lock();
		try {
            //根据key得到这条记录
			Record rec = getRecord(key, false);
			if (rec == null) {
				return false;
			}
            //根据记录,得到记录的索引
			RecordIndex idx = rec.getIndex();
            //TODO 索引的parent是什么? 同一个bucket的冲突列表??
            //什么时候记录的parent为空? 链表的第一个元素没有parent.
            //链表的第二个元素开始都有可能有parent引用. 我们能确定的是第一个元素一定没有parent,但不一定其他元素都有parent
            //因为setParent发生在get时才设置,如果没有get,就没有setParent. 如果get刚好在第一个元素,pidx=null
            //要删除链表的第一个元素,则要将下一个元素的RecordIndex保存在bucket桶里:即第一个元素的next值. 删除前bucket保存的是表头的RecordIndex.
			if (rec.getParent() == null) {
				map.setBucket(key, rec.getNext());
			} else {
                //删除的不是链表表头,先找到要删除节点的父节点
				MemoryBuffer pb = buffers.get(rec.getParent().index());
                //设置父节点的next引用指向要删除节点的next
                //父节点的位置是rec.getParent().offset,再加上一个字节就是offset后的next,因为Record的offset是MAGIC,再往后一个字节是next
                //要删除节点的下一个节点的引用,存储在记录的next字段里:即rec.getNext.
				pb.putLong(rec.getNext(), rec.getParent().offset() + 1);
			}

            //根据索引的index(第几个内存块)得到内存块
			MemoryBuffer b = buffers.get(idx.index());
			if (idx.index() == buffers.size() - 1) {
                //如果是当前内存块的最后一条记录,则直接合并,不删除.下一次写数据的话,会直接覆盖最后一条记录.
				if (b.mergeLastRecord(rec.getIndex(), rec.getInfo().used())) {
					return true;
				}
			}
            //删除记录
			b.removeRecord(rec.getIndex(), rec.getInfo().used());
			return true;
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

	public boolean existRecord(String key) {
		this.lock.readLock().lock();
		try {
			return getRecord(key, false) != null;
		} finally {
			this.lock.readLock().unlock();
		}
	}

	// dump
	public String dumpBuffer(MemoryBuffer buf) {
		return buf.hexDump();
	}
}
