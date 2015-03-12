package hdgl.db.graph;

import hdgl.db.exception.HdglException;

import org.junit.Test;

public class ExceptionTest {

	@Test
	public void test() {
		try{
			throw new HdglException("test", new Exception("Test Cause"));
		}catch(HdglException ex){
			ex.printStackTrace();
		}
	}

}
