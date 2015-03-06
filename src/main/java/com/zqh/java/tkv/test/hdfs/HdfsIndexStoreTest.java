/**
 * 
 */
package com.zqh.java.tkv.test.hdfs;

import java.io.IOException;

import com.zqh.java.tkv.hdfs.HdfsHelper;
import com.zqh.java.tkv.hdfs.HdfsIndexStore;
import junit.framework.Assert;

import org.apache.hadoop.fs.FileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.zqh.java.tkv.Meta;
import com.zqh.java.tkv.test.StoreTestHelper;


/**
 * @author sean.wang
 * @since Mar 7, 2012
 */
public class HdfsIndexStoreTest extends StoreTestHelper {
	private HdfsIndexStore indexStore;

	@Before
	public void setUp() throws Exception {
		FileSystem localHdfsDir = HdfsHelper.createLocalFileSystem(super.localHdfsDir.getAbsolutePath());
		indexStore = new HdfsIndexStore(localHdfsDir, localIndexFile.getName(), localIndexFile, 64, 100);
	}

	@After
	public void tearDown() throws Exception {
		indexStore.close();
		indexStore.delete();
	}

	@Test
	public void testAppendAndGetIndex() throws IOException {
		final Meta meta1 = getMeta1();

		final Meta meta2 = getMeta2();

		// index meta1
		this.indexStore.append(meta1);

		// index meta2
		this.indexStore.append(meta2);

		// assert meta1
		Meta index = indexStore.getIndex(meta1.getKey());
		Assert.assertEquals(meta1.toString(), index.toString());

		// assert meta2
		index = indexStore.getIndex(meta2.getKey());
		Assert.assertEquals(meta2.toString(), index.toString());

		// assert get meta1 by tag
		index = indexStore.getIndex(meta1.getKey(), meta1.getTags().keySet().iterator().next());
		Assert.assertEquals(meta1.toString(), index.toString());

	}

	@Test
	public void testUploadToHdfs() throws IOException {
		final Meta meta1 = getMeta1();
		this.indexStore.append(meta1);
		this.indexStore.flush();
	}

	@Test
	public void testDownloadToHdfs() throws IOException {
		final Meta meta1 = getMeta1();
		this.indexStore.append(meta1);
		long length = this.localIndexFile.length();
		this.indexStore.flush();
		this.localIndexFile.delete();
		this.indexStore.download();
		Assert.assertEquals(length, this.localIndexFile.length());

	}

}
