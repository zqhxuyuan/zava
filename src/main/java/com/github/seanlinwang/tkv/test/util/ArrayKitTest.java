package com.github.seanlinwang.tkv.test.util;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.seanlinwang.tkv.util.ArrayKit;


public class ArrayKitTest {

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
	public void testSplit() {
		byte[][] segs = ArrayKit.split(new byte[] { '1', '\t', '2', '3', '\t' }, (byte) '\t');
		Assert.assertTrue(Arrays.deepEquals(new byte[][] { { '1' }, { '2', '3' } }, segs));
	}

}
