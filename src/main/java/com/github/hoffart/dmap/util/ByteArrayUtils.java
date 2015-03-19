package com.github.hoffart.dmap.util;


public class ByteArrayUtils {
  /**
   * Returns the maximum element that is less than given target
   *    1. Any element that matches target is returned.
   *    2. If target greater than last element, the last array element is returned
   *    3. If target less than first element, null is returned
   *    
   * @param keys A sorted array of ByteArray elements to be searched
   * @param target Search element.
   * @return The maximum element less than given target
   */
  public static ByteArray findMaxElementLessThanTarget(ByteArray[] keys, ByteArray target) {
    return binarySearch(keys, 0, keys.length-1, target);
  }
  
  private static ByteArray binarySearch(ByteArray[] keys, int start, int end, ByteArray target) {
    // start will never be greater than end
    if(end-start <= 1) {
      if(target.compareTo(keys[end]) >= 0) {
        return keys[end];
      }else if(target.compareTo(keys[start]) >= 0) {
        return keys[start];
      }else {
        // Should never come here.
        // Target is not in the range of keys provided
        // DMapBuilder never added the target element during creation phase.
        return null;
      }
    }

    int mid = ((end - start)/2) + start;
    int cmp = target.compareTo(keys[mid]);
    if(cmp == 0) {
      return keys[mid];
    } else if(cmp < 0) {
      return binarySearch(keys, start, mid-1, target);
    } else { 
      return binarySearch(keys, mid, end, target);
    }    
  }
}