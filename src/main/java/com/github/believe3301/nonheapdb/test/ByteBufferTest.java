package com.github.believe3301.nonheapdb.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.github.believe3301.nonheapdb.*;
import junit.framework.TestCase;

public class ByteBufferTest extends BasedTest {

	public ByteBufferTest(final String name) {
		super(name);
	}

	public void testBasic() {
        //一条KV记录的key有4个字节,value有12个字节.
		int ksize = 4;
		int vsize = 12;
		byte[] key = this.generateTestData(ksize);
		byte[] value = this.generateTestData(vsize);

        //内存缓冲区只分配了16kb? 够吗???
        //足够了,上面的key,value加起来有4+12=16bytes(虽然不包括head部分).注意单位差了1024倍! 1kb=1024byte
		MemoryBuffer buffer = MemoryBuffer.makeNew(Util.Kb(16));
		assertEquals(Util.Kb(16), buffer.capacity()); //内存缓冲区的容量=创建时指定的大小

		// 1. add key1
		RecordIndex rec = writeRecord(buffer, key, value);
		assertEquals(rec.offset(), 0); //第一条记录的offset在文件的开始位置
		assertEquals(buffer.fpsize(), 0);
        //rec的capacity=27=16+9+1+1

		// 2. add key2
		int ksize2 = ksize * 3; //12
		int vsize2 = vsize * 3; //36
		byte[] key2 = this.generateTestData(ksize2);
		byte[] value2 = this.generateTestData(vsize2);
		RecordIndex rec2 = writeRecord(buffer, key2, value2);
		assertEquals(rec2.offset(), rec.capacity()); //第二条记录的开始位置是第一条的长度
        //rec2的capacity=59=48+1+8+1+1

		// 3. add key3
		int ksize3 = 12;
		int vsize3 = 32;
		byte[] key3 = this.generateTestData(ksize3);
		byte[] value3 = this.generateTestData(vsize3);
		RecordIndex rec3 = writeRecord(buffer, key3, value3);
		assertEquals(rec3.offset(), rec.capacity() + rec2.capacity());//第三条的offset=前2条记录的长度总和

		// 4. remove key2
		int used = buffer.used();
		buffer.removeRecord(rec2, rec2.capacity());
		TestCase.assertEquals(buffer.getData(rec2.offset()), Record.MAGICFB);
		assertEquals(used - buffer.used(), rec2.capacity());//remove前-remove后=remove的记录的长度
		assertEquals(buffer.fpsize(), 1); //有一个空闲块

		// 5. find free block
		RecordIndex frec = buffer.findFreeBlock(rec2.capacity());//rec2是删除的,寻找等于rec2长度的空闲块
		assertNotNull(frec); //一定能找到
		assertEquals(frec.offset(), rec2.offset()); //空闲块的offset就是已经被删除的块的offset
		assertEquals(frec.capacity(), rec2.capacity());
		assertEquals(buffer.fpsize(), 0); //没了吗? 不是应该有一块吗??
        //调用findFreeBlock就会从fb中删除:fp.remove(rec.getBucket()),所以没有空闲块了!

		// 6. add key2
		writeRecord(buffer, key2, value2, frec.offset()); //在空闲块位置添加一条相同长度的记录
		checkRecord(buffer, frec, key2, value2); //检查能否正常放入.因为添加的记录和删除的记录是一样的(测试而已).

		// 7. remove key2
		used = buffer.used();
		int cap2 = rec2.capacity();
		buffer.removeRecord(rec2, cap2); //搞什么鬼,又要删除
		assertEquals(buffer.getData(rec2.offset()), Record.MAGICFB);
		assertEquals(used - buffer.used(), rec2.capacity());
		assertEquals(buffer.fpsize(), 1);//删除后就有一个空闲块了

		// 8. add key1 (test split)
        // 上面5.调用findFreeBlock后没有,因为空闲块的大小=要找的rec2的大小.调用方法后,会从fp中删除
        // 这里调用后为什么还有一块?因为要找的大小是rec,它是空闲块的1/3,调用方法后,虽然删除了,但是会分裂出一块新的空闲块.
        // 注意frec返回的是满足rec.capacity的那个空闲块(1/3),而不是新分裂出来的空闲块(2/3).
		frec = buffer.findFreeBlock(rec.capacity());//rec记录的长度比删除的rec2要小,有没有rec长度的空闲块?
		assertEquals(frec.offset(), rec2.offset());//因为返回的是满足rec.capacity的空闲块,这个块的开始位置是rec2的offset
		assertEquals(frec.capacity(), rec.capacity());//第一个空闲快的大小就是我们要的.
		assertEquals(buffer.fpsize(), 1);//这块空闲块是新分裂出来的吗? 不是!是旧的,因为findFreeBlock返回的是rec,而不是nrec.
		writeRecord(buffer, key, value, frec.offset());//好吧,你可以写了.
        //测试split.如果空闲块的大小>2*写入的记录,则进行拆分

		// 9. add key1
		RecordIndex nfrec = buffer.findFreeBlock(rec.capacity());//还有没有?有!因为在上一步还分裂出一个新的空闲块.这里调用后就没有空闲块了
		assertEquals(nfrec.offset(), frec.offset() + frec.capacity());//接着上一个写入的空闲块的后面
		assertEquals(nfrec.capacity(), cap2 - frec.capacity());//cap2是rec2的大小,frec是上一步rec的大小,剩余的是两者相减
        //照理说新分裂出来的还是rec的2倍.因为rec2是rec的3倍,在上一步找到一个rec,剩余2个rec.应该还满足rsiz >= length * 2,还会分裂?
        //不会的.rec2是rec的3倍,仅仅是key,value.而一条记录还包括head.所以以rec来寻找空闲块时,上一步满足,这一步已经不能满足分裂条件了.
        //59>27(frec)*2=54, 59-27=32(nfrec), 32<27*2
        assertEquals(buffer.fpsize(), 0);
        writeRecord(buffer, key, value, frec.offset());

		assertEquals(buffer.used(), rec.capacity() * 3 + rec3.capacity());//总共写了3次key1,一次key3. key2在来来回回中被删除了
		assertEquals(buffer.remaining(), buffer.capacity() -
                (rec.capacity() + cap2 + rec3.capacity()));//实际用的容量是rec+rec2+rec3.虽然rec2没有写满.buffer指向的position在rec3末尾
		//System.out.println(buffer.hexDump());
	}

