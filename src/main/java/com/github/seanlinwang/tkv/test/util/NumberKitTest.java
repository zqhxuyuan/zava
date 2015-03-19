package com.github.seanlinwang.tkv.test.util;

import com.github.seanlinwang.tkv.util.NumberKit;
import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class NumberKitTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBytes2IntByteArrayInt() {
		Assert.assertEquals(1, NumberKit.bytes2Int(new byte[]{0, 0, 0, 1}, 0));
		Assert.assertEquals(1, NumberKit.bytes2Int(new byte[] { 1, 0, 0, 0, 1 }, 1));
	}

}
