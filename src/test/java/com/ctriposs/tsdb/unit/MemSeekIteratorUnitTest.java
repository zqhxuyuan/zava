package com.ctriposs.tsdb.unit;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Map.Entry;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.ctriposs.tsdb.InternalKey;
import com.ctriposs.tsdb.iterator.MemSeekIterator;
import com.ctriposs.tsdb.table.InternalKeyComparator;
import com.ctriposs.tsdb.table.MemTable;
import com.ctriposs.tsdb.test.util.TestUtil;
import com.ctriposs.tsdb.util.FileUtil;

public class MemSeekIteratorUnitTest {
	private static final String TEST_DIR = TestUtil.TEST_BASE_DIR + "unit/memseekiterator/";
	
	private static MemSeekIterator mIterator;
	private static String fileName;
	private static long timecount = 300000;
	private static long maxtimecount = 555;
	private static long startTime = 0;
	private static Random random = new Random();
	
	@Before
	public void setup() throws IOException{
		MemTable memTable = new MemTable(TEST_DIR, 1, MemTable.MAX_MEM_SIZE, MemTable.MAX_MEM_SIZE, new InternalKeyComparator());
		fileName = memTable.getLogFile();
		startTime = System.currentTimeMillis();
		int code = 0;
		int value = 0;
		long time = startTime;
		for (int i = 0,count = 0; i < timecount; i++,count++) {
			
			if(count==maxtimecount){
				count = 0;
				code++;
				value = 0;
			}
			InternalKey key = new InternalKey(code, time++);
			memTable.add(key, (String.valueOf(value++)).getBytes());
		}
		memTable.close();
		mIterator = memTable.iterator(null); 
	}
	
	@Test
	public void testIteratorNext() throws IOException {
		int maxCode = (int) (timecount/maxtimecount);
		int curCode = random.nextInt(maxCode);
		mIterator.seek(curCode,0);
		int count = 0;
		while (mIterator.hasNext()) {
			Entry<InternalKey, byte[]> entry = mIterator.next();
			String value = new String(entry.getValue());
			Assert.assertEquals(String.valueOf(count++), value);
		}
		int expect = (int) maxtimecount;
		if(curCode==maxCode){
			expect = (int) (timecount-curCode*maxtimecount);
		}

		Assert.assertEquals(count, expect);
	}
	
	@Test
	public void testIteratorPrev() throws IOException {
		int maxCode = (int) (timecount/maxtimecount);
		int curCode = random.nextInt(maxCode);
		
		int count = (int) maxtimecount;
		if(curCode==maxCode){
			count = (int) (timecount-curCode*maxtimecount);
		}
		count--;

		mIterator.seek(curCode,Long.MAX_VALUE);
		while (mIterator.hasPrev()) {
			Entry<InternalKey, byte[]> entry = mIterator.prev();
			String value = new String(entry.getValue());
			Assert.assertEquals(String.valueOf(count--), value);
		}
		Assert.assertEquals(count, -1);
	}
		

	
	@After
	public void close() throws IOException{
		
		FileUtil.forceDelete(new File(fileName));
	}
}
