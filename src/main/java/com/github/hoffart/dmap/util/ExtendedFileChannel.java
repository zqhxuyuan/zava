package com.github.hoffart.dmap.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class ExtendedFileChannel {
  public static final byte TRUE_BYTE = 1;
  public static final byte FALSE_BYTE = 0;
  
  private FileChannel fileChannel_;
  private final ByteBuffer boolBuffer_;
  private final ByteBuffer intBuffer_;
  private final ByteBuffer vIntBuffer_;
  private final ByteBuffer longBuffer_;
  private final ByteBuffer vLongBuffer_;

  public ExtendedFileChannel(FileChannel fileChannel) {
    fileChannel_ = fileChannel;
    boolBuffer_ = ByteBuffer.allocate(1);
    intBuffer_ = ByteBuffer.allocate(4);
    vIntBuffer_ = ByteBuffer.allocate(5);
    longBuffer_ = ByteBuffer.allocate(8);
    vLongBuffer_ = ByteBuffer.allocate(9);
  }
  
  public int write(ByteBuffer byteBuffer) throws IOException {
    byteBuffer.position(0);
    return fileChannel_.write(byteBuffer);
  }
  
  public int write(byte[] bytes) throws IOException {
    return write(ByteBuffer.wrap(bytes));
  }

  public int writeBool(boolean value) throws IOException {
    boolBuffer_.rewind();
    boolBuffer_.put(value ? TRUE_BYTE : FALSE_BYTE);
    return write(boolBuffer_);
  }

  public int writeInt(int value) throws IOException {
    intBuffer_.rewind();
    intBuffer_.putInt(value);
    return write(intBuffer_);
  }

  public int writeVInt(int value) throws IOException {
    vIntBuffer_.clear();
    int length = CompressionUtils.writeVInt(vIntBuffer_, value);
    vIntBuffer_.rewind();
    vIntBuffer_.limit(length);
    return fileChannel_.write(vIntBuffer_);
  }

  public int writeLong(long value) throws IOException {
    longBuffer_.rewind();
    longBuffer_.putLong(value);
    return write(longBuffer_);
  }
  
  public int writeVLong(long value) throws IOException {
    vLongBuffer_.clear();
    int length = CompressionUtils.writeVLong(vLongBuffer_, value);
    vLongBuffer_.rewind();
    vLongBuffer_.limit(length);
    return fileChannel_.write(vLongBuffer_);
  }
  
  public int read(ByteBuffer byteBuffer) throws IOException {
    return fileChannel_.read(byteBuffer);
  }
  
  public int read(byte[] bytes) throws IOException {
    return read(ByteBuffer.wrap(bytes));
  }
  
  public boolean readBool() throws IOException {
    boolBuffer_.rewind();
    read(boolBuffer_);
    return boolBuffer_.get(0) == TRUE_BYTE;
  }
  
  public int readInt() throws IOException {
    intBuffer_.rewind();
    read(intBuffer_);
    return intBuffer_.getInt(0);
  }
  
  public int readVInt() throws IOException {
    return (int) readVLong();
  }
  
  public long readLong() throws IOException {
    longBuffer_.rewind();
    read(longBuffer_);
    return longBuffer_.getLong(0);
  }
  
  public long readVLong() throws IOException {
    vLongBuffer_.clear();
    vLongBuffer_.limit(1);
    read(vLongBuffer_);
    byte firstByte = vLongBuffer_.get(0);
    int length = CompressionUtils.decodeVNumSize(firstByte);
    vLongBuffer_.limit(length);
    read(vLongBuffer_);
    vLongBuffer_.position(1);
    return CompressionUtils.readVLong(vLongBuffer_, length, firstByte);
  }
  
  public void flush() throws IOException {
    fileChannel_.force(true);
  }
  
  public void close() throws IOException {
    fileChannel_.close();
  }
  
  public long size() throws IOException {
    return fileChannel_.size();
  }
  
  public long position() throws IOException {
    return fileChannel_.position();
  }
  
  public ExtendedFileChannel position(long newPosition) throws IOException {
    fileChannel_.position(newPosition);
    return this;
  }
  
  public MappedByteBuffer map(FileChannel.MapMode mapMode, long position, long size) throws IOException {
    return fileChannel_.map(mapMode, position, size);
  }
}
