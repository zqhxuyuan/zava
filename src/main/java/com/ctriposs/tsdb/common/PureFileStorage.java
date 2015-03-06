package com.ctriposs.tsdb.common;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class PureFileStorage implements IStorage {

	private FileChannel fileChannel;
	private RandomAccessFile raf;
	private String fullFileName;

	public PureFileStorage(String dir, long time, String suffix, long capacity) throws IOException {
		File dirFile = new File(dir);
		if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

		fullFileName = dir + time+"-"+suffix;
		raf = new RandomAccessFile(fullFileName, "rw");
		raf.setLength(capacity);
		fileChannel = raf.getChannel();
	}
	
	public PureFileStorage(String dir, long time, String suffix) throws IOException {
		File dirFile = new File(dir);
		if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
		fullFileName = dir + time+"-"+suffix;
		raf = new RandomAccessFile(fullFileName, "rw");
		fileChannel = raf.getChannel();
	}

	public PureFileStorage(File file) throws IOException {
		raf = new RandomAccessFile(file, "rw");
		fullFileName = file.getPath();
		fileChannel = raf.getChannel();
	}
	
	@Override
	public void get(long position, byte[] dest) throws IOException {
		fileChannel.read(ByteBuffer.wrap(dest), position);
	}

	@Override
	public void put(long position, byte[] source) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(source);

        while (byteBuffer.hasRemaining()) {
            int len = fileChannel.write(byteBuffer, position);
            position += len;
        }
	}
	
	@Override
	public void put(long position, ByteBuffer source) throws IOException {

        while (source.hasRemaining()) {
            int len = fileChannel.write(source, position);
            position += len;
        }
	}

	@Override
	public void free() {
		// nothing to do here
		try {
			fileChannel.truncate(0);
		} catch (IOException e) {
		}
	}

	@Override
	public void close() throws IOException {
		if (this.fileChannel != null) {
			this.fileChannel.close();
		}
		if (this.raf != null) {
			this.raf.close();
		}
	}

	@Override
	public String getName() {
		return fullFileName;
	}


}
