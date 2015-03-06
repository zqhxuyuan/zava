package com.ctriposs.tsdb.test;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.ctriposs.tsdb.DBConfig;
import com.ctriposs.tsdb.DBEngine;
import com.ctriposs.tsdb.test.util.TestUtil;


public class DBEngineStressTest {

    private static final String TEST_DIR = TestUtil.TEST_BASE_DIR +"/stress/";

    private static DBEngine engine;

    public static void main(String[] args) throws IOException {
        int numKeyLimit = 1024 * 160;
        int valueLengthLimit = 1024 * 16;

        DBConfig config = new DBConfig(TEST_DIR);
        engine = new DBEngine(config);
        
        String[] rndStrings = new String[] {
                TestUtil.randomString(valueLengthLimit/2),
                TestUtil.randomString(valueLengthLimit),
                TestUtil.randomString(valueLengthLimit + valueLengthLimit/2)
        };

        Random random = new Random();
        Map<String, byte[]> bytesMap = new HashMap<String, byte[]>();
        
        System.out.println("Start from date " + new Date());
        long start = System.currentTimeMillis();
        for (long counter = 0;; counter++) {
        	
        	String rndKey = String.valueOf(random.nextInt(numKeyLimit));    
        	byte[] rndValue = rndStrings[random.nextInt(3)].getBytes();
        	long time = System.currentTimeMillis();
        	engine.put(rndKey, rndKey, time, rndValue);
        	String key = rndKey+"-"+time;
        	bytesMap.put(key, rndValue);
            if (bytesMap.size() == 3*numKeyLimit) {
                System.out.println("Current date:        " + new Date());
                System.out.println("counter:             " + counter);
                System.out.println("store level          " + engine.getStoreCounter(0));
                System.out.println("store level error    " + engine.getStoreErrorCounter(0));
                System.out.println("compact level        " + engine.getStoreCounter(1));
                System.out.println("compact level error  " + engine.getStoreErrorCounter(1));
                for (Entry<String, byte[]> entry:bytesMap.entrySet()) {
                     String[] keys = entry.getKey().split("-");
                     byte[] mapValue = entry.getValue();
                     byte[] engineValue = engine.get(keys[0], keys[0], Long.parseLong(keys[1]));
                     
                     if (mapValue == null && engineValue != null) {
                         System.out.println("Key:" + key);
                         System.out.println("engine Value:" + new String(engineValue));
                         //throw new RuntimeException("Validation exception, key exists in cache but not in map");
                     }
                     if (mapValue != null && engineValue == null) {
                     	 System.out.println("Validation exception, key exists in map but not in cache");
                         System.out.println("Key:" + key);
                         System.out.println("engine Value:" + new String(engineValue));
                         System.out.println("map Value:" + new String(mapValue));
                         //throw new RuntimeException("Validation exception, key exists in map but not in cache");

                     }
                     if (engineValue != null && mapValue != null) {
                         if (compare(mapValue, engineValue) != 0) {
                         	System.out.println("Validation exception, value in map does not equal to cache");
                             System.out.println("Key:" + key);
                             System.out.println("engine Value:" + new String(engineValue));
                             System.out.println("cache Value:" + new String(mapValue));
                          
                            // throw new RuntimeException("Validation exception, value in map does not equal to cache");
                         }
                         
                     }
                }
                bytesMap.clear();
            }
        }
        
        
        
    }

    public static int compare(byte[] left, byte[] right) {
        for (int i = 0, j = 0; i < left.length && j < right.length; i++, j++) {
            int a = (left[i] & 0xff);
            int b = (right[j] & 0xff);
            if (a != b) {
                return a - b;
            }
        }

        return left.length - right.length;
    }

}