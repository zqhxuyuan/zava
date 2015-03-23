package edu.berkeley.cs162;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class KVMessageTest {
	KVMessage k;
	@Before
	public void setup() throws Exception {
		k = new KVMessage("putreq");
		k.setKey("I like pie");
		k.setValue("I like cake");
	}

	@Test
	public void test() throws Exception {
		assertEquals(
				k.toXML(),
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<KVMessage type=\"putreq\">\n<Key>I like pie</Key>\n<Value>I like cake</Value>\n</KVMessage>\n");
	}


	@Test
	public void test2() throws Exception {
		FakeSocket fake = new FakeSocket(k.toXML());
		KVMessage q = new KVMessage(fake);
		assertEquals(
				q.toXML(),
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<KVMessage type=\"putreq\">\n<Key>I like pie</Key>\n<Value>I like cake</Value>\n</KVMessage>\n");

	}
	
	@Test 
	public void test1() throws Exception {
		KVMessage a = new KVMessage("delreq");
		a.setKey("Testing is a complete waste of my time! Hooray!");
		assertEquals(a.toXML(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<KVMessage type=\"delreq\">\n<Key>Testing is a complete waste of my time! Hooray!</Key>\n</KVMessage>\n");
	}
	
	@Test
	public void test3() throws Exception {
		FakeSocket fake = new FakeSocket(k.toXML());
		KVMessage q = new KVMessage(fake, 100);
		assertEquals(
				q.toXML(),
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<KVMessage type=\"putreq\">\n<Key>I like pie</Key>\n<Value>I like cake</Value>\n</KVMessage>\n");
	}
	@Test
	public void test4() throws Exception {
		FakeSocket fake = new FakeSocket(k.toXML());
		try {
		KVMessage q = new KVMessage(fake, 5);
		} catch (KVException e) {
			return;
		}
		assertTrue(false);
	}
	@Test
	public void test5() throws Exception {
		KVClient c = new KVClient("Testing is stupid!!!", 1);
		c.ignoreNext();
		c.put("Testing sucks", "I hate testing");
		//will throw error if ignoreNext is broken since c not connected
	}
}
