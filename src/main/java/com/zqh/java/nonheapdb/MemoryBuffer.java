package com.zqh.java.nonheapdb;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.TreeSet;

//buffer block
public class MemoryBuffer {
	private int used; 							// record used byte
	private int count;							// record count
	
	private ByteBuffer buf; 					// record buffer,position is zero
	private ByteBuffer buf_append;  			// sliced record buffer,position is last record,only for append

	/*free pool config */
	//TODO LongTreeSet(reduce boxing)
	private TreeSet<Long> fp;     		// record free pool,sort with size desc(free block not merge and split)
	public static final int FBMAX = 64;			// fb max cout
	public static final float FBRATIO = 0.75f;  // if fbRatio exceed,would process auto defragment

	private MemoryBuffer(ByteBuffer buf) {
		this.buf = buf;
		this.buf_append = buf.slice();
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
	/*
	 * add remaining free memory to free pool, and return record
	 */
	public RecordIndex remainToRecord(int index) {
		RecordIndex record = new RecordIndex()
			.setCapacity(this.remaining())
			.setOffset(buf_append.position())
			.setIndex(index);
		this.putData(Record.MAGICFB, record.offset());
		this.fp.add(record.getBucket());
		return record;
	}

	/*
	 * find free block by length (best fit), if free block is too large, would to split
	 */
	public RecordIndex findFreeBlock(int length) {
		if (this.fp.size() == 0) {
			return null;
		}
		
		RecordIndex fake = new RecordIndex().setCapacity(length);
		Long b = this.fp.ceiling(fake.getBucket());
		RecordIndex rec = null;
		if (b != null) {
			rec = new RecordIndex(b);
			assert this.getData(rec.offset()) == Record.MAGICFB;
			
			this.fp.remove(rec.getBucket());
			int rsiz = rec.capacity();
			// split
			if (rsiz >= length * 2) {
				int offset = rec.offset();
				RecordIndex nrec = new RecordIndex()
					.setCapacity(rsiz - length)
					.setOffset(offset + length)
					.setIndex(rec.index());
				
				this.putData(Record.MAGICFB,nrec.offset());
				this.fp.add(nrec.getBucket());

				rec.setCapacity(length);
			}
		}
		return rec;
	}


	/*
	 * remove record and add to free pool, if record is the last record
	 */
	public void removeRecord(RecordIndex rec, int used) {
		this.putData(Record.MAGICFB,rec.offset());
		this.used -= used;
		this.count --;
		this.fp.add(rec.getBucket());
	}

	/*
	 * if the memory block is active, before removeRecord you can mergeLastRecord
	 */
	public boolean mergeLastRecord(RecordIndex rec, int used) {
		// last record
		if (rec.offset() + rec.capacity() == buf_append.position()) {
			this.buf_append.position(rec.offset());
			this.used -= used;
			this.count --;
			return true;
		}
		return false;
	}
	
	/*
	 * check defragment
	 */
	public boolean canDefragment() {
		//TODO
		//(free -freemax)/free
		if ((this.fp.size() > FBMAX) &&
				(((this.used() * 1.0f) / this.capacity()) < FBRATIO)
			) {
			return true;
		}
		return false;
	}
	
	/*
	 * auto defragment
	 */
	
	private boolean checkRecord(MemoryManager mm, int start, int end) {
		int i = start;
		while(i < end) {
			Record rec = mm.getRecord(this, i);
			int capacity = rec.getIndex().capacity();
			i += capacity;
		}
		
		return i == end;
	}
	
	public void defragment(MemoryManager mm) {
		TreeSet<Long> offs = new TreeSet<Long>(RecordIndex.offsetComparator());
		offs.addAll(this.fp);
		Iterator<Long> iter = offs.iterator();
		
		int off = 0;
		RecordIndex rec = null;
		if (iter.hasNext()) {
			rec = new RecordIndex(iter.next());
		}
		assert this.getData(rec.offset()) == Record.MAGICFB;
		
		while(iter.hasNext()) {
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
	
	public RecordIndex putData(ByteBuffer nbuf) {
		RecordIndex record = new RecordIndex()
		.setOffset(buf_append.position())
		.setCapacity(nbuf.limit());
		this.buf_append.put(nbuf);
		this.used += record.capacity();
		this.count++;
		return record;
	}
	
	public void putData(ByteBuffer rbuf, int offset) {
		ByteBuffer nbuf = this.buf.slice();
		nbuf.position(offset);
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

	public byte[] getData(int length, int offset) {
		ByteBuffer nbuf = this.buf.slice();
		nbuf.position(offset);
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
	
	public String hexDump() {
		ByteBuffer nbuf = this.buf.slice();
		byte[] arr = new byte[buf_append.position()];
		nbuf.get(arr);
		return Util.hexDump(arr, 0, arr.length);
	}
	
}
