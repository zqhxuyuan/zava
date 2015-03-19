package com.github.hoffart.dmap.util;

import java.nio.ByteBuffer;

/**
 * VInt compression taken from WritableUtils in Apache Hadoop 1.0.3!
 */
public final class CompressionUtils {
  public static int writeVInt(ByteBuffer bb, int i) {
    return writeVLong(bb, i);
  }
  
  public static int writeVLong(ByteBuffer bb, long l) {
    int initPos = bb.position();
    if (l >= -112 && l <= 127) {
      bb.put((byte) l);
      return 1;
    }

    int len = -112;
    if (l < 0) {
      l ^= -1L; // take one's complement'
      len = -120;
    }

    long tmp = l;
    while (tmp != 0) {
      tmp = tmp >> 8;
      len--;
    }

    bb.put((byte)len);

    len = (len < -120) ? -(len + 120) : -(len + 112);

    for (int idx = len; idx != 0; idx--) {
      int shiftbits = (idx - 1) * 8;
      long mask = 0xFFL << shiftbits;
      bb.put((byte) ((l & mask) >> shiftbits));
    }
    return bb.position() - initPos;
  }
  
  public static int readVInt(ByteBuffer bb) {
    return (int) readVLong(bb);
  }

  public static int readVInt(ByteBuffer bb, int len, byte firstByte) {
    return (int) readVLong(bb, len, firstByte);
  }

  public static long readVLong(ByteBuffer bb) {
    byte firstByte = bb.get();
    int len = decodeVNumSize(firstByte);
    return readVLong(bb, len, firstByte);
  }
  
  public static long readVLong(ByteBuffer bb, int len, byte firstByte) {
    if (len == 1) {
      return firstByte;
    }
    long l = 0;
    for (int idx = 0; idx < len-1; idx++) {
      byte b = bb.get();
      l = l << 8;
      l = l | (b & 0xFF);
    }
    return (isNegativeVNum(firstByte) ? ~l : l);
  }

  public static boolean isNegativeVNum(byte value) {
    return value < -120 || (value >= -112 && value < 0);
  }
  
  public static int decodeVNumSize(byte value) {
    if (value >= -112) {
      return 1;
    } else if (value < -120) {
      return -119 - value;
    }
    return -111 - value;
  }


  public static int getVNumSize(long i) {
    if (i >= -112 && i <= 127) {
      return 1;
    }

    if (i < 0) {
      i ^= -1L; // take one's complement'
    }
    // find the number of bytes with non-leading zeros
    int dataBits = Long.SIZE - Long.numberOfLeadingZeros(i);
    // find the number of data bytes + length byte
    return (dataBits + 7) / 8 + 1;
  }
}