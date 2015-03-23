package edu.berkeley.cs162;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import org.junit.Test;

@SuppressWarnings("all")
public class TPCLogTest {

	@Test
	public void test() throws Exception {
		KVServer s = new KVServer(100, 100);
		TPCLog log = new TPCLog("test", s);
		KVMessage x = new KVMessage("putreq");
		x.setKey("hello");
		x.setValue("bye bye");
		log.appendAndFlush(x);
		s.put("hello", "bye bye");
		ObjectInputStream f = new ObjectInputStream(new FileInputStream("test"));
		ArrayList<KVMessage> garbage = (ArrayList<KVMessage>) f.readObject();
		KVMessage mess = garbage.get(0);
		assertEquals(mess.getKey(), "hello");
		assertEquals(mess.getValue(), "bye bye");

	}
	
	@Test
	public void test2() throws Exception {
		KVServer s = new KVServer(100, 100);
		TPCLog log = new TPCLog("test", s);
		KVMessage x = new KVMessage("putreq");
		x.setKey("hello");
		x.setValue("bye bye");
		log.appendAndFlush(x);
		log.setKvServer(null);
		log.rebuildKeyServer();
		assertEquals(log.getKvServer().get("hello"), "bye bye");
	}

}
