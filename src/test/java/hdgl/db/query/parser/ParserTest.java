package hdgl.db.query.parser;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import hdgl.db.query.convert.QueryCompletion;
import hdgl.db.query.convert.QueryToStateMachine;
import hdgl.db.query.expression.Expression;
import hdgl.db.query.stm.SimpleStateMachine;
import hdgl.db.query.stm.StateMachine;


import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenRewriteStream;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class ParserTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	QueryParser parser(String query){
		QueryLexer lexer=new QueryLexer(new ANTLRStringStream(query));
		QueryParser parser = new QueryParser(new TokenRewriteStream(lexer));
		return parser;
	}
	
	@Test
	public void experiment(){
		HashSet<Integer> set1 = new HashSet<Integer>();
		HashSet<Integer> set2 = new HashSet<Integer>();
		assertEquals(set1, set2);
		set1.add(1);
		set2.add(1);
		assertEquals(set1, set2);
		set1.add(2);
		assertTrue(!set1.equals(set2));
		set2.add(2);
		assertEquals(set1, set2);
		assertNotSame(set1, set2);
		set1.add(10003);
		set2.add(10003);
		assertEquals(set1, set2);
		set1.add(new Integer(50));
		set2.add(new Integer(50));
		assertEquals(set1, set2);
		Set<Set<Integer>> set3 = new HashSet<Set<Integer>>();
		set3.add(set1);
		assertFalse(set3.add(set2));
		assertEquals(set3.size(), 1);
	}
	
	@Test
	public void test() throws RecognitionException {
		assertEquals(parser(".[desc:label<=val]*").expression().toString(), parser(".[DESC:label][label<=val]*").expression().toString());
		assertEquals(parser(".-.[<:price]").expression().toString(), parser("((. -) .[ASC:price])").expression().toString());
		assertEquals(parser(".[id=1]|-[price<10](.)*").expression().toString(), parser("(.[id=1]|-[price<10] .*)").expression().toString());
		assertEquals(parser(".[desc:label<=val]*").expression().clone().toString(), parser(".[DESC:label][label<=val]*").expression().toString());
		assertEquals(parser(".-.[<:price]").expression().clone().toString(), parser("((. -) .[ASC:price])").expression().toString());
		assertEquals(parser(".[id=1]|-[price<10](.)*").expression().clone().toString(), parser("(.[id=1]|-[price<10] .*)").expression().toString());
		
		assertEquals("(.[id=1]|((. -[price<10]) .) (- .)*)", QueryCompletion.complete(parser(".[id=1]|-[price<10](.)*").expression()).toString());
		assertEquals("((. -[price<10])|(.[id=1] -) ((. -)* .))", QueryCompletion.complete(parser("-[price<10]|.[id=1](.)*").expression()).toString());
		Expression q = QueryCompletion.complete(parser(".-.").expression());
		SimpleStateMachine stm = QueryToStateMachine.convert(q);
		stm.print(System.out);
		StateMachine fstm = stm.buildStateMachine();
		//fstm.print(System.out);
		System.out.print(fstm);
		
		q = QueryCompletion.complete(parser(".[id=1]|-[price<10](.)*").expression());
		stm = QueryToStateMachine.convert(q);
		//stm.print(System.out);
		fstm = stm.buildStateMachine();
		//fstm.print(System.out);
		//System.out.print(fstm);
		try{
			ByteArrayOutputStream out=new ByteArrayOutputStream();
			DataOutput d = new DataOutputStream(out);
			fstm.write(d);
			ByteArrayInputStream in=new ByteArrayInputStream(out.toByteArray());
			DataInput input = new DataInputStream(in);
			StateMachine nstm = new StateMachine();
			nstm.readFields(input);
			assertEquals(fstm.toString(), nstm.toString());
		}catch (IOException e) {
			fail(e.getMessage());
		}
	}

}
