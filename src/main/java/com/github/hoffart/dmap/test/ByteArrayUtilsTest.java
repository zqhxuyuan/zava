package com.github.hoffart.dmap.test;

import static org.junit.Assert.*;

import com.github.hoffart.dmap.util.ByteArray;
import com.github.hoffart.dmap.util.ByteArrayUtils;
import com.github.hoffart.dmap.util.ByteUtils;
import org.junit.Assert;
import org.junit.Test;

public class ByteArrayUtilsTest {
  @Test
  public void testDirectKeyMatch() {
    ByteArray[] keys = new ByteArray[10];
    
    for(int i=0; i<keys.length; ++i) {
     keys[i] = new ByteArray(ByteUtils.getBytes(i));
    }

    Assert.assertEquals(new ByteArray(ByteUtils.getBytes(5)), ByteArrayUtils.findMaxElementLessThanTarget(keys, new ByteArray(ByteUtils.getBytes(5))));
  }
  
  @Test
  public void testForTargetInRangeButNotMatchingAnyKeys() {
    ByteArray[] keys = new ByteArray[5];
    
    int value = 1;
    // 1->(2,3,4), 5->(6,7,8), 9->(10,11,12), 13->(14,15,16), 17->(18,19,20,....)
    for(int i=0; i<keys.length; ++i) {
     keys[i] = new ByteArray(ByteUtils.getBytes(value));
     value+=4;
    }

    assertEquals(new ByteArray(ByteUtils.getBytes(9)), 
                  ByteArrayUtils.findMaxElementLessThanTarget(keys, new ByteArray(ByteUtils.getBytes(12))));
    
    assertEquals(new ByteArray(ByteUtils.getBytes(5)), 
        ByteArrayUtils.findMaxElementLessThanTarget(keys, new ByteArray(ByteUtils.getBytes(8))));
  }

  @Test
  public void testForTargetGreaterThanLastElement() {
    ByteArray[] keys = new ByteArray[5];
    
    int value = 1;
    // 1->(2,3,4), 5->(6,7,8), 9->(10,11,12), 13->(14,15,16), 17->(18,19,20,....)
    for(int i=0; i<keys.length; ++i) {
     keys[i] = new ByteArray(ByteUtils.getBytes(value));
     value+=4;
    }

    // In this case, the last blocks start key should be returned (though the block might not contain the actual key)
    assertEquals(new ByteArray(ByteUtils.getBytes(17)), 
        ByteArrayUtils.findMaxElementLessThanTarget(keys, new ByteArray(ByteUtils.getBytes(50))));
  }
  
  @Test
  public void testForTargetLessThanFirstElement() {
    ByteArray[] keys = new ByteArray[5];
    
    int value = 1;
    // 1 - 17
    for(int i=0; i<keys.length; ++i) {
     keys[i] = new ByteArray(ByteUtils.getBytes(value+=4)); 
    }

    assertEquals(null, 
        ByteArrayUtils.findMaxElementLessThanTarget(keys, new ByteArray(ByteUtils.getBytes(0))));
  }
  
  @Test
  public void testMoreScenarios() {
    ByteArray[] keys = new ByteArray[8];
    int value = 0;
    // The keys are incremented by 32 = > 0,32,64,....224
    for(int i=0;i<keys.length;i++) {
      keys[i] = new ByteArray(ByteUtils.getBytes(value));
      value+=32;
    }
    
    // any key less than 32 will be in block 1 starting with 0
    assertEquals(new ByteArray(ByteUtils.getBytes(0)), 
        ByteArrayUtils.findMaxElementLessThanTarget(keys, new ByteArray(ByteUtils.getBytes(31))));
    
    // any key >=32 and less than 64 will be in block 2 starting with 32
    assertEquals(new ByteArray(ByteUtils.getBytes(32)), 
        ByteArrayUtils.findMaxElementLessThanTarget(keys, new ByteArray(ByteUtils.getBytes(32))));
    
    assertEquals(new ByteArray(ByteUtils.getBytes(32)), 
        ByteArrayUtils.findMaxElementLessThanTarget(keys, new ByteArray(ByteUtils.getBytes(33))));
    
    assertEquals(new ByteArray(ByteUtils.getBytes(32)), 
        ByteArrayUtils.findMaxElementLessThanTarget(keys, new ByteArray(ByteUtils.getBytes(63))));
    
    // any key >= 64 and less than 96 will be in block 3 starting with 64
    assertEquals(new ByteArray(ByteUtils.getBytes(64)), 
        ByteArrayUtils.findMaxElementLessThanTarget(keys, new ByteArray(ByteUtils.getBytes(64))));
    
    assertEquals(new ByteArray(ByteUtils.getBytes(64)), 
        ByteArrayUtils.findMaxElementLessThanTarget(keys, new ByteArray(ByteUtils.getBytes(65))));
    
    // any key >=96 and less than 128 will be in block 4 starting with 96
    assertEquals(new ByteArray(ByteUtils.getBytes(96)), 
        ByteArrayUtils.findMaxElementLessThanTarget(keys, new ByteArray(ByteUtils.getBytes(96))));
    
    assertEquals(new ByteArray(ByteUtils.getBytes(96)), 
        ByteArrayUtils.findMaxElementLessThanTarget(keys, new ByteArray(ByteUtils.getBytes(127))));
    
    // any key >=128 and less than 160 will be in block 5 starting with 128
    assertEquals(new ByteArray(ByteUtils.getBytes(128)), 
        ByteArrayUtils.findMaxElementLessThanTarget(keys, new ByteArray(ByteUtils.getBytes(128))));
    
    assertEquals(new ByteArray(ByteUtils.getBytes(128)), 
        ByteArrayUtils.findMaxElementLessThanTarget(keys, new ByteArray(ByteUtils.getBytes(159))));
    
    // any key >=192 and less than 224 will be in block 7 starting with 192
    assertEquals(new ByteArray(ByteUtils.getBytes(192)), 
        ByteArrayUtils.findMaxElementLessThanTarget(keys, new ByteArray(ByteUtils.getBytes(223))));
    
    // any key >=224 will be in the last block 8 starting with 224
    assertEquals(new ByteArray(ByteUtils.getBytes(224)), 
        ByteArrayUtils.findMaxElementLessThanTarget(keys, new ByteArray(ByteUtils.getBytes(270))));
    
    assertEquals(new ByteArray(ByteUtils.getBytes(224)), 
        ByteArrayUtils.findMaxElementLessThanTarget(keys, new ByteArray(ByteUtils.getBytes(1024))));
    
    assertEquals(new ByteArray(ByteUtils.getBytes(224)), 
        ByteArrayUtils.findMaxElementLessThanTarget(keys, new ByteArray(ByteUtils.getBytes(1024*1024))));
  }
}
