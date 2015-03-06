package com.zqh.java.dmap.test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import com.zqh.java.dmap.util.map.CachingHashMap;

public class CachingHashMapTest {
  @Test
  public void testForUnchangedMapSizeForMultipleInserts() {
    int limit = 3;
    CachingHashMap<Integer, Integer> cachedIntegers = new CachingHashMap<>(limit);
    int count = 0;
    for(int i=0;i<10;i++) {
      cachedIntegers.put(i, i);
      if(count < limit) {
        assertEquals(++count, cachedIntegers.size());
      } else {
        assertEquals(count, cachedIntegers.size());
      }
      
    }
  }
  
  @Test
  public void testForOldestEntryRemoval() {
    int limit = 3;
    CachingHashMap<Integer, Integer> cachedIntegers = new CachingHashMap<>(limit);
    for(int i=0;i<10;i++) {
      cachedIntegers.put(i, i);
    }
    assertEquals(3, cachedIntegers.size());
    assertEquals(false, cachedIntegers.containsKey(1));
    assertEquals(false, cachedIntegers.containsKey(5));
    assertEquals(false, cachedIntegers.containsKey(6));
    assertEquals(true, cachedIntegers.containsKey(7));
    assertEquals(true, cachedIntegers.containsKey(9));
    assertEquals(true, cachedIntegers.containsKey(8));    
  }
}
