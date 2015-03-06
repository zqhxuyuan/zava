package com.zqh.java.nonheapdb;

import java.nio.ByteBuffer;

public class Record {
	public static final byte MAGICREC = (byte) 0xC8;
	public static final byte MAGICFB = (byte) 0xB0;
	public static final int MAXHEAD = 24;
	public static final int READUNIT = 64;
	public static final int MAXRECORDSIZE = 1 << 16 - 1;
	
	private RecordIndex index;
	private RecordInfo info;
	private String key;
	private byte data[];
	private RecordIndex parent;
	private long next;
	
	private boolean free;
	
	public Record() {
		this.info = new RecordInfo();
	}
	
	public RecordIndex getIndex() {
		return index;
	}
	
	public RecordInfo getInfo() {
		return info;
	}
	
	public boolean isFree() {
		return this.free;
	}
	
	public long getNext() {
		return this.next;
	}
	
	public Record setNext(long next) {
		this.next = next;
		return this;
	}
	
	public Record setIndex(RecordIndex index) {
		this.index = index;
		return this;
	}

	public String getKey() {
		return key;
	}
	
	public Record setKey(String key) {
		this.key = key;
		return this;
	}

	public byte[] getData() {
		return data;
	}

	public Record setData(byte data[]) {
		this.data = data;
		return this;
	}

	public RecordIndex getParent() {
		return parent;
	}

	public void setParent(RecordIndex parent) {
		this.parent = parent;
	}
	
	public ByteBuffer getBuffer() {
		int rsiz = MAXHEAD + this.key.length() + this.data.length;
		ByteBuffer nbuf = ByteBuffer.allocate(rsiz);
		nbuf.put(MAGICREC);
		nbuf.putLong(this.next);
		Util.writeVarInt(this.key.length(), nbuf);
		Util.writeVarInt(this.data.length, nbuf);
		assert nbuf.position() < MAXHEAD;
		nbuf.put(this.key.getBytes());
		nbuf.put(this.data);
		nbuf.flip();
		return nbuf;
	}

	public int setBuffer(ByteBuffer nbuf) {
		byte magic = nbuf.get();
		if(magic == MAGICFB) {
			this.free = true;
		} else if(magic == MAGICREC) {
			this.free = false;
		} else {
			assert false;
		}
		this.next = nbuf.getLong();
		this.info.ksize = Util.readVarInt(nbuf);
		this.info.kstep = nbuf.position() - 8 - 1;
		this.info.vsize = Util.readVarInt(nbuf);
		this.info.vstep = nbuf.position() - info.kstep - 8 - 1;
		
		//check has read key?
		if (nbuf.limit() > nbuf.position() + this.info.ksize) {
			this.key = new String(nbuf.array(), nbuf.position(), this.info.ksize);
			nbuf.position(nbuf.position() + this.info.ksize);
		}
		//check has read value?
		if (nbuf.limit() > nbuf.position() + this.info.vsize) {
			this.data = new byte[this.info.vsize];
			nbuf.get(this.data);
		}
		return this.info.hsize();
	}
	
	public static class RecordInfo{
		int vsize;
		int vstep;
		int ksize;
		int kstep;
		
		public final int hsize() {
			return vstep + kstep + 8 + 1;
		}
		
		public final int used() {
			return vsize + vstep + ksize + kstep + 8 + 1;
		}
	}
}
