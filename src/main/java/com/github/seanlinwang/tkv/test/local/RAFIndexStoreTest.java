/**
 * 
 */
package com.github.seanlinwang.tkv.test.local;

import java.io.IOException;

import com.github.seanlinwang.tkv.Meta;
import com.github.seanlinwang.tkv.local.RAFIndexStore;
import com.github.seanlinwang.tkv.test.StoreTestHelper;
import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author sean.wang
 * @since Mar 7, 2012
 */
public class RAFIndexStoreTest extends StoreTestHelper {
	private RAFIndexStore indexStore;

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
		localIndexFile.delete();
		indexStore = new RAFIndexStore(localIndexFile, 64, 100);
	}

	/**
	 * @throws java.lang.Exception
	 */
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

}
