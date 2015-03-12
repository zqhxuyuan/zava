package hdgl.db.server.bsp;

import static org.junit.Assert.*;
import hdgl.db.protocol.MessagePackWritable;
import hdgl.db.protocol.MessageWritable;
import hdgl.util.WritableHelper;

import org.junit.Test;

public class MessageTest {

	@Test
	public void test() {
		MessageWritable msg = new MessageWritable();
		msg.add(1, new long[]{1,2,3});
		msg.add(2, new long[]{2,3,4});
		byte[] data = WritableHelper.toBytes(msg);
		MessageWritable other=WritableHelper.parse(data, MessageWritable.class);
		assertEquals(2, other.size());
		for(int i=0;i<other.size();i++){
			assertEquals(msg.getState(i), other.getState(i));
			assertArrayEquals(msg.getPath(i), other.getPath(i));
		}
		MessagePackWritable msgPack=new MessagePackWritable();
		msgPack.add(1, msg);
		MessagePackWritable other2 = WritableHelper.parse(WritableHelper.toBytes(msgPack), MessagePackWritable.class);
		assertEquals(msgPack.size(), other2.size());
		for(int j=0;j<other2.size();j++){
			other = other2.getMessage(j);
			for(int i=0;i<other.size();i++){
				assertEquals(msg.getState(i), other.getState(i));
				assertArrayEquals(msg.getPath(i), other.getPath(i));
			}
		}
	}

}
