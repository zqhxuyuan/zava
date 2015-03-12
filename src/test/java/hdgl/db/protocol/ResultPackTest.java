package hdgl.db.protocol;

import static org.junit.Assert.*;
import hdgl.util.WritableHelper;

import org.junit.Test;

public class ResultPackTest {

	@Test
	public void test() {
		long[][] path=new long[][]{
				new long[]{1,2,3},
				new long[]{2,3,4},
				new long[]{3,4,5},
		};
		ResultPackWritable r = new ResultPackWritable(path, false);
		ResultPackWritable other = WritableHelper.parse(WritableHelper.toBytes(r), ResultPackWritable.class);
		long[][] o=other.getResult();
		assertEquals(false, other.isHasMore());
		assertEquals(path.length, o.length);
		for(int i=0;i<path.length;i++){
			assertArrayEquals(path[i], o[i]);
		}
	}

}
