package com.ctriposs.tsdb.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.ctriposs.tsdb.DBConfig;
import com.ctriposs.tsdb.DBEngine;
import com.ctriposs.tsdb.test.util.TestUtil;

public class DBEnginePutGetFunctionTest {

    private static final String TEST_DIR = TestUtil.TEST_BASE_DIR +"put_test";
    private static final int INIT_COUNT = 10*1000*1000;
    private static DBEngine engine;

    public static void main(String[] args) throws IOException  {

    	int numKeyLimit = 30000;
        DBConfig config = new DBConfig(TEST_DIR);
        engine = new DBEngine(config);
        
        Random random = new Random();
        long start = System.nanoTime();
        
        String data = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        
        Map<String,String> map = new HashMap<String,String>();
        
        for (int i = 0; i < 2*INIT_COUNT; i++) {
        	String rndKey = String.valueOf(random.nextInt(numKeyLimit));

        	long time = System.currentTimeMillis();
        	String value = data+i;
        	engine.put(rndKey, rndKey, time, value.getBytes());
        	if(map.size()<3*numKeyLimit){
        		map.put(rndKey + "-" + time,value);
        	}
        }
        
        long duration = System.nanoTime() - start;
        System.out.printf("Put/get %,d K operations per second single thread%n",
                (int) (INIT_COUNT * 2 * 1e6 / duration));
        
        int total=0;
        int miss = 0;
        int error = 0;
        for(Entry<String,String> entry:map.entrySet()){
        	String keys[] = entry.getKey().split("-");
        	total++;
        	byte[] enginValue = engine.get(keys[0],keys[0],Long.parseLong(keys[1]));
        	if(enginValue != null){
        		String engineStr = new String(enginValue);
        		if(!entry.getValue().equals(engineStr)){
        			System.out.println(++error+"error ");
        			System.out.println("map value	:"+entry.getValue());
        			System.out.println("engine value:"+engineStr);
        		}
        	}else{
        		System.out.println(++miss+"not found "+entry.getKey()+":"+entry.getValue());
        	}
        }

        System.out.println("total:"+total+"miss:"+miss+"error:"+error);

		
	}
}
