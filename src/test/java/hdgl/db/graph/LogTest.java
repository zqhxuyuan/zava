package hdgl.db.graph;

import static org.junit.Assert.*;

import hdgl.db.store.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.Test;

public class LogTest {

	@Test
	public void test() throws IOException {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		DataOutput out = new DataOutputStream(buf);
		Log.addVertex(0, "abcde").write(out);
		Log.addEdge(1, "bcdef", 0, 2).write(out);
		Log.setLabel(2, "cdefg", null).write(out);
		Log.setLabel(3, "defgh", new byte[]{1,2,3,4,5}).write(out);
		DataInput in = new DataInputStream(new ByteArrayInputStream(buf.toByteArray()));
		Log l = new Log();
		l.readFields(in);
		assertEquals(0, l.getId1());
		assertEquals("abcde",l.getName());
		l.readFields(in);
		assertEquals(1, l.getId1());
		assertEquals(0, l.getId2());
		assertEquals(2, l.getId3());
		assertEquals("bcdef",l.getName());
		l.readFields(in);
		assertEquals(2, l.getId1());
		assertEquals("cdefg",l.getName());
		assertEquals(null,l.getData());
		l.readFields(in);
		assertEquals(3, l.getId1());
		assertEquals("defgh",l.getName());
		assertArrayEquals(new byte[]{1,2,3,4,5},l.getData());
	}

}
