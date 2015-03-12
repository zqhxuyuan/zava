package com.ctriposs.sdb.table;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Test;

import com.ctriposs.sdb.table.AbstractMapTable;
import com.ctriposs.sdb.table.GetResult;
import com.ctriposs.sdb.table.HashMapTable;
import com.ctriposs.sdb.utils.FileUtil;
import com.ctriposs.sdb.utils.TestUtil;

public class HashMapTablePerfTest {
	
	private static String testDir = TestUtil.TEST_BASE_DIR + "sdb/unit/hashmap_table_perf_test";
	
	private HashMapTable map;
	
	static final int N_THREADS = 128;

	@Test
	public void testPut() throws IOException, ClassNotFoundException {
		int count = 40000;
		
		map = new HashMapTable(testDir, 0, System.nanoTime());
		
		long start = System.nanoTime();
		
		final SampleValue value = new SampleValue();
		StringBuilder user = new StringBuilder();
		System.out.println(value.toBytes().length);
		for(int i = 0; i < count; i++) {
			value.ee = i;
			value.gg = i;
			value.ii = i;
			map.put(users(user, i).getBytes(), value.toBytes(), AbstractMapTable.NO_TIMEOUT, System.currentTimeMillis());
		}
		assertTrue(map.getAppendedSize() == count);
		for(int i = 0; i < count; i++) {
			GetResult result = map.get(users(user, i).getBytes());
			assertTrue(result.isFound() && !result.isDeleted() && !result.isExpired());
			assertNotNull(result.getValue());
			SampleValue value2 = SampleValue.fromBytes(result.getValue());
			assertEquals(i, value2.ee);
			assertEquals(i, value2.gg, 0.0);
			assertEquals(i, value2.ii);
		}
		for(int i = 0; i < count; i++) {
			GetResult result = map.get(users(user, i).getBytes());
			assertNotNull(result.getValue());
		}
        for (int i = 0; i < count; i++) {
            map.delete(users(user, i).getBytes());
        }
        long time = System.nanoTime() - start;
        System.out.printf("Put/get %,d K operations per second%n",
                (int) (count * 4 * 1e6 / time));
	}
	
	@Test
	public void testPutPerf() throws ExecutionException, InterruptedException, IOException {
		ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        // use a simple pseudo-random distribution over 64-bits
		
		System.out.println("Starting test");
		
		map = new HashMapTable(testDir, 0, System.nanoTime());
		final int COUNT = 50000;
		final String[] users = new String[COUNT];
		for(int i = 0; i < COUNT; i++) users[i] = "user:" + i;
		
		
		long start = System.nanoTime();
		List<Future<?>> futures = new ArrayList<Future<?>>();
		for(int t = 0; t < N_THREADS; t++) {
			final int finalT = t;
			futures.add(es.submit(new Runnable() {

				@Override
				public void run() {
					try {
						final SampleValue value = new SampleValue();
	                    StringBuilder user = new StringBuilder();
	                    for (int i = finalT; i < COUNT; i += N_THREADS) {
	                        value.ee = i;
	                        value.gg = i;
	                        value.ii = i;
	                        map.put(users(user, i).getBytes(), value.toBytes(), AbstractMapTable.NO_TIMEOUT, System.currentTimeMillis());
	                    }
	                    for (int i = finalT; i < COUNT; i += N_THREADS) {
	            			GetResult result = map.get(users(user, i).getBytes());
	            			assertTrue(result.isFound() && !result.isDeleted() && !result.isExpired());
	            			assertNotNull(result.getValue());
	            			SampleValue value2 = SampleValue.fromBytes(result.getValue());
	                        assertEquals(i, value2.ee);
	                        assertEquals(i, value2.gg, 0.0);
	                        assertEquals(i, value2.ii);
	                    }
	                    for (int i = finalT; i < COUNT; i += N_THREADS)
                            assertTrue(map.get(users(user, i).getBytes()).isFound());
	                    for (int i = finalT; i < COUNT; i += N_THREADS)
	                        map.delete(users(user, i).getBytes());
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				
			}));
		}
		
		for (Future<?> future : futures)
            future.get();
        long time = System.nanoTime() - start;
        System.out.printf("Put/get %,d K operations per second%n",
                (int) (COUNT * 4 * 1e6 / time));
        es.shutdown();
		
	}
	
	public static class SampleValue implements Serializable {

		private static final long serialVersionUID = 1L;
		public String aa = "aaaaaaaaaa";
	    public String bb = "bbbbbbbbbb";
	    public BuySell cc = BuySell.Buy;
	    public BuySell dd = BuySell.Sell;
	    public int ee = 123456;
	    public int ff = 654321;
	    public double gg = 1.23456789;
	    public double hh = 9.87654321;
	    public long ii = 987654321;
	    public long jj = 123456789;
	    
	    public byte[] toBytes() throws IOException {
	    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    	ObjectOutput out = null;
	    	try {
	    	  out = new ObjectOutputStream(bos);   
	    	  out.writeObject(this);
	    	  byte[] yourBytes = bos.toByteArray();
	    	  return yourBytes;
	    	} finally {
	    	  try {
	    	    if (out != null) {
	    	      out.close();
	    	    }
	    	  } catch (IOException ex) {
	    	    // ignore close exception
	    	  }
	    	  try {
	    	    bos.close();
	    	  } catch (IOException ex) {
	    	    // ignore close exception
	    	  }
	    	}
	    }
	    
	    public static SampleValue fromBytes(byte[] bytes) throws ClassNotFoundException, IOException {
	    	ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
	    	ObjectInput in = null;
	    	try {
	    	  in = new ObjectInputStream(bis);
	    	  Object o = in.readObject(); 
	    	  return (SampleValue)o;
	    	} finally {
	    	  try {
	    	    bis.close();
	    	  } catch (IOException ex) {
	    	    // ignore close exception
	    	  }
	    	  try {
	    	    if (in != null) {
	    	      in.close();
	    	    }
	    	  } catch (IOException ex) {
	    	    // ignore close exception
	    	  }
	    	}
	    }
	}

	enum BuySell {
	    Buy, Sell
	}
	
	public static String users(StringBuilder user, int i) {
        user.setLength(0);
        user.append("user:");
        user.append(i);
        return user.toString();
    }
	
	@After
	public void clear() throws IOException {
		if (map != null) {
			map.close();
		}
		FileUtil.deleteDirectory(new File(testDir));
	}

}
