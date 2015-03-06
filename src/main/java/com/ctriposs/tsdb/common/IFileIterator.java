package com.ctriposs.tsdb.common;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import com.ctriposs.tsdb.storage.CodeItem;

public interface IFileIterator<K, V> extends Iterator<Entry<K, V>> {
	
	/**
	 * 
	 * @param code
	 * @param time
	 * @throws IOException
	 */
	void seek(int code,long time) throws IOException;
	
	/**
	 * seek code from start positon
	 * @param code
	 * @param isNext
	 * @throws IOException
	 */
	void seekToFirst(int code,boolean isNext) throws IOException;
	
	/**
	 * seek code from current postion
	 * @param isNext
	 * @return
	 * @throws IOException
	 */
	boolean seekToCurrent(boolean isNext) throws IOException;

	/**
	 * get next code item for the current code block
	 * @return
	 * @throws IOException
	 */
	CodeItem nextCode() throws IOException;
	
	/**
	 * get previous code item for the current code block
	 * @return
	 * @throws IOException
	 */
	CodeItem prevCode() throws IOException;	
	
	/**
	 * get current code item for the current code block
	 * @return
	 * @throws IOException
	 */
	CodeItem currentCode() throws IOException;	
	
	/**
	 * judge if has next code
	 * @return
	 * @throws IOException
	 */
	boolean hasNextCode() throws IOException;
	
	/**
	 * judge if has previous code
	 * @return
	 * @throws IOException
	 */
	boolean hasPrevCode() throws IOException;	
	
	/**
	 * get all time item count for the file
	 * @return
	 */
	long timeItemCount();
	
	/**
	 * get the iterator priority
	 * @return
	 */
	long priority();
	
	/**
	 * get current key
	 * @return
	 */
	K key();

	/**
	 * get current time
	 * @return
	 */
	long time();

	/**
	 * get current value
	 * @return
	 * @throws IOException
	 */
	byte[] value() throws IOException;

	/**
	 * judge if current entry is valid
	 * @return
	 */
	boolean valid();
	
	/**
	 * judge if has previous entry
	 * @return
	 */
    boolean hasPrev();

    /**
     * get previous entry
     * @return
     */
	Entry<K, V> prev();
	
	/**
	 * get current entry
	 * @return
	 */
	Entry<K, V> current();

	/**
	 * close iterator
	 * @throws IOException
	 */
	void close() throws IOException;
}
