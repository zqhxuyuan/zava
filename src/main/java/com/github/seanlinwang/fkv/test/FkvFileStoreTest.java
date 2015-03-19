package com.github.seanlinwang.fkv.test;

import java.io.File;
import java.io.IOException;

import com.github.seanlinwang.fkv.FkvFileStore;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FkvFileStoreTest {
	File dbFile;
	private FkvFileStore store;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		dbFile = new File("/tmp/fkvtest.db");
		store = new FkvFileStore(dbFile);
	}

	@After
	public void tearDown() throws Exception {
		store.close();
		dbFile.delete();
	}

	@Test
	public void testPutGet() throws IOException {
		int startIndex = 10;
		store.put(startIndex, new byte[] { (byte) 2 });
		byte[] bytes = store.get(startIndex, 1);
		Assert.assertArrayEquals(new byte[] { (byte) 2 }, bytes);
		store.close();
	}

}
