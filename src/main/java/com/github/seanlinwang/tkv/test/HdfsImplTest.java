/**
 * 
 */
package com.github.seanlinwang.tkv.test;

import java.io.IOException;

import com.github.seanlinwang.tkv.HdfsImpl;
import com.github.seanlinwang.tkv.Meta;
import org.apache.hadoop.fs.FileSystem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.seanlinwang.tkv.hdfs.HdfsHelper;

/**
 * @author sean.wang
 * @since Mar 7, 2012
 */
public class HdfsImplTest extends StoreTestHelper {
	private HdfsImpl hdfs;

	@Before
	public void tearUp() throws Exception {
		this.localIndexFile.delete();
		this.localDataFile.delete();
		FileSystem localHdfsDir = HdfsHelper.createLocalFileSystem(super.localHdfsDir.getAbsolutePath());
		hdfs = new HdfsImpl(localHdfsDir, super.localDir, localIndexFile.getName(), localDataFile.getName(), 64, 100);
	}

	@After
	public void tearDown() throws Exception {
		hdfs.close();
		hdfs.delete();
	}

	@Test
	public void testPutAndGetWithoutTag() throws IOException {
		Meta m1 = super.getMeta1();
		String value1 = "1234";
		hdfs.startWrite();
		Assert.assertTrue(hdfs.put(m1.getKey(), value1.getBytes()));
		hdfs.buildIndex();
		hdfs.endWrite();
		hdfs.startRead();
		Assert.assertEquals(value1, new String(hdfs.get(m1.getKey())));
		hdfs.endRead();
	}

	@Test
	public void testPutAndGetWithTag() throws IOException {
		hdfs.startWrite();
		// keys must asc order for offset calculation bellow!
		final String key1 = "12345678901234567890123456789010";
		final String key2 = "12345678901234567890123456789017";
		final String key3 = "12345678901234567890123456789029";
		final String value1 = "It's A.";
		final String value2 = "It's B.";
		final String value3 = "It's C.";
		final String tagName1 = "t1";

		hdfs.put(key1, value1.getBytes(), tagName1);
		hdfs.put(key2, value2.getBytes(), tagName1);
		hdfs.put(key3, value3.getBytes());

		hdfs.buildIndex();
		hdfs.endWrite();
		hdfs.startRead();

		Assert.assertEquals(value1, new String(hdfs.get(key1)));
		Assert.assertEquals(value1, new String(hdfs.get(key1, tagName1)));
		Assert.assertEquals(value2, new String(hdfs.get(key2)));
		Assert.assertEquals(value2, new String(hdfs.get(key2, tagName1)));
		Assert.assertEquals(value3, new String(hdfs.get(key3)));
		Assert.assertNull("key3 not tagged", hdfs.get(key3, tagName1));
		Assert.assertEquals("key2 is next key1 with the same tag", value2, new String(hdfs.getNext(key1, tagName1)));
		Assert.assertEquals("key1 is previous key2 with the same tag", value1, new String(hdfs.getPrevious(key2, tagName1)));
	}

	@Test
	public void testGetNotExistsKey() throws IOException {
		hdfs.startWrite();
		final String key1 = "12345678901234567890123456789010";
		final String value1 = "It's A.";
		final String tagName1 = "t1";

		hdfs.put(key1, value1.getBytes(), tagName1);
		hdfs.buildIndex();
		hdfs.endWrite();
		hdfs.startRead();

		Assert.assertNull(hdfs.get("notexistkey"));
	}
}
