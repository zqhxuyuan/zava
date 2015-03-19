package com.github.hoffart.dmap.util;

import java.util.Arrays;

/**
 * Wrapper for a byte[] to be used as key in a Map.
 */
public class ByteArray implements Comparable<ByteArray> {
  private final byte[] bytes_;

  public ByteArray(byte[] bytes) {
    bytes_ = bytes;
  }

  public byte[] getBytes() {
    return bytes_;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(bytes_);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ByteArray) {
      ByteArray comp = (ByteArray) obj;
      return Arrays.equals(comp.bytes_, bytes_);
    } else {
      return false;
    }
  }

  @Override
  public int compareTo(ByteArray b) {
    byte[] bBytes = b.getBytes();
    if (bytes_.length != bBytes.length) {
      return bytes_.length - bBytes.length;
    } else {
      for (int i = 0; i < bytes_.length; ++i) {
        // comparing unsigned ints instead of signed bytes
        int maskedThis = (bytes_[i] & 0xff);
        int maskedB = (bBytes[i] & 0xff);
        if (maskedThis != maskedB) {
          return maskedThis - maskedB;
        }
      }
      // Everything is the same.
      return 0;
    }
  }

  @Override
  public String toString() {
    return Arrays.toString(bytes_);
  }
}