package com.github.believe3301.nonheapdb;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * buffer block 缓冲块
 * 一个内存块管理记录的put和get,remove操作.
 * 添加一条记录返回这条记录的索引信息RecordIndex.
 *
 * 并提供在内存块中寻找合适的空闲块:findFreeBlock,
 * 碎片整理:defragment,
 * 保留空间:remainToRecord,
 * 合并最后一条记录:mergeLastRecord.
 */
public class MemoryBuffer {
	private int used; 							// record used byte
	private int count;							// record count

	private ByteBuffer buf; 					// record buffer,position is zero
	private ByteBuffer buf_append;  			// sliced record buffer,position is last record,only for append

	/*free pool config */
	//TODO LongTreeSet(reduce boxing)
    // record free pool,sort with size desc(free block not merge and split)
    // 空闲块的大小按照升序排列.这样新写入的块要写入空闲块时,可以根据块的大小决定写往满足大小的空闲块
	private TreeSet<Long> fp;
	public static final int FBMAX = 64;			// fb max cout 最多允许64个空闲块
	public static final float FBRATIO = 0.75f;  // if fbRatio exceed,would process auto defragment

	private MemoryBuffer(ByteBuffer buf) {
		this.buf = buf;
		this.buf_append = buf.slice();
        //按照指定的Comparator进行排序.
		this.fp = new TreeSet<Long>(RecordIndex.capacityComparator());
		this.used = 0;
		this.count = 0;
	}

	public static MemoryBuffer makeNew(int capacity) {
		return makeNew(capacity, false);
	}

	public static MemoryBuffer makeNew(int capacity, byte[] init) {
		ByteBuffer b = ByteBuffer.wrap(init);
		return new MemoryBuffer(b);
	}

	public static MemoryBuffer makeNew(int capacity, boolean direct) {
		ByteBuffer b;
		if (direct) {
			b = ByteBuffer.allocateDirect(capacity);
		} else {
			b = ByteBuffer.allocate(capacity);
		}
		//b.limit(capacity);
		return new MemoryBuffer(b);
	}
	
	public int capacity() {
		return this.buf.capacity();
	}

	public int remaining() {
		return this.buf_append.remaining();
	}
	
	public int count() {
		return this.count;
	}

	public int used() {
		return this.used;
	}

	public int fpsize() {
		return this.fp.size();
	}

    // 将记录的字节数组放入缓冲区中.返回记录的索引信息
    // 全内存(Non-Heap)操作如何体现?
    // 添加一条记录后,返回这条记录的索引信息. 因为记录直接写在内存中,
    // 所以要在内存中知道刚刚写入的这条记录在内存中的位置信息,以及占用的大小

    // 没有指定offset的话,会往buf_append末尾追加.也会修改buf的内容!
    // 即追加到buf_append中的数据后,通过buf也能获取到写入的数据
    // 原始buf和buf_append唯一的不同是:remain和position不同.
    // 追加到buf_append后,position增加,remain减少. 而原始buf都没有变化(0和capacity)
    public RecordIndex putData(ByteBuffer nbuf) {
        RecordIndex record = new RecordIndex()
                .setOffset(buf_append.position())
                .setCapacity(nbuf.limit());
        //追加到buf_append缓冲区中
        this.buf_append.put(nbuf);
        this.used += record.capacity(); //记录了内存缓冲区使用的字节数
        this.count++; //记录的数量
        return record;
    }

    // 有指定offset的话,会在内存缓冲区的指定位置写入数据.
    public void putData(ByteBuffer rbuf, int offset) {
        //slice()根据现有的缓冲区创建一个子缓冲区:它创建一个新的缓冲区，新缓冲区与原来的缓冲区的一部分共享数据。
        ByteBuffer nbuf = this.buf.slice();
        //定位到指定位置
        nbuf.position(offset);
        //写入数据
        nbuf.put(rbuf);

        this.used += rbuf.limit();
        this.count++;
    }

    public void putData(byte data, int offset) {
        ByteBuffer nbuf = this.buf.slice();
        nbuf.position(offset);
        nbuf.put(data);
    }

    public void putLong(long data, int offset){
        ByteBuffer nbuf = this.buf.slice();
        nbuf.position(offset);
        nbuf.putLong(data);
    }

    //在offset开始,读取length个字节
    public byte[] getData(int length, int offset) {
        ByteBuffer nbuf = this.buf.slice();
        nbuf.position(offset);
        //要读取的长度比缓冲区剩余的数据还要多, 最多就只读取缓冲区的那些了.
        //因为缓冲区的remainning的限制,如果要读取比remainning还要多的数据,是做不到的.
        int rl = (length - nbuf.remaining() > 0) ? nbuf.remaining():length;
        byte[] data = new byte[rl];
        nbuf.get(data);
        return data;
    }

