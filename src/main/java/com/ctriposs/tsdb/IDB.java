package com.ctriposs.tsdb;

import java.io.Closeable;
import java.io.IOException;

public interface IDB extends ISeekIterable<InternalKey, byte[]>, Closeable {

	/**
	 * Puts the value with the specified table and column for time.
	 *
	 * @param tableName the table name
	 * @param colName the column name
	 * @param time the time
	 * @param value the value
	 * @throws IOException
	 */
	void put(String tableName,String colName,long time,byte[] value)throws IOException;
	
	/**
	 * Gets the value with the specified table and column for time.
	 *
	 * @param tableName the table name
	 * @param colName the column name
	 * @param time the time
	 * @return the value
	 * @throws IOException
	 */
	byte[] get(String tableName,String colName,long time)throws IOException;
	
	
	/**
	 * Delete all data after time.
	 *
	 * @param time the time
	 * @throws IOException
	 */
	void delete(long afterTime)throws IOException;
}
