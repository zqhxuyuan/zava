package com.github.seanlinwang.fkv;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * KV的文件存储接口
 * 针对文件而言,写到文件里的数据需要通过索引才可以高效地读取出来.
 * 文件的一个很重要的特性就是position信息.即startIndex.
 * 接口方法的参数暴露了position参数.具体的KV实现会调用KV的文件存储实现类,传递position参数从而写入数据到文件中
 * @author sean.wang
 * @since Nov 16, 2011
 */
public interface FkvStore {
    /**
     * 从索引处开始读取,一共读取size个大小
     * @param startIndex
     * @param size
     * @return
     */
	byte[] get(int startIndex, int size);

    /**
     * 从指定位置开始写入数据,数据的内容是value
     * @param startIndex
     * @param value
     */
	void put(int startIndex, byte[] value);

    /**
     * 从指定位置写入一共字节的数据
     * @param startIndex
     * @param value
     */
	void put(int startIndex, byte value);

	void close() throws IOException;

	boolean isNeedDeserial();

	ByteBuffer getBuffer();

	void rewind();

	int remaining();

	void get(byte[] bytes);
}
