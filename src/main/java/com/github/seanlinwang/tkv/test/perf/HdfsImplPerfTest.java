package com.github.seanlinwang.tkv.test.perf;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.apache.hadoop.fs.FileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.seanlinwang.tkv.HdfsImpl;
import com.github.seanlinwang.tkv.test.StoreTestHelper;
import com.github.seanlinwang.tkv.hdfs.HdfsHelper;

public class HdfsImplPerfTest extends StoreTestHelper {

	private HdfsImpl hdfs;

	private void serialRead(final AtomicInteger serial) throws IOException {
		hdfs.endWrite();
		hdfs.startRead();
		for (int p = 0; p < threadNum; p++) {
			for (int i = 0; i < timesPerThread; i++) {
				String id = "" + serial.incrementAndGet();
				String expect = "value" + id;
				byte[] target = hdfs.get(id);
				Assert.assertEquals(expect, new String(target));
			}
		}
	}

	private void serialWrite(AtomicInteger serial) throws IOException {
		hdfs.startWrite();
		for (int p = 0; p < threadNum; p++) {
			for (int i = 0; i < timesPerThread; i++) {
				String id = "" + serial.incrementAndGet();
				String value = "value" + id;
				Assert.assertTrue(hdfs.put(id, value.getBytes()));
			}
		}
		hdfs.buildIndex();
		hdfs.endWrite();
		hdfs.startRead();
	}

	@Before
	public void setUp() throws IOException {
		FileSystem localHdfsDir = HdfsHelper.createLocalFileSystem(super.localHdfsDir.getAbsolutePath());
		hdfs = new HdfsImpl(localHdfsDir, super.localDir, localIndexFile.getName(), localDataFile.getName(), 16, 100);
	}

	@After
	public void tearDown() throws Exception {
		hdfs.close();
		hdfs.delete();
	}

	@Test
	public void testConcurrentRead() throws Exception {
		final AtomicInteger serial = createSerial();
		final AtomicInteger fail = new AtomicInteger();
		final CountDownLatch latch = createLatch();
		this.serialWrite(serial);
		resetSerial(serial);
		long start = System.currentTimeMillis();
		submit(new Runnable() {
			public void run() {
				for (int i = 0; i < timesPerThread; i++) {
					String id = null;
					String expect = null;
					try {
						id = "" + serial.incrementAndGet();
						expect = "value" + id;
						byte[] target = hdfs.get(id);
						Assert.assertEquals(expect, new String(target));
					} catch (Throwable e) {
						System.out.println(Thread.currentThread().getName() + ":"+id + ":" + expect);
						e.printStackTrace();
						fail.incrementAndGet();
					}
				}
				latch.countDown();
			}
		});
		latch.await();
		printFails(fail.get(), start, hdfs.size());
	}

	@Test
	public void testConcurrentWrite() throws Exception {
		hdfs.startWrite();
		final AtomicInteger serial = createSerial();
		final AtomicInteger fail = new AtomicInteger();
		final CountDownLatch latch = createLatch();
		long start = System.currentTimeMillis();
		submit(new Runnable() {
			public void run() {
				for (int i = 0; i < timesPerThread; i++) {
					try {
						String id = "" + serial.incrementAndGet();
						String t1 = "value" + id;
						boolean success = hdfs.put(id, t1.getBytes());
						if (!success) {
							fail.incrementAndGet();
						}
					} catch (Throwable e) {
						fail.incrementAndGet();
					}
				}
				latch.countDown();
			}
		});
		latch.await();
		hdfs.buildIndex();
		printFails(fail.get(), start, hdfs.size());

		resetSerial(serial);
		hdfs.endWrite();
		this.serialRead(serial);
	}

	@Test
	public void testSerialRead() throws Exception {
		final AtomicInteger serial = createSerial();
		this.serialWrite(serial);
		resetSerial(serial);
		long start = System.currentTimeMillis();
		serialRead(serial);
		print(start, this.hdfs.size());
	}

	@Test
	public void testSerialWrite() throws Exception {
		final AtomicInteger serial = createSerial();
		long start = System.currentTimeMillis();
		this.serialWrite(serial);
		print(start, this.hdfs.size());
		resetSerial(serial);
		this.serialRead(serial);
	}

}
