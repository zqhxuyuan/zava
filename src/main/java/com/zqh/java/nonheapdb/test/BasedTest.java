package com.zqh.java.nonheapdb.test;

import java.util.Arrays;
import java.util.Random;

import com.zqh.java.nonheapdb.MemoryBuffer;
import com.zqh.java.nonheapdb.Record;
import junit.framework.TestCase;
import com.zqh.java.nonheapdb.RecordIndex;
import com.zqh.java.nonheapdb.Util;

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
	
	protected void checkRecord(MemoryBuffer buffer, RecordIndex rec, byte[] key, byte[] value){
		byte[] ksbuf = Util.writeVarInt(key.length);
		byte[] vsbuf = Util.writeVarInt(value.length);
		int capacity = ksbuf.length + vsbuf.length + 1 + key.length + value.length + 8;
		assertEquals(rec.capacity(), capacity);
		
		
		TestCase.assertEquals(buffer.getData(rec.offset()), Record.MAGICREC);
		assertTrue(buffer.getLong(rec.offset() + 1) == 0L);
		assertEqualContent(buffer.getData(ksbuf.length, rec.offset() + 8 + 1), ksbuf);
		assertEqualContent(buffer.getData(vsbuf.length, rec.offset() + ksbuf.length + 8 + 1), vsbuf);
		assertEqualContent(buffer.getData(key.length, rec.offset() + ksbuf.length + vsbuf.length + 8 + 1), key);
		assertEqualContent(buffer.getData(value.length, rec.offset() + ksbuf.length + vsbuf.length + 8 + key.length + 1), value);
	}
	
	protected RecordIndex writeRecord(MemoryBuffer buffer, byte[] key, byte[] value) {
		int used = buffer.used();
		Record rec = new Record();
		rec.setData(value);
		rec.setKey(new String(key));
		RecordIndex ridx = buffer.putData(rec.getBuffer());
		assertEquals(ridx.capacity(), buffer.used() - used);
		checkRecord(buffer, ridx, key, value);
		return ridx;
	}
	
	protected void writeRecord(MemoryBuffer buffer, byte[] key, byte[] value, int offset) {
		Record rec = new Record();
		rec.setData(value);
		rec.setKey(new String(key));
		buffer.putData(rec.getBuffer(), offset);
	}
}
