package com.ctriposs.tsdb.unit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import com.ctriposs.tsdb.iterator.MergeFileSeekIterator;
import com.ctriposs.tsdb.manage.FileManager;
import com.ctriposs.tsdb.storage.DBWriter;
import com.ctriposs.tsdb.table.InternalKeyComparator;
import com.ctriposs.tsdb.table.MemTable;
import com.ctriposs.tsdb.test.util.TestUtil;
import com.ctriposs.tsdb.util.FileUtil;

public class MergeFileSeekIteratorUnitTest {

	private static final String TEST_DIR = TestUtil.TEST_BASE_DIR + "unit/mergeseekiterator/";
	
	
	private static MergeFileSeekIterator mIterator;
	
	private static long timecount = 300000;
	private static long maxtimecount = 199;
	private static long maxminute = 5;
	private static long startTime = 0;
	private static Random random = new Random();
	private static List<String> fileList = new ArrayList<String>();
	
	
	private IStorage makeData(long start,int fileCount) throws IOException{
		IStorage storage = new MapFileStorage(TEST_DIR,start, "dat",MemTable.MAX_MEM_SIZE);
		fileList.add(storage.getName());		
		DBWriter dbWriter = new DBWriter(storage, timecount, fileCount+1);
		int maxCode = (int) (timecount/maxtimecount);
		
		int code = 0;
		int value = (int) (fileCount*maxtimecount);;
		long time = start;
		for (int i = 0,count = 0; i < timecount; i++,count++) {
			if(count==maxtimecount){
				count = 0;
				code++;
				time = start;
				if(maxCode==code){
					value = (int) ((int) fileCount*(timecount-code*maxtimecount));
				}else{
					value = (int) (fileCount*maxtimecount);
				}
			}
			InternalKey key = new InternalKey(code, time++);
			dbWriter.add(key, (String.valueOf(value++)).getBytes());
		}
		dbWriter.close();
		return new PureFileStorage(new File(storage.getName()));
	}
	
	@Before
	public void setup() throws IOException {
		FileManager fileManager = new FileManager(TEST_DIR, 300*MemTable.MINUTE, new InternalKeyComparator(), null);

		startTime = (System.currentTimeMillis()-maxminute*MemTable.MINUTE)/MemTable.MINUTE*MemTable.MINUTE;
		long time = startTime;
		mIterator = new MergeFileSeekIterator(fileManager);
		for(int i=0;i<maxminute;i++){
			IStorage  storage = makeData(time,i);
			mIterator.addIterator( new FileSeekIterator(storage, i));
			time += MemTable.MINUTE;
		}
		
	}
	
	@Test
	public void testIteratorNext() throws IOException {
		int maxCode = (int) (timecount/maxtimecount);
		int curCode = random.nextInt(maxCode);
		mIterator.seek(curCode, startTime);
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
		Assert.assertEquals(count, expect*maxminute);
	}

	@Test
	public void testIteratorPrev() throws IOException {
		
		int maxCode = (int) (timecount/maxtimecount);
		int curCode = random.nextInt(maxCode);
		mIterator.seek(curCode,Long.MAX_VALUE);
		int expect = (int) maxtimecount;
		if(curCode==maxCode){
			expect = (int) (timecount-curCode*maxtimecount);
		}
		int count = (int) (expect*maxminute);
		count--;
		
		while (mIterator.hasPrev()) {
			Entry<InternalKey, byte[]> entry = mIterator.prev();
			String value = new String(entry.getValue());
			System.out.println(value);
			Assert.assertEquals(String.valueOf(count--), value);
		}

		Assert.assertEquals(count, -1);
	}
	

	@Test
	public void testAllIteratorNext() throws IOException {
		int code = 0;
		int count = 0;
		int index = 0;
		mIterator.seekToFirst();
		while(mIterator.hasNext()){
			Entry<InternalKey, byte[]> entry = mIterator.next();
			String value = new String(entry.getValue());
			if(code == entry.getKey().getCode()){
				Assert.assertEquals(code, entry.getKey().getCode());
			}else{
				Assert.assertEquals(++code, entry.getKey().getCode());
				index=0;
			}
			Assert.assertEquals(String.valueOf(index++), value);
			count++;
		}

		Assert.assertEquals(timecount*maxminute, count);		
		
	}
	
	@Test
	public void testAllIteratorPrev() throws IOException {
		int count = 0;		
		int maxcode = (int) ((timecount+maxtimecount-1)/maxtimecount-1);
		int index = (int) ((timecount-maxcode*maxtimecount)*maxminute);
		
		mIterator.seekToLast(maxcode);
		int code = maxcode;
		while(mIterator.hasPrev()){
			Entry<InternalKey, byte[]> entry = mIterator.prev();
			String value = new String(entry.getValue());
			if(code == entry.getKey().getCode()){
				Assert.assertEquals(code, entry.getKey().getCode());
			}else{
				Assert.assertEquals(--code, entry.getKey().getCode());
				index = (int) (maxtimecount*maxminute);
			}
			
			Assert.assertEquals(String.valueOf(--index), value);
			count++;
		}

		Assert.assertEquals(timecount*maxminute, count);	
	}
	
	
	@After
	public void close() throws IOException{
		mIterator.close();
		for(String str:fileList){
			try{
				FileUtil.forceDelete(new File(str));
			}catch(Throwable t){
				t.printStackTrace();
			}
		}
	}
}
