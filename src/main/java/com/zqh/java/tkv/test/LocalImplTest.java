/**
 * 
 */
package com.zqh.java.tkv.test;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zqh.java.tkv.LocalImpl;
import com.zqh.java.tkv.Record;


/**
 * @author sean.wang
 * @since Nov 17, 2011
 */
public class LocalImplTest {

	LocalImpl tkv;

	File dbFile;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		dbFile = new File("/tmp/tkvtest.db");
		dbFile.delete();
		tkv = new LocalImpl(dbFile);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		tkv.close();
		tkv.delete();
	}

	/**
	 * Test method for {@link com.zqh.java.tkv.LocalImpl#get(java.lang.String)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testPut() throws IOException {
		String key = "01234567";
		String value = "ayellowdog";
		String[] tags = new String[] { "dog", "pet" };
		tkv.put(key, value.getBytes(), tags);
		Assert.assertEquals(4 + 4 + 4 + key.length() + value.length(), +7 + 1, dbFile.length());
		// assert tkv
		Assert.assertEquals(value, new String(tkv.get(key)));
	}

	@Test
	public void testGetTagRecord() throws IOException {
		String key = "01234567";
		String value = "ayellowdog";
		String[] tags = new String[] { "dog", "pet" };
		tkv.put(key, value.getBytes(), tags);
		String key2 = "01";
		String value2 = "baby";
		String[] tags2 = new String[] { "bird", "pet" };
		tkv.put(key2, value2.getBytes(), tags2);

		String key3 = "0145jy";
		String value3 = "brown";
		String[] tags3 = new String[] { "pet" };
		tkv.put(key3, value3.getBytes(), tags3);

		Record r = tkv.getRecord(key2, "pet");

		Record rNext = tkv.getRecord(r.nextKey(), "pet");
		Assert.assertEquals(key3, rNext.getKey());
		Assert.assertEquals(value3, new String(rNext.getValue()));

		Record rPrevious = tkv.getRecord(r.priviousKey(), "pet");
		Assert.assertEquals(key, rPrevious.getKey());
		Assert.assertEquals(value, new String(rPrevious.getValue()));
	}

	/**
	 * Test method for {@link com.zqh.java.tkv.LocalImpl#get(java.lang.String)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testDeserial() throws IOException {
		// String key = "01234567";
		// String value = "0123456789";
		// String key2 = "01234568";
		// String value2 = "0123456780";
		// fkv.put(key, value);
		// fkv.put(key2, value2);
		// fkv.delete(key2);
		// fkv.close();
		// Assert.assertEquals(size * (keyLength + valueLength + 2), dbFile.length());
		// // deserial
		// fkv = new TkvImpl(dbFile, 10000, 8, 10);
		// Assert.assertEquals(1, fkv.size());
		// Assert.assertEquals(1, fkv.getDeletedSize());
		// Assert.assertEquals(fkv.getRecordLength() * 2, fkv.getEndIndex());
		// Assert.assertEquals(null, fkv.get(key2)); // key2 is deleted
		// Assert.assertEquals(value, fkv.get(key));
		// Assert.assertEquals(0, fkv.getRecord(key).getIndex());
		// Assert.assertEquals(value, fkv.getRecord(key).getStringValue());
		// fkv.put(key, value2);
		// Assert.assertEquals(value2, fkv.get(key));
		// fkv.put(key2, value2);
		// Assert.assertEquals(value2, fkv.get(key2));
		// Assert.assertEquals(2, fkv.size());
		// Assert.assertEquals(0, fkv.getDeletedSize());
	}

}
