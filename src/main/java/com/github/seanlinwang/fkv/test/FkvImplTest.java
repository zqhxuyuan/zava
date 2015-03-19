/**
 * 
 */
package com.github.seanlinwang.fkv.test;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

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
public class FkvImplTest {

	private static final int valueLength = 10;

	private static final int keyLength = 8;

	private static final int size = 100000;

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
		fkv = new FkvImpl(dbFile, size, keyLength, valueLength);
	}

	@After
	public void tearDown() throws Exception {
		fkv.close();
		dbFile.delete();
	}

	@Test
	public void testPut() {
		String key = "01234567";
		String value = "0123456789";
		fkv.put(key, value);
		Assert.assertEquals(fkv.getRecordLength() * 1, fkv.getEndIndex());
		Assert.assertEquals(value, fkv.get(key));
	}

	@Test
	public void testUpdate() {
		String key = "01234567";
		String value = "0123456789";
		String value2 = "9876543210";
		fkv.put(key, value);
		Assert.assertEquals(fkv.getRecordLength() * 1, fkv.getEndIndex());
		Assert.assertEquals(value, fkv.get(key));
		fkv.put(key, value2);
		Assert.assertEquals(fkv.getRecordLength() * 1, fkv.getEndIndex());
		Assert.assertEquals(value2, fkv.get(key));
	}

	@Test
	public void testDelete() {
		String key = "01234567";
		String value = "0123456789";
		fkv.put(key, value);
		Assert.assertEquals(fkv.getRecordLength() * 1, fkv.getEndIndex());
		fkv.delete(key);
		Assert.assertEquals(fkv.getRecordLength() * 1, fkv.getEndIndex());
		Assert.assertEquals(1, fkv.getDeletedSize());
		Assert.assertEquals(0, fkv.size());
		Assert.assertNull(fkv.get(key));
	}

	@Test
	public void testClear() {
		String key = "01234567";
		String value = "0123456789";
		fkv.put(key, value);
		fkv.clear();
		Assert.assertNull(fkv.get(key));
	}

	@Test
	public void testDeserial() throws IOException {
		String key = "01234567";
		String value = "0123456789";
		String key2 = "01234568";
		String value2 = "0123456780";
		fkv.put(key, value);
		fkv.put(key2, value2);
		fkv.delete(key2);
		fkv.close();
		Assert.assertEquals(size * (keyLength + valueLength + 2), dbFile.length());
		// deserial
		fkv = new FkvImpl(dbFile, 10000, 8, 10);
		Assert.assertEquals(1, fkv.size());
		Assert.assertEquals(1, fkv.getDeletedSize());
		Assert.assertEquals(fkv.getRecordLength() * 2, fkv.getEndIndex());
		Assert.assertEquals(null, fkv.get(key2)); // key2 is deleted
		Assert.assertEquals(value, fkv.get(key));
		Assert.assertEquals(0, fkv.getRecord(key).getIndex());
		Assert.assertEquals(value, fkv.getRecord(key).getValue());
		fkv.put(key, value2);
		Assert.assertEquals(value2, fkv.get(key));
		fkv.put(key2, value2);
		Assert.assertEquals(value2, fkv.get(key2));
		Assert.assertEquals(2, fkv.size());
		Assert.assertEquals(0, fkv.getDeletedSize());
	}

	@Test
	public void testPutDeleteInOneRecordSize() throws IOException {
		String key = "01234567";
		String value = "0123456789";
		fkv = new FkvImpl(dbFile, 1, 8, 10);
		for (int i = 0; i < 1000; i++) {
			fkv.put(key, value);
			fkv.delete(key);
		}
	}

}
