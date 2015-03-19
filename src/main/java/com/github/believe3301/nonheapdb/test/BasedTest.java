package com.github.believe3301.nonheapdb.test;

import java.util.Arrays;
import java.util.Random;

import com.github.believe3301.nonheapdb.MemoryBuffer;
import com.github.believe3301.nonheapdb.Record;
import com.github.believe3301.nonheapdb.Util;
import junit.framework.TestCase;
import com.github.believe3301.nonheapdb.RecordIndex;

public abstract class BasedTest extends TestCase {

	public BasedTest(final String name) {
        super(name);
    }
	
	public String generateKey(long n) {
		return String.format("%016d", n);
	}
	
	protected byte[] generateTestData(final int size) {
		byte[] data = new byte[size];
		
		Random rand = new Random();
		int start = 33 +  rand.nextInt(95);
		for (int i = 0; i < size; i++) {
			data[i] = (byte) (((start + i) % 127) + 1);
		}
		return data;
	}

	protected void assertEqualContent(byte[] b1, byte[] b2) {
		if (b1 != null) {
			assertNotNull(b2);
			assertEquals("length mismatch", b1.length, b2.length);
			assertTrue("content mismatch", Arrays.equals(b1, b2));
		} else {
			assertNull(b2);
		}
	}

    /**
     * 检查记录按照一定的格式存储
     * @param buffer 内存缓冲区. 注意因为内存是共享的,所以不是指当前记录,通过这个内存缓冲区可以定位到任何一条记录
     * @param rec 索引,可以获取capacity,offset
     * @param key 记录的key
     * @param value 记录的value
     */
	protected void checkRecord(MemoryBuffer buffer, RecordIndex rec, byte[] key, byte[] value){
		byte[] ksbuf = Util.writeVarInt(key.length);
		byte[] vsbuf = Util.writeVarInt(value.length);
        //kstep + vstep + 1 + ksize + vsize + 8
		int capacity = ksbuf.length + vsbuf.length + 1 + key.length + value.length + 8;
		assertEquals(rec.capacity(), capacity);
		
		//每条记录的offset是一个MAGIC标记
		TestCase.assertEquals(buffer.getData(rec.offset()), Record.MAGICREC);
		assertTrue(buffer.getLong(rec.offset() + 1) == 0L);//next

        //kstep
        assertEqualContent(buffer.getData(ksbuf.length, rec.offset() + 8 + 1), ksbuf);
		//vstep
        assertEqualContent(buffer.getData(vsbuf.length, rec.offset() + ksbuf.length + 8 + 1), vsbuf);
		//ksize
        assertEqualContent(buffer.getData(key.length, rec.offset() + ksbuf.length + vsbuf.length + 8 + 1), key);
		//vsize
        assertEqualContent(buffer.getData(value.length, rec.offset() + ksbuf.length + vsbuf.length + 8 + key.length + 1), value);
	}

    /**
     * 写一条记录
     * @param buffer
     * @param key
     * @param value
     * @return
     */
	protected RecordIndex writeRecord(MemoryBuffer buffer, byte[] key, byte[] value) {
		int used = buffer.used();
        //新创建一条记录
		Record rec = new Record();
		rec.setData(value);
		rec.setKey(new String(key));

        //将记录的字节数组添加到内存中的缓冲区中,返回这条记录的索引信息
		RecordIndex ridx = buffer.putData(rec.getBuffer());
        //索引记录了记录的容量=添加记录后的容量-添加记录前的容量
		assertEquals(ridx.capacity(), buffer.used() - used);
        //检查这条记录是否按照指定的格式存入
		checkRecord(buffer, ridx, key, value);
		return ridx;
	}
	
	protected void writeRecord(MemoryBuffer buffer, byte[] key, byte[] value, int offset) {
		Record rec = new Record();
		rec.setData(value);
		rec.setKey(new String(key));
        //在指定的位置将记录写入内存的缓冲区中
		buffer.putData(rec.getBuffer(), offset);
	}
}
