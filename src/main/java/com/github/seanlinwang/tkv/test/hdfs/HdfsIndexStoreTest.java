/**
 * 
 */
package com.github.seanlinwang.tkv.test.hdfs;

import java.io.IOException;

import com.github.seanlinwang.tkv.Meta;
import com.github.seanlinwang.tkv.hdfs.HdfsHelper;
import com.github.seanlinwang.tkv.hdfs.HdfsIndexStore;
import com.github.seanlinwang.tkv.test.StoreTestHelper;
import junit.framework.Assert;

import org.apache.hadoop.fs.FileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


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
