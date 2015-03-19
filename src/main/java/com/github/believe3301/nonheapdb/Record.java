package com.github.believe3301.nonheapdb;

import java.nio.ByteBuffer;

public class Record {

    public static class RecordInfo{
        int vsize;
        int vstep;
        int ksize;
        int kstep;

        //head size: ksize和vsize不计算在内
        //计算的是key|value之前的MAGIC|next|keyLen|valLen占用的字节大小
        public final int hsize() {
            return vstep + kstep + 8 + 1;
        }

        //整个记录占用的大小.要把ksize,vsize计算在内了. 下面第一行是记录的格式,第二行是每个字段占用的大小
        //MAGIC|next|keyLen|valLen|  key |value
        // 1     8    kstep  vstep  ksize vsize
        public final int used() {
            return vsize + vstep + ksize + kstep + 8 + 1;
        }
    }

	public static final byte MAGICREC = (byte) 0xC8;
	public static final byte MAGICFB = (byte) 0xB0; //标示这是一个free block. FB
	public static final int MAXHEAD = 24;
	public static final int READUNIT = 64;
	public static final int MAXRECORDSIZE = 1 << 16 - 1;

    //一条记录要包含key和value.数据是字节数组,key是字符串
    private String key;
    private byte data[];

    private RecordInfo info;
	private RecordIndex index;
	private RecordIndex parent;
	private long next;
	
	private boolean free;

    public Record() {
        this.info = new RecordInfo();
    }

    public RecordInfo getInfo() {
        return info;
    }
    public String getKey() {
        return key;
    }
    public byte[] getData() {
        return data;
    }
    public RecordIndex getIndex() {
        return index;
    }
    public boolean isFree() {
        return this.free;
    }
    public long getNext() {
        return this.next;
    }
    public RecordIndex getParent() {
        return parent;
    }

	public Record setNext(long next) {
		this.next = next;
		return this;
	}
	
	public Record setIndex(RecordIndex index) {
		this.index = index;
		return this;
	}

	public Record setKey(String key) {
		this.key = key;
		return this;
	}

	public Record setData(byte data[]) {
		this.data = data;
		return this;
	}

	public void setParent(RecordIndex parent) {
		this.parent = parent;
	}

    //这条记录的字节格式
	public ByteBuffer getBuffer() {
        //一条记录的长度: HEAD + key和value的长度
		int rsiz = MAXHEAD + this.key.length() + this.data.length;
		ByteBuffer nbuf = ByteBuffer.allocate(rsiz);
		nbuf.put(MAGICREC); // 1
		nbuf.putLong(this.next); // 8
		Util.writeVarInt(this.key.length(), nbuf); //key.length的值
		Util.writeVarInt(this.data.length, nbuf); //value.length的值
		assert nbuf.position() < MAXHEAD; //keyLen+valLen占用的长度不能超过15.即最多14.
		nbuf.put(this.key.getBytes()); //key
		nbuf.put(this.data);//value
		nbuf.flip();
		return nbuf;
	}

	public int setBuffer(ByteBuffer nbuf) {
		byte magic = nbuf.get();//第一个字节标示是否是空闲块
		if(magic == MAGICFB) {
			this.free = true;
		} else if(magic == MAGICREC) {
			this.free = false;
		} else {
			assert false;
		}
		this.next = nbuf.getLong();//long类型,8个字节

        //keyLength和keySize是不一样的概念. keyLength表示写入的key一共有多少个字节.
        //而keySize是keyLength的值占用了多少个字节. 比如key="12345",则keyLength=5.
        //而keySize=1.因为5这个数字只占用了一个字节.

		this.info.ksize = Util.readVarInt(nbuf);//keyLength.只是读取里面的值.
        //由于写入时是varInt,所以要知道keyLength本身占用了多少个字节
        //MAGIC|next|keyLength|valLength|key|value
        // 1      8           |pos
        //          |<------->|
        //             kstep
        this.info.kstep = nbuf.position() - 8 - 1;
		this.info.vsize = Util.readVarInt(nbuf);
        //MAGIC|next|keyLength|valLength|key|value
        // 1      8                     |pos
        //          |<------->|<------->|
        //              kstep     vstep=pos-1-8-kstep
		this.info.vstep = nbuf.position() - info.kstep - 8 - 1;
		
		//check has read key?
		if (nbuf.limit() > nbuf.position() + this.info.ksize) {
            //当前缓冲区的position位置是key的开始位置,一共要读取ksize个字节,ksize是存储在keyLength里
			this.key = new String(nbuf.array(), nbuf.position(), this.info.ksize);
			nbuf.position(nbuf.position() + this.info.ksize);
		}
		//check has read value?
		if (nbuf.limit() > nbuf.position() + this.info.vsize) {
            //key通过字符串的offset,size获取,data由于直接是字节数组,直接获取
			this.data = new byte[this.info.vsize]; //声明data字节数组的大小为vsize
			nbuf.get(this.data);//从缓冲区中获取出vsize大小的数组,设置到data里
		}
		return this.info.hsize();
	}
}
