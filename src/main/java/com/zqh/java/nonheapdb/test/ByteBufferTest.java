package com.zqh.java.nonheapdb.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.zqh.java.nonheapdb.*;
import junit.framework.TestCase;

public class ByteBufferTest extends BasedTest {

	public ByteBufferTest(final String name) {
		super(name);
	}

	public void testBasic() {
		int ksize = 4;
		int vsize = 12;
		byte[] key = this.generateTestData(ksize);
		byte[] value = this.generateTestData(vsize);

		MemoryBuffer buffer = MemoryBuffer.makeNew(Util.Kb(16));
		assertEquals(Util.Kb(16), buffer.capacity());

		// add key1
		RecordIndex rec = writeRecord(buffer, key, value);
		assertEquals(rec.offset(), 0);
		assertEquals(buffer.fpsize(), 0);

		// add key2
		int ksize2 = ksize * 3;
		int vsize2 = vsize * 3;
		byte[] key2 = this.generateTestData(ksize2);
		byte[] value2 = this.generateTestData(vsize2);
		RecordIndex rec2 = writeRecord(buffer, key2, value2);
		assertEquals(rec2.offset(), rec.capacity());

		// add key3
		int ksize3 = 12;
		int vsize3 = 32;
		byte[] key3 = this.generateTestData(ksize3);
		byte[] value3 = this.generateTestData(vsize3);
		RecordIndex rec3 = writeRecord(buffer, key3, value3);
		assertEquals(rec3.offset(), rec.capacity() + rec2.capacity());

		// remove key2
		int used = buffer.used();
		buffer.removeRecord(rec2, rec2.capacity());
		TestCase.assertEquals(buffer.getData(rec2.offset()), Record.MAGICFB);
		assertEquals(used - buffer.used(), rec2.capacity());
		assertEquals(buffer.fpsize(), 1);

		// find free block
		RecordIndex frec = buffer.findFreeBlock(rec2.capacity());
		assertNotNull(frec);
		assertEquals(frec.offset(), rec2.offset());
		assertEquals(frec.capacity(), rec2.capacity());
		assertEquals(buffer.fpsize(), 0);

		// add key2
		writeRecord(buffer, key2, value2, frec.offset());
		checkRecord(buffer, frec, key2, value2);

		// remove key2
		used = buffer.used();

		int cap2 = rec2.capacity();
		buffer.removeRecord(rec2, cap2);
		assertEquals(buffer.getData(rec2.offset()), Record.MAGICFB);
		assertEquals(used - buffer.used(), rec2.capacity());
		assertEquals(buffer.fpsize(), 1);

		// add key1 (test split)
		frec = buffer.findFreeBlock(rec.capacity());
		assertEquals(frec.offset(), rec2.offset());
		assertEquals(frec.capacity(), rec.capacity());
		assertEquals(buffer.fpsize(), 1);
		writeRecord(buffer, key, value, frec.offset());

		// add key1
		RecordIndex nfrec = buffer.findFreeBlock(rec.capacity());
		assertEquals(nfrec.offset(), frec.offset() + frec.capacity());
		assertEquals(nfrec.capacity(), cap2 - frec.capacity());
		writeRecord(buffer, key, value, frec.offset());

		assertEquals(buffer.used(), rec.capacity() * 3 + rec3.capacity());
		assertEquals(buffer.remaining(), buffer.capacity()
				- (rec.capacity() + cap2 + rec3.capacity()));
		System.out.println(buffer.hexDump());
	}

	public void testManager() {
		DBCache.BucketManager bm = new DBCache.BucketManager(16);
		MemoryManager mm = new MemoryManager(bm, Util.Kb(1), -1);
		HashMap<String, byte[]> maps = new HashMap<String, byte[]>();

		int used = 0;
		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis() ^ System.nanoTime());
		// add data
		for (int i = 0; i < 10000; i++) {

			String key = this.generateKey(i);
			Record rec = mm.getRecord(new String(key));
			assertNull(rec);

			int vsiz = rand.nextInt(512);
			byte[] value = this.generateTestData(vsiz);
			assertTrue(mm.put(key, value));
			rec = mm.getRecord(new String(key));
			assertEqualContent(rec.getData(), value);
			assertNull(maps.put(key, value));

			used += rec.getInfo().used();
		}

