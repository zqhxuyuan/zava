package com.ctriposs.tsdb.test;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Random;

import com.ctriposs.tsdb.DBConfig;
import com.ctriposs.tsdb.DBEngine;
import com.ctriposs.tsdb.ISeekIterator;
import com.ctriposs.tsdb.InternalKey;
import com.ctriposs.tsdb.test.util.TestUtil;

public class DBEngineSeekFunctionTest {

    private static final String TEST_DIR = TestUtil.TEST_BASE_DIR +"/seek_test";
    private static final int INIT_COUNT = 10*1000*1000;
    private static DBEngine engine;

    public static void main(String[] args) throws IOException {

    	int numKeyLimit = 300;
        DBConfig config = new DBConfig(TEST_DIR);
        engine = new DBEngine(config);


        Random random = new Random();
        long start = System.nanoTime();

        String data = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        LinkedHashMap<String,String> map = new LinkedHashMap<String,String>();


        String startKey = null;
        long startTime = 0;
        long lastTime = 0;
        String lastValue = null;
        
        for (int i = 0; i < 2*INIT_COUNT; i++) {
        	String rndKey = String.valueOf(random.nextInt(numKeyLimit));
            long time = System.currentTimeMillis();
            String key = rndKey+"-"+time;
            String value = data+i;
            if(i==0){
            	startKey = rndKey;
            	startTime = time;
            }
        	
        	engine.put(rndKey, rndKey, time, value.getBytes());
        	    
            if(rndKey.equals(startKey)){
            	map.put(key,value);
            	lastTime= time;
            	lastValue = value;
            }
        }
        long duration = System.nanoTime() - start;
        System.out.printf("Put/get %,d K operations per second single thread%n", (int) (INIT_COUNT * 2 * 1e6 / duration));

        System.out.println("start value:"+new String(engine.get(startKey, startKey, startTime)));

        ISeekIterator<InternalKey, byte[]> eIt = engine.iterator();

        eIt.seek(startKey, startKey, startTime);
  
        Iterator<Entry<String,String>> mIt = map.entrySet().iterator();
        int total=0;
        int miss = 0;
        int error = 0;
		while (mIt.hasNext()) {
			Entry<InternalKey, byte[]> entry = null;
			if(eIt.hasNext()){
				entry = eIt.next();
			}
			Entry<String, String> mEntry = mIt.next();
			

			String value = new String(entry.getValue());

			if (!value.equals(mEntry.getValue())) {
				error++;
				System.out.println("Error----------------------------------"+error+"-----------------------------------------------------" );
				System.out.println("engine value:"+eIt.column()+"-" +eIt.key().getTime()+ ":" + value+"|");
				System.out.println("cache  value:" + mEntry.getKey()+":"+mEntry.getValue()+"|");
				System.out.println("Error-----------------------------------"+total+"----------------------------------------------------" );
			} else {
				System.out.println("OK:"+eIt.key() + ":" + value);
			}
			total++;
		}
		
		miss = map.size() - total;
       
        System.out.println("total:"+total+"miss:"+miss+"error:"+error);
        System.out.println("last enging value:"+new String(engine.get(startKey, startKey, lastTime)));
        System.out.println("last cache  value:"+lastValue);
        System.out.println("map size:"+map.size());


    }
}
