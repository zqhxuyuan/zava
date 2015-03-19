/**
 * 
 */
package com.github.seanlinwang.fkv;

import java.io.IOException;

/**
 * 给调用者暴露的接口.对于KV,只有两种操作get和set.
 * 我们不应该关心具体底层的存储方式,比如对于文件,在保存的时候需要传递position参数.
 * @author sean.wang
 * @since Nov 15, 2011
 */
public interface Fkv {

	/**
	 * get record by key
	 * 
	 * @param key
	 * @return
	 */
	String get(String key);

	/**
	 * put record
	 * 
	 * @param key
	 * @param value
	 */
	void put(String key, String value);

	/**
	 * delete record
	 * 
	 * @param key
	 */
	void delete(String key);

	/**
	 * active record size
	 * 
	 * @return
	 */
	int size();

	/**
	 * close fkv
	 * 
	 * @throws IOException
	 */
	void close() throws IOException;

	/**
	 * delete all records
	 */
	void clear();

}
