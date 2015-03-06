package com.ctriposs.tsdb.common;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;


/**
 * The Interface IStorage for get/put cached data in bytes.
 */
public interface IStorage  extends Closeable {
	
	public static final String DATA_FILE_SUFFIX = ".data";
	public static final String META_FILE_SUFFIX = ".meta";
	public static final String LOG_FILE_SUFFIX = ".log";
	/**
	 * Gets bytes from the specified location.
	 *
	 * @param position the position
	 * @param dest the destination
	 */
	void get(long position, byte[] dest) throws IOException;
	
	/**
	 * Puts source to the specified location of the Storage.
	 *
	 * @param position the position
	 * @param source the source
	 */
	void put(long position, byte[] source) throws IOException;
	
	/**
	 * Puts source to the specified location of the Storage.
	 *
	 * @param position the position
	 * @param source the source
	 */
	void put(long position, ByteBuffer source) throws IOException;
	
	/**
	 * Frees the storage.
	 */
	void free();
	
	
	public String getName();
  
}

