/**
 * 
 */
package com.github.seanlinwang.fkv;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author sean.wang
 * @since Nov 16, 2011
 */
public class FkvFileStore implements FkvStore {

	private RandomAccessFile in;
	private FileChannel ch;
	private MappedByteBuffer buffer;
	private final static int DEFAULT_DB_SIZE = 1024 * 1024;

	private boolean needDeserial = false;

	public FkvFileStore(File dbFile) throws IOException {
		this(dbFile, 0);
	}

	public FkvFileStore(File dbFile, int dbSize) throws IOException {
		if (!dbFile.exists()) {
			dbFile.createNewFile();
		}
		in = new RandomAccessFile(dbFile, "rw");
		ch = in.getChannel();
		long mappedSize = dbFile.length();
		if (mappedSize <= 0) {
			if (dbSize <= 0) {
				mappedSize = DEFAULT_DB_SIZE;
			} else {
				mappedSize = dbSize;
			}
		} else {
			this.needDeserial = true;
		}
        //文件提供了内存的持久化方式,否则内存中的数据一旦丢失,则数据无法恢复
		buffer = ch.map(FileChannel.MapMode.READ_WRITE, 0, mappedSize);
	}

	@Override
	public void close() throws IOException {
		ch.close();
		in.close();
	}

	@Override
	public void get(byte[] bytes) {
		this.buffer.get(bytes);
	}

    // 对于文件而言, 获取出的数据都是二进制的字节数组! 客户端实现类要自己将二进制转换成自定义对象
	@Override
	public byte[] get(int startIndex, int size) {
		byte[] value = new byte[size];
		buffer.position(startIndex);
		buffer.get(value);
		return value;
	}

	@Override
	public ByteBuffer getBuffer() {
		return this.buffer;
	}

	@Override
	public boolean isNeedDeserial() {
		return needDeserial;
	}

	@Override
	public void put(int startIndex, byte value) {
		buffer.position(startIndex);
		buffer.put(value);
	}

	@Override
	public void put(int startIndex, byte[] value) {
		buffer.position(startIndex);
		buffer.put(value);
	}

	@Override
	public int remaining() {
		return this.buffer.remaining();
	}

	@Override
	public void rewind() {
		this.buffer.rewind();
	}

}