    public byte getData(int offset){
        ByteBuffer nbuf = this.buf.slice();
        nbuf.position(offset);
        return nbuf.get();
    }

    public Long getLong(int offset) {
        ByteBuffer nbuf = this.buf.slice();
        nbuf.position(offset);
        return nbuf.getLong();
    }

    /*
     * remove record and add to free pool, if record is the last record
     * 删除一条记录,要在内存缓冲区中将这条记录(在内存缓冲区的开始位置)的第一个字节标记为空闲
     * 然后要将这条记录加入到空闲池中. fp接受的是RecordIndex. 而不是记录本身.
     * 通过RecordIndex可以方便地获取到offset用于更新空闲标记.
     */
    public void removeRecord(RecordIndex rec, int used) {
        this.putData(Record.MAGICFB,rec.offset());
        this.used -= used;
        this.count --;
        this.fp.add(rec.getBucket());
    }

    /*
     * if the memory block is active, before removeRecord you can mergeLastRecord
     * 如果当前内存块是激活的,在删除记录前,可以对最后一条记录进行合并
     * mergetLastRecord(lastRecordIndex)
     * removeRecord(lastRecordIndex)
     */
    public boolean mergeLastRecord(RecordIndex rec, int used) {
        // the rec is last record. buf_append是内存块的最后一个字节.
        // 最后一条记录的offset+capacity=最后一条记录的endpoint.即整个内存块的endpoint
        if (rec.offset() + rec.capacity() == buf_append.position()) {
            //定位到最后一条记录的开始位置.这样下一条记录如果写数据的话,会直接覆盖最后一条记录.
            //我们并没有像removeRecord那样在offset位置标记空闲,并加入到fp中.而是直接覆盖!
            this.buf_append.position(rec.offset());
            //删除掉最后一条记录.所以计数器都要减少
            this.used -= used;
            this.count --;
            //返回true,表示合并成功,就不会调用removeRecord了.
            return true;
        }
        return false;
    }

	/*
	 * add remaining free memory to free pool, and return record
	 * 添加剩余的空闲内存到空闲块池中,并返回当前记录(RecordIndex)
	 */
	public RecordIndex remainToRecord(int index) {
        //构造RecordIndex时,指定三个字段,其中bucket的计算是根据三个字段组合起来的
		RecordIndex record = new RecordIndex()
			.setCapacity(this.remaining())    //要把剩余的都给这条记录,应该在内存块的末尾调用该方法
			.setOffset(buf_append.position()) //buf_append的位置是要写入记录的offset.
			.setIndex(index);                 //指定是哪个内存块
        //在指定位置修改标记位为空闲. 因为剩余的空间要被保留(不够写)
		this.putData(Record.MAGICFB, record.offset());
        //添加到空闲块池中是record的bucket. 要从空闲池中获取,只要根据bucket反解析即可得到RecordIndex
		this.fp.add(record.getBucket());
		return record;
	}

