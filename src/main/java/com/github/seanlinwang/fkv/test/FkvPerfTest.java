/**
 * 
 */
package com.github.seanlinwang.fkv.test;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.seanlinwang.fkv.FkvImpl;

/**
 * @author sean.wang
 * @since Nov 17, 2011
 */
public class FkvPerfTest {

	FkvImpl fkv;

	File dbFile;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		dbFile = new File("/tmp/fkvtest.db");
		dbFile.delete();
		fkv = new FkvImpl(dbFile, perfTimes, 8, 10);
	}

	@After
	public void tearDown() throws Exception {
		fkv.close();
		dbFile.delete();
	}

	private int perfTimes = 20 * 10000;

	@Test
	public void testPutSameKeyPerf() {
		String key = "01234567";
		String value = "0123456789";
		long start = System.currentTimeMillis();
		for (int i = 0; i < perfTimes; i++) {
			fkv.put(key, value);
		}
		System.out.println("testPutSameKeyPerf:" + (System.currentTimeMillis() - start));
	}

	@Test
	public void testPutDiffKeyPerf() {
		String value = "0123456789";
		long start = System.currentTimeMillis();
		for (int i = 0; i < perfTimes; i++) {
			fkv.put("" + (10000000 + i), value);
		}
		System.out.println("testPutDiffKeyPerf:" + (System.currentTimeMillis() - start));
	}

	@Test
	public void testGetSameKeyPerf() {
		String key = "01234567";
		long start = System.currentTimeMillis();
		for (int i = 0; i < perfTimes; i++) {
			fkv.get(key);
		}
		System.out.println("testGetSameKeyPerf:" + (System.currentTimeMillis() - start));
	}

	@Test
	public void testPutDeletePerf() {
		String key = "01234567";
		String value = "0123456789";
		long start = System.currentTimeMillis();
		for (int i = 0; i < perfTimes / 2; i++) {
			fkv.put(key, value);
			fkv.delete(key);
		}
		System.out.println("testPutDeletePerf:" + (System.currentTimeMillis() - start));
	}

}
