/**
 * 
 */
package com.github.seanlinwang.tkv.test;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.seanlinwang.tkv.Meta;
import com.github.seanlinwang.tkv.Tag;
import junit.framework.Assert;

/**
 * @author sean.wang
 * @since Mar 9, 2012
 */
public abstract class StoreTestHelper {

	protected File localDir = new File(System.getProperty("user.dir") + "/target/hdfstmp/");

	protected File localHdfsDir = new File(System.getProperty("user.dir") + "/target/hdfs/");

	protected File localIndexFile = new File(localDir, "a.index");

	protected File localDataFile = new File(localDir, "a.data");

	protected final static int threadNum = 10;// notice: max 9, for creating asc order id bellow

	protected final static int timesPerThread = 1000; // notice: must be powers 10, fro creating asc order id bellow

	protected final static int TagLength = 128;

	protected final static int KeyLength = 32;

	protected void printFails(final int fails, final long start, final long size) {
		System.out.println(new Throwable().getStackTrace()[1].toString() + " threads:" + threadNum + " total:" + threadNum * timesPerThread + " fails:" + fails + " waste:" + (System.currentTimeMillis() - start) + "ms");
		if (fails > 0) {
			Assert.fail("fails:" + fails);
		}
		Assert.assertEquals(threadNum * timesPerThread, size);
	}

	protected void print(final long start, final long size) {
		System.out.println(new Throwable().getStackTrace()[1].toString() + " threads:" + threadNum + " total:" + threadNum * timesPerThread + " waste:" + (System.currentTimeMillis() - start) + "ms");
		Assert.assertEquals(threadNum * timesPerThread, size);
	}

	/**
	 * <pre>
	 * Meta [key=12345678, offset=0, length=10, 
	 * tags={bird=Tag [previous=-1, next=-1, name=bird], pet=Tag [previous=-1, next=1, name=pet]}]
	 * </pre>
	 * 
	 * @return
	 */
	protected Meta getMeta1() {
		final Meta meta1 = new Meta();
		meta1.setKey("12345678");
		meta1.setOffset(0);
		meta1.setLength(10);
		Tag t1 = new Tag();
		t1.setName("pet");
		t1.setPos(0);
		t1.setNext(1);
		meta1.addTag(t1);
		Tag t2 = new Tag();
		t2.setName("bird");
		t2.setPos(0);
		meta1.addTag(t2);
		return meta1;
	}

	protected Meta getMeta2() {
		final Meta meta2 = new Meta();
		meta2.setKey("87654321");
		meta2.setOffset(10);
		meta2.setLength(20);
		Tag t3 = new Tag();
		t3.setName("pet");
		t3.setPos(1);
		t3.setPrevious(0);
		meta2.addTag(t3);
		return meta2;
	}

	protected void resetSerial(final AtomicInteger serial) {
		serial.set(10 * timesPerThread);
	}

	protected AtomicInteger createSerial() {
		return new AtomicInteger(10 * timesPerThread);
	}

	final ExecutorService pool = Executors.newFixedThreadPool(threadNum);

	protected void submit(Runnable run) {
		for (int p = 0; p < threadNum; p++) {
			pool.submit(run);
		}
	}

	protected CountDownLatch createLatch() {
		return new CountDownLatch(threadNum);
	}
}
