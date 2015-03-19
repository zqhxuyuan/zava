package com.github.hoffart.dmap.util;

import java.nio.ByteBuffer;

/**
 * Util methods for working on byte arrays.
 */
public class ByteUtils {
  
  /**
   * Returns the byte[] representation of the int 
   * (creates a new ByteBuffer each time).
   * 
   * @param i Input int
   * @return  byte[] representation of i
   */
  public static byte[] getBytes(int i) {
    ByteBuffer bb = ByteBuffer.allocate(4);
    bb.putInt(i);
    return bb.array();
  }
}