	/*
	 * find free block by length (best fit), if free block is too large, would to split
	 * 给定长度,寻找最适合的块. 如果空闲块太大,则进行分裂.
	 *
	 * 注意:调用该方法后,如果没有分裂,则找到的那个空闲块会从fp中删除,并返回给客户端
	 * 如果空闲块太大,分裂后,假设原先有一个空闲块,调用该方法后,还会剩余一个新分裂出来的空闲块.
	 * 但是注意返回给客户端的是客户端要求长度的空闲块.而不是新分裂出的空闲块.
	 * 比如原先空闲块=59, 要求length=27, 则返回给客户端的是length=27的空闲块,并分裂出新的空闲块,大小=32
	 */
	public RecordIndex findFreeBlock(int length) {
        //什么时候没有空闲块? 数据一直put,没有remove,则没有空闲块. 空闲块发生在remove的时候,或内存块的最后几个字节
		if (this.fp.size() == 0) return null;

        //先构造一个大小满足给定长度的RecordIndex. 首先要求记录的长度=length.
		RecordIndex fake = new RecordIndex().setCapacity(length);
        //向上取整.不能向下,因为不能比要求的还少.少了就不干
		Long b = this.fp.ceiling(fake.getBucket());
		RecordIndex rec = null;
		if (b != null) {
            //传给RecordIndex的是bucket,
			rec = new RecordIndex(b);
            //确保记录的标记位是空闲标记.在放入fp空闲池时,进行了更新操作
			assert this.getData(rec.offset()) == Record.MAGICFB;

            //根据RecordIndex的bucket可以反解析出RecordIndex的三个字段
            //找到空闲块,要从fp中删除. 因为符合条件的空闲块将用于写数据.所以当前空闲块就不是空闲的了
			this.fp.remove(rec.getBucket());
			int rsiz = rec.capacity(); //record size
			// split: 当前找到的空闲块的大小比我们需要释放的块的2倍空间还要多
            System.out.println("rsize:"+rsiz + "|length:"+length + "|分裂否:"+(rsiz >= length * 2));
            if (rsiz >= length * 2) {
				int offset = rec.offset();
                //rec是要返回的,nrec是新分裂出的RecordIndex,因为rsiz足够大,所以将空闲块分成rec和nrec
                //|<------------rsiz--------->|
                //|offset     |offset+length
                //|    rec    |     nrec      |
                //|<-length-->|
                //            |<-rsiz-length->|
				RecordIndex nrec = new RecordIndex()
					.setCapacity(rsiz - length)
					.setOffset(offset + length) //新分裂的offset从要返回的offset+要返回的length开始
                    .setIndex(rec.index()); //在同一个内存块里

                //标记这条记录也是空闲块
				this.putData(Record.MAGICFB,nrec.offset());
                //将新分裂的空闲块加入池中
				this.fp.add(nrec.getBucket());

                //最后设置要返回的记录的容量
				rec.setCapacity(length);
			}
		}
		return rec;
	}
	
	/*
	 * check defragment
	 */
	public boolean canDefragment() {
		//TODO
		//(free -freemax)/free
        //什么时候可以整理碎片:空闲块数量超过指定值,
		if ((this.fp.size() > FBMAX) && (((this.used() * 1.0f) / this.capacity()) < FBRATIO)) {
			return true;
		}
		return false;
	}
	
	private boolean checkRecord(MemoryManager mm, int start, int end) {
		int i = start;
		while(i < end) {
			Record rec = mm.getRecord(this, i);
			int capacity = rec.getIndex().capacity();
			i += capacity;
		}
		return i == end;
	}

    /*
	 * auto defragment 碎片整理
	 */
	public void defragment(MemoryManager mm) {
		TreeSet<Long> offs = new TreeSet<Long>(RecordIndex.offsetComparator());
		offs.addAll(this.fp);
		Iterator<Long> iter = offs.iterator();

        //第一个空闲块
		int off = 0;
		RecordIndex rec = null;
		if (iter.hasNext()) {
			rec = new RecordIndex(iter.next());
		}
		assert this.getData(rec.offset()) == Record.MAGICFB;
		
		while(iter.hasNext()) {
            //下一个空闲块
			RecordIndex nrec = new RecordIndex(iter.next());
			
			//shift offset
			mm.shiftRecord(this, off + rec.capacity(), rec.offset() + rec.capacity(), nrec.offset());
				
			//move data
			ByteBuffer nbuf = this.buf.slice();
			nbuf.position(rec.offset() - off);
				
			ByteBuffer kbuf = nbuf.slice();
			kbuf.position(off + rec.capacity());
			kbuf.limit(off + nrec.offset() - rec.offset());
			kbuf.compact();
				
			assert checkRecord(mm, rec.offset() - off, nrec.offset() - rec.capacity() - off);
			off += rec.capacity();
			rec = nrec;
		}
		
		rec.setCapacity(rec.capacity() + off);
		rec.setOffset(rec.offset() - off);
		this.putData(Record.MAGICFB, rec.offset());
		this.fp.clear();
		this.fp.add(rec.getBucket());
	}
	
	/*
	private byte[] getRecordBuffer(byte[] key, byte[] value) {
		int siz = key.length + value.length + 1;
		byte[] brec = new byte[siz];
		brec[0] = Record.MAGICREC;
		System.arraycopy(key, 0, brec, 1, key.length);
		System.arraycopy(value, 0, brec, 1 + key.length, value.length);
		return brec;
	}
	*/

    //二进制备份
	public String hexDump() {
		ByteBuffer nbuf = this.buf.slice();
        //要用buf_append的position,因为put时,数据追加到buf_append,如果用buf.position=0
		byte[] arr = new byte[buf_append.position()];
        //从buf中获取指定大小的数据赋值到arr字节数组中. 因为buf_append和buf共享数据,所以buf中也有数据
		nbuf.get(arr);
		return Util.hexDump(arr, 0, arr.length);
	}
	
}
