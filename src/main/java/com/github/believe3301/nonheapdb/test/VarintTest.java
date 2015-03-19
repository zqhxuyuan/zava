package com.github.believe3301.nonheapdb.test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

import com.github.believe3301.nonheapdb.Util;
import junit.framework.TestCase;

public class VarintTest extends TestCase {
	
	public VarintTest(final String name) {
        super(name);
    }

	public void testVarint() {
		byte[] array = new byte[10];
		ByteBuffer buffer = ByteBuffer.wrap(array);
		for (int i = 0; i <= Short.MAX_VALUE; i++) {
			testVarint(buffer, i);
		}
		testVarint(buffer, Integer.MAX_VALUE / 2);
		testVarint(buffer, Integer.MAX_VALUE -1);
		testVarint(buffer, Integer.MAX_VALUE);
		
		
		for (int i = 0; i <= Short.MAX_VALUE; i++) {
			testVarLong(buffer, (long)i);
		}
		testVarLong(buffer, (long)Integer.MAX_VALUE / 2);
		testVarLong(buffer, (long)Integer.MAX_VALUE -1);
		testVarLong(buffer, (long)Integer.MAX_VALUE);
		testVarLong(buffer, (long)Integer.MAX_VALUE +1);
		testVarLong(buffer, (long)Long.MAX_VALUE / 2);
		testVarLong(buffer, (long)Long.MAX_VALUE -1);
		testVarLong(buffer, (long)Long.MAX_VALUE);
		
		//test write more data
		array = new byte[50];
		buffer = ByteBuffer.wrap(array);
		Random ran = new Random();
		ArrayList<Integer> values = new ArrayList<Integer>();
		for (int i = 0; i < 10; i++) {
			int value = ran.nextInt(Integer.MAX_VALUE) + 1;
			Util.writeVarInt(value, buffer);
			values.add(value);
		}
		buffer.flip();
		for (int i = 0; i < 10; i++) {
			int value = Util.readVarInt(buffer);
			assertEquals("read seq failed",new Integer(value), values.get(i));
		}
	}

	private void testVarLong(ByteBuffer buffer, long l) {
		Util.writeVarLong(l, buffer);
		buffer.flip();
		assertEquals("read failed", Util.readVarLong(buffer) ,l);
	
		
		byte[] arr = Util.writeVarLong(l);
		assert arr.length == buffer.limit();
		assert Util.readVarLong(arr) == l;
		
		buffer.clear();
	}
	
	private void testVarint(ByteBuffer buffer, int i) {
		Util.writeVarInt(i, buffer);
		buffer.flip();
		assertEquals("read failed", Util.readVarInt(buffer) ,i);
	
		
		byte[] arr = Util.writeVarInt(i);
		assert arr.length == buffer.limit();
		assert Util.readVarInt(arr) == i;
		
		buffer.clear();
	}
	
	public void testHexDump() {
		String s = "helloworld,fuck your mother, suck my dick";
		System.out.println(Util.hexDump(s.getBytes(), 0, s.length()));
	}
}
