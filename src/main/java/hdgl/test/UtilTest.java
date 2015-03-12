package hdgl.test;

import static org.junit.Assert.*;
import hdgl.util.NetHelper;
import hdgl.util.WritableHelper;

import org.junit.Test;

public class UtilTest {

	@Test
	public void test() {
		System.out.println(NetHelper.getMyHostName());
		assertEquals(13, WritableHelper.parseInt(WritableHelper.toBytes(13)));
		assertEquals("test string", WritableHelper.parseString(WritableHelper.toBytes("test string")));
	}

}
