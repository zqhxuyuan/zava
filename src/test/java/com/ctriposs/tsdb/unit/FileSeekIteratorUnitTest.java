package com.ctriposs.tsdb.unit;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Random;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ctriposs.tsdb.InternalKey;
import com.ctriposs.tsdb.common.IStorage;
import com.ctriposs.tsdb.common.MapFileStorage;
import com.ctriposs.tsdb.common.PureFileStorage;
import com.ctriposs.tsdb.iterator.FileSeekIterator;
import com.ctriposs.tsdb.storage.CodeItem;
import com.ctriposs.tsdb.storage.DBWriter;
import com.ctriposs.tsdb.table.MemTable;
import com.ctriposs.tsdb.test.util.TestUtil;
import com.ctriposs.tsdb.util.FileUtil;

public class FileSeekIteratorUnitTest {

	private static final String TEST_DIR = TestUtil.TEST_BASE_DIR + "unit/fileseekiterator/";

	private static FileSeekIterator fIterator;
	private static String fileName;
	private static long timecount = 300000;
	private static long maxtimecount = 199;
	private static long startTime = 0;
	private static Random random = new Random();

	@Before
	public void setup() throws IOException {

		IStorage storage = new  MapFileStorage(TEST_DIR,System.currentTimeMillis(), "dat",MemTable.MAX_MEM_SIZE);
		fileName = storage.getName();
		DBWriter dbWriter = new DBWriter(storage, timecount, 1);
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
			dbWriter.add(key, (String.valueOf(value++)).getBytes());
		}
		
		dbWriter.close();
		fIterator = new FileSeekIterator(new PureFileStorage(new File(fileName)));
	}

	@Test
	public void testOneCodeIteratorNext() throws IOException {

		int maxCode = (int) (timecount/maxtimecount);
		int curCode = random.nextInt(maxCode);
		fIterator.seekToFirst(curCode,true);
		//fIterator.seek(curCode, startTime, true);
		int count = 0;
		while (fIterator.hasNext()) {
			Entry<InternalKey, byte[]> entry = fIterator.next();
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
	public void testAllCodeIteratorNext() throws IOException {
		int code = 0;
		int count = 0;
		while(fIterator.hasNextCode()){
			CodeItem codeItem = fIterator.nextCode();
			Assert.assertEquals(code++, codeItem.getCode());
			fIterator.seekToCurrent(true);
			int index = 0;
			while (fIterator.hasNext()) {
				Entry<InternalKey, byte[]> entry = fIterator.next();
				String value = new String(entry.getValue());
				Assert.assertEquals(String.valueOf(index++), value);
				count++;
			}
		}

		Assert.assertEquals(timecount, count);		

	}

	@Test
	public void testOneCodeIteratorPrev() throws IOException {

		int maxCode = (int) (timecount/maxtimecount);
		int curCode = random.nextInt(maxCode);
		fIterator.seekToFirst(curCode,false);
		int count = (int) maxtimecount;
		if(curCode==maxCode){
			count = (int) (timecount-curCode*maxtimecount);
		}
		count--;
		while (fIterator.hasPrev()) {
			Entry<InternalKey, byte[]> entry = fIterator.prev();
			String value = new String(entry.getValue());
			String expectValue = String.valueOf(count--);

			Assert.assertEquals(value,expectValue);
		}
		Assert.assertEquals(count, -1);
	}

	@Test
	public void testAllCodeIteratorPrev() throws IOException {
	
		int count = 0;
		int code = (int) ((timecount+maxtimecount-1)/maxtimecount-1);
		//fIterator.seekToFirst(code,false);
		fIterator.seek(code, Long.MAX_VALUE);
		while(fIterator.hasPrevCode()){
			CodeItem codeItem = fIterator.prevCode();
			Assert.assertEquals(code, codeItem.getCode());
			fIterator.seekToCurrent(false);
			int index = 0;
			if(count==0){
				index = (int) (timecount-code*maxtimecount);
			}else{
				index = (int) maxtimecount;
			}
			code--;
			while (fIterator.hasPrev()) {
				Entry<InternalKey, byte[]> entry = fIterator.prev();
				String value = new String(entry.getValue());
				String expectValue = String.valueOf(--index);
				Assert.assertEquals(expectValue, value);
				count++;
			}
		}

		Assert.assertEquals(timecount, count);		

	}
	

	@After
	public void close() throws IOException {
		fIterator.close();
		FileUtil.forceDelete(new File(fileName));
	}

}
