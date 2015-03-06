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
import com.ctriposs.tsdb.common.Level;
import com.ctriposs.tsdb.common.MapFileStorage;
import com.ctriposs.tsdb.iterator.LevelSeekIterator;
import com.ctriposs.tsdb.level.StoreLevel;
import com.ctriposs.tsdb.manage.FileManager;
import com.ctriposs.tsdb.storage.DBWriter;
import com.ctriposs.tsdb.storage.FileMeta;
import com.ctriposs.tsdb.table.InternalKeyComparator;
import com.ctriposs.tsdb.table.MemTable;
import com.ctriposs.tsdb.test.util.TestUtil;
import com.ctriposs.tsdb.util.FileUtil;

public class LevelSeekIteratorUnitTest {
	private static final String TEST_DIR = TestUtil.TEST_BASE_DIR + "unit/levelseekiterator/";
	
	private static LevelSeekIterator lIterator;
	private static long timecount = 300000;
	private static long maxtimecount = 199;
	private static long maxminute = 5;
	private static long startTime = 0;
	private static Random random = new Random();
	private static List<String> fileList = new ArrayList<String>();
	
	
	private FileMeta makeFileMeta(long start,int fileCount) throws IOException{
		IStorage storage = new MapFileStorage(TEST_DIR,start, "dat",MemTable.MAX_MEM_SIZE);
		fileList.add(storage.getName());		
		DBWriter dbWriter = new DBWriter(storage, timecount, fileCount+1);
		int maxCode = (int) (timecount/maxtimecount);
		
		int code = 0;
		int value = 0;
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
		return dbWriter.close();
	}
	
	@Before
	public void setup() throws IOException{
		FileManager fileManager = new FileManager(TEST_DIR, 300*MemTable.MINUTE, new InternalKeyComparator(), null);
		Level level = new StoreLevel(fileManager, 1, 1, MemTable.MINUTE);
		
		startTime = (System.currentTimeMillis()-maxminute*MemTable.MINUTE)/MemTable.MINUTE*MemTable.MINUTE;
		long time = startTime;
		for(int i=0;i<maxminute;i++){
			level.add(time, makeFileMeta(time,i));
			time += MemTable.MINUTE;
		}
		
		lIterator = new LevelSeekIterator(fileManager, level);
	}
	
	@Test
	public void testIteratorNext() throws IOException {
		int maxCode = (int) (timecount/maxtimecount);
		int curCode = random.nextInt(maxCode);
		lIterator.seek(curCode, startTime);
		int count = 0;
		while (lIterator.hasNext()) {
			Entry<InternalKey, byte[]> entry = lIterator.next();
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
		lIterator.seek(curCode, System.currentTimeMillis());
		int expect = (int) maxtimecount;
		if(curCode==maxCode){
			expect = (int) (timecount-curCode*maxtimecount);
		}
		
		int count = (int) (expect*maxminute);
		count--;
		while (lIterator.hasPrev()) {
			Entry<InternalKey, byte[]> entry = lIterator.prev();
			String value = new String(entry.getValue());
			Assert.assertEquals(String.valueOf(count--), value);
		}

		Assert.assertEquals(count, -1);
	}
	
	@After
	public void close() throws IOException{
		lIterator.close();
		for(String str:fileList){
			try{
				FileUtil.forceDelete(new File(str));
			}catch(Throwable t){
				
			}
		}
	}
	
}
