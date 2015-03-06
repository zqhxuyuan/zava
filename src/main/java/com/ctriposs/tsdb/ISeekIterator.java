package com.ctriposs.tsdb;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

public interface ISeekIterator<K,V> extends Iterator<Entry<K, V>> {
	
	void seek(String table, String column, long time) throws IOException;
	
	void seek(int code, long time) throws IOException;
	
	String table();
	
	String column();
	
	K key();
	
	long time();
	
	byte[] value() throws IOException;
	
	boolean valid();
	
	boolean hasPrev();
	
	Entry<K,V> prev();
	
	long priority();

	void close() throws IOException;
}
