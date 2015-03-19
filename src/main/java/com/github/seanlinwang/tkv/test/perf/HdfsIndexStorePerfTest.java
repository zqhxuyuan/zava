/**
 * 
 */
package com.github.seanlinwang.tkv.test.perf;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.apache.hadoop.fs.FileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.seanlinwang.tkv.Meta;
import com.github.seanlinwang.tkv.test.StoreTestHelper;
import com.github.seanlinwang.tkv.hdfs.HdfsHelper;
import com.github.seanlinwang.tkv.hdfs.HdfsIndexStore;

/**
 * @author sean.wang
 * @since Mar 14, 2012
 */
public class HdfsIndexStorePerfTest extends StoreTestHelper {

	private HdfsIndexStore indexStore;

	/**
	 * @param serial
	 * @return
	 * @throws IOException
	 */
	private void serialWrite(final AtomicInteger serial) throws IOException {
		for (int p = 1; p < 1 + threadNum; p++) {
			for (int i = 0; i < timesPerThread; i++) {
				String id = "" + serial.incrementAndGet();
				Meta m = new Meta();
				m.setKey(id);
				indexStore.append(m);
			}
		}
	}

	private String serialWriteWithTag(final AtomicInteger serial) throws IOException {
		String tagName = "t1";
		for (int p = 1; p < 1 + threadNum; p++) {
			for (int i = 0; i < timesPerThread; i++) {
				String id = "" + serial.incrementAndGet();
				Meta m = new Meta();
				m.setKey(id);
				m.addTag(tagName);
				indexStore.append(m);
			}
		}
		return tagName;
	}

	@Before
	public void setUp() throws Exception {
		FileSystem localHdfsDir = HdfsHelper.createLocalFileSystem(super.localHdfsDir.getAbsolutePath());
		indexStore = new HdfsIndexStore(localHdfsDir, localIndexFile.getName(), localIndexFile, KeyLength, TagLength);
	}

	@After
	public void tearDown() throws Exception {
		indexStore.close();
		indexStore.delete();
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
					try {
						id = "" + serial.incrementAndGet();
						Meta meta = indexStore.getIndex(id);
						Assert.assertEquals(id, meta.getKey());
					} catch (Throwable e) {
						System.out.println(id);
						e.printStackTrace();
						fail.incrementAndGet();
					}
				}
				latch.countDown();
			}
		});
		latch.await();
		printFails(fail.get(), start, indexStore.size());
	}

	@Test
	public void testConcurrentWrite() throws Exception {
		final AtomicInteger serial = createSerial();
		final AtomicInteger fail = new AtomicInteger();
		final CountDownLatch latch = createLatch();
		long start = System.currentTimeMillis();
		submit(new Runnable() {
			public void run() {
				for (int i = 0; i < timesPerThread; i++) {
					try {
						String id = null;
						synchronized (serial) { // XXX for asc append
							id = "" + serial.incrementAndGet();
							Meta m = new Meta();
							m.setKey(id);
							indexStore.append(m);
						}
					} catch (Throwable e) {
						fail.incrementAndGet();
					}
				}
				latch.countDown();
			}
		});
		latch.await();
		printFails(fail.get(), start, indexStore.size());
		this.resetSerial(serial);
		this.serialRead(serial);
	}

	@Test
	public void testConcurrentWriteRead() throws Exception {
		final AtomicInteger serial = createSerial();
		final AtomicInteger fail = new AtomicInteger();
		final CountDownLatch latch = createLatch();
		long start = System.currentTimeMillis();
		submit(new Runnable() {
			public void run() {
				for (int i = 0; i < timesPerThread; i++) {
					try {
						String id = null;
						synchronized (serial) { // XXX for asc append
							id = "" + serial.incrementAndGet();
							Meta m = new Meta();
							m.setKey(id);
							indexStore.append(m);
						}
						Assert.assertEquals(id, indexStore.getIndex(id).getKey());
					} catch (Throwable e) {
						fail.incrementAndGet();
					}
				}
				latch.countDown();
			}
		});
		latch.await();
		printFails(fail.get(), start, indexStore.size());
	}

	@Test
	public void testSerialRead() throws Exception {
		final AtomicInteger serial = createSerial();
		serialWrite(serial);
		resetSerial(serial);
		long start = System.currentTimeMillis();
		serialRead(serial);
		print(start, indexStore.size());
	}

	protected void serialRead(final AtomicInteger serial) throws IOException {
		for (int p = 1; p < 1 + threadNum; p++) {
			for (int i = 0; i < timesPerThread; i++) {
				String id = "" + serial.incrementAndGet();
				Meta meta = indexStore.getIndex(id);
				Assert.assertEquals(id, meta.getKey());
			}
		}
	}

	@Test
	public void testSerialReadWithTag() throws Exception {
		final AtomicInteger serial = createSerial();
		String tagName = serialWriteWithTag(serial);
		resetSerial(serial);
		long start = System.currentTimeMillis();
		for (int p = 1; p < 1 + threadNum; p++) {
			for (int i = 0; i < timesPerThread; i++) {
				String id = "" + serial.incrementAndGet();
				Meta meta = indexStore.getIndex(id, tagName);
				Assert.assertEquals(id, meta.getKey());
			}
		}
		print(start, indexStore.size());
	}

	@Test
	public void testSerialWrite() throws Exception {
		final AtomicInteger serial = createSerial();
		long start = System.currentTimeMillis();
		serialWrite(serial);
		print(start, this.indexStore.size());
	}

	@Test
	public void testSerialWriteWithTag() throws Exception {
		final AtomicInteger serial = createSerial();
		long start = System.currentTimeMillis();
		serialWriteWithTag(serial);
		print(start, this.indexStore.size());
	}

}