		// check data
		for (String key : maps.keySet()) {
			Record rec = mm.getRecord(key);
			assertNotNull(rec);
			assertEqualContent(maps.get(key), rec.getData());
		}

		// remove data
		ArrayList<String> rkeys = new ArrayList<String>();
		int j = 0;
		for (String key : maps.keySet()) {
			if (j++ < 100) {
				Record rec = mm.getRecord(key);
				assertNotNull(rec);
				used -= rec.getInfo().used();

				mm.removeRecord(key);
				assertNull(mm.getRecord(key));

				rkeys.add(key);
			}
		}

		for (String key : rkeys) {
			maps.remove(key);
		}

		// check data
		for (String key : maps.keySet()) {
			Record rec = mm.getRecord(key);
			assertNotNull(rec);
			assertEqualContent(maps.get(key), rec.getData());
		}

		for (int i = 10000; i < 20000; i++) {
			String key = this.generateKey(i);
			Record rec = mm.getRecord(new String(key));
			assertNull(rec);

			int vsiz = rand.nextInt(512);
			byte[] value = this.generateTestData(vsiz);
			assertTrue(mm.put(key, value));
			rec = mm.getRecord(new String(key));
			assertEqualContent(rec.getData(), value);
			assertNull(maps.put(key, value));

			used += rec.getInfo().used();
		}

		// check data
		for (String key : maps.keySet()) {
			Record rec = mm.getRecord(key);
			assertNotNull(rec);
			assertEqualContent(maps.get(key), rec.getData());
		}

		assertEquals(used, mm.used());
		assertEquals(maps.size(), mm.reccount());
	}

	public void testDefragment() {
		DBCache.BucketManager bm = new DBCache.BucketManager(16);
		MemoryManager mm = new MemoryManager(bm, Util.Mb(1), -1);
		HashMap<String, byte[]> maps = new HashMap<String, byte[]>();

		int used = 0;
		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis() ^ System.nanoTime());
		// add data
		for (int i = 0; i < MemoryBuffer.FBMAX * 10; i++) {
			String key = this.generateKey(i);
			Record rec = mm.getRecord(new String(key));
			if (rec != null) {
				continue;
			}

			int vsiz = rand.nextInt(64);
			byte[] value = this.generateTestData(vsiz);
			assertTrue(mm.put(key, value));
			rec = mm.getRecord(new String(key));
			assertEqualContent(rec.getData(), value);
			assertNull(maps.put(key, value));

			used += rec.getInfo().used();
		}
		assertEquals(mm.bsize(), 1);

		// remove data
		ArrayList<String> rkeys = new ArrayList<String>();
		int j = 0;

		for (String key : maps.keySet()) {
			if (j++ < MemoryBuffer.FBMAX * 2) {
				Record rec = mm.getRecord(key);
				assertNotNull(rec);
				used -= rec.getInfo().used();
				mm.removeRecord(key);
				assertNull(mm.getRecord(key));

				rkeys.add(key);
			}
		}

		for (String key : rkeys) {
			maps.remove(key);
		}

		assertTrue((used * 1.0f) / Util.Mb(1) < MemoryBuffer.FBRATIO);

		// add large data
		for (int i = MemoryBuffer.FBMAX * 10; i < MemoryBuffer.FBMAX * 10 + 100; i++) {
			String key = this.generateKey(i);
			Record rec = mm.getRecord(new String(key));
			if (rec != null) {
				continue;
			}

			int vsiz = 64 + rand.nextInt(20);
			byte[] value = this.generateTestData(vsiz);
			assertTrue(mm.put(key, value));
			rec = mm.getRecord(new String(key));
			assertEqualContent(rec.getData(), value);
			assertNull(maps.put(key, value));

			used += rec.getInfo().used();
		}

		// check data
		for (String key : maps.keySet()) {
			Record rec = mm.getRecord(key);
			assertNotNull(rec);
			assertEqualContent(maps.get(key), rec.getData());
		}

		assertEquals(used, mm.used());
		assertEquals(maps.size(), mm.reccount());
	}
}