    // MemoryManager貌似只有2个方法, put, getRecord
    // MemoryManager管理多个内存块MemoryBuffer,实际的记录操作都在MemoryBuffer中
    // 所以内存管理只是验证put和get数据的正确性. 至于底层内存块中记录是如何申请,如何寻找空闲块,都在上面的测试方法中
	public void testManager() {
        //hashpower=16,capacity=1<<16=65536.buckets桶的大小也是capacity.
        //桶的管理, 设置hash值以及桶的大小
		DBCache.BucketManager bm = new DBCache.BucketManager(16);

        //MM的capacity=1kb, 注意和桶的capacity=1<<16不一样.
		MemoryManager mm = new MemoryManager(bm, Util.Kb(1), -1);
		HashMap<String, byte[]> maps = new HashMap<>();

		int used = 0;
		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis() ^ System.nanoTime());
		// add data
		for (int i = 0; i < 10000; i++) {

			String key = this.generateKey(i);
            //还没开始put,显然get为null
			Record rec = mm.getRecord(new String(key));
			assertNull(rec);

			int vsiz = rand.nextInt(512);
			byte[] value = this.generateTestData(vsiz);
            //put成功
			assertTrue(mm.put(key, value));
			rec = mm.getRecord(new String(key));
			assertEqualContent(rec.getData(), value);

            //用maps保存.将kv放入map中,如果map中没有k,则返回null,如果已经存在,则返回旧的k.
			assertNull(maps.put(key, value));

			used += rec.getInfo().used();
		}

		// check data
		for (String key : maps.keySet()) {
			Record rec = mm.getRecord(key);
			assertNotNull(rec);
            //用map获取k->v,和用mm获取记录的data是一样的.因为上一步将数据分别放入mm和maps中
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
                //删除后再get就是空的了
				assertNull(mm.getRecord(key));

                //removed keys
				rkeys.add(key);
			}
		}

        //同样也要从maps中移除,保证mm和maps的数据一致
		for (String key : rkeys) {
			maps.remove(key);
		}

		// check data
		for (String key : maps.keySet()) {
			Record rec = mm.getRecord(key);
			assertNotNull(rec);
			assertEqualContent(maps.get(key), rec.getData());
		}

        // new data
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
