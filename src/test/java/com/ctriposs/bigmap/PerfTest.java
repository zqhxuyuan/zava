package com.ctriposs.bigmap;

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

import com.ctriposs.bigmap.utils.FileUtil;

public class PerfTest {
	
	private static String testDir = TestUtil.TEST_BASE_DIR + "bigmap/unit/perf_test";
	
	private BigConcurrentHashMapImpl map;
	
	static final int N_THREADS = 128;
	static final int COUNT = 8 * 10000000;
	static final long stride;
	
	static {
		long _stride = Long.MAX_VALUE / COUNT;
		while(_stride % 2 == 0)
			_stride--;
		stride = _stride;
	}

	@Test
	public void testPut() throws IOException, ClassNotFoundException {
		int count = 4000000;
		
		BigConfig config = new BigConfig().setInitialCapacity(count).setConcurrencyLevel(128);
		map = new BigConcurrentHashMapImpl(testDir, "testPut", config);
		
		long start = System.nanoTime();
		
		final SampleValue value = new SampleValue();
		StringBuilder user = new StringBuilder();
		System.out.println(value.toBytes().length);
		for(int i = 0; i < count; i++) {
			value.ee = i;
			value.gg = i;
			value.ii = i;
			map.put(users(user, i).getBytes(), value.toBytes());
		}
		assertTrue(map.size() == count);
		for(int i = 0; i < count; i++) {
			byte[] bytes = map.get(users(user, i).getBytes());
			assertNotNull(bytes);
			SampleValue value2 = SampleValue.fromBytes(bytes);
			assertEquals(i, value2.ee);
			assertEquals(i, value2.gg, 0.0);
			assertEquals(i, value2.ii);
		}
		for(int i = 0; i < count; i++) {
			byte[] bytes = map.get(users(user, i).getBytes());
			assertNotNull(bytes);
		}
        for (int i = 0; i < count; i++) {
            map.remove(users(user, i).getBytes());
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
		
		BigConfig config = new BigConfig().setInitialCapacity(COUNT).setConcurrencyLevel(64);
		map = new BigConcurrentHashMapImpl(testDir, "testPutPerf", config);
		
		final int COUNT = 5000000;
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
	                        map.put(users(user, i).getBytes(), value.toBytes());
	                    }
	                    for (int i = finalT; i < COUNT; i += N_THREADS) {
	            			byte[] bytes = map.get(users(user, i).getBytes());
	            			assertNotNull(bytes);
	            			SampleValue value2 = SampleValue.fromBytes(bytes);
	                        assertEquals(i, value2.ee);
	                        assertEquals(i, value2.gg, 0.0);
	                        assertEquals(i, value2.ii);
	                    }
	                    for (int i = finalT; i < COUNT; i += N_THREADS)
	                    	if (map.get(users(user,i).getBytes()) == null) {
	                    		System.out.println(users(user, i));
	                    	}
                            //assertNotNull(map.get(users(user, i).getBytes()));
	                    for (int i = finalT; i < COUNT; i += N_THREADS)
	                        map.remove(users(user, i).getBytes());
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
		String aa = "aaaaaaaaaa";
	    String bb = "bbbbbbbbbb";
	    BuySell cc = BuySell.Buy;
	    BuySell dd = BuySell.Sell;
	    int ee = 123456;
	    int ff = 654321;
	    double gg = 1.23456789;
	    double hh = 9.87654321;
	    long ii = 987654321;
	    long jj = 123456789;
	    
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
	
	String users(StringBuilder user, int i) {
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
