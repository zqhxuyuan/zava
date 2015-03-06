package com.ctriposs.tsdb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class DBEnginePutTest {

    private static final String TEST_DIR = "d:\\tsdb_test\\put_test";
    private static final int INIT_COUNT = 10*1000*1000;
    private static DBEngine engine;

    public static void main(String[] args) throws IOException  {


        DBConfig config = new DBConfig(TEST_DIR);
        engine = new DBEngine(config);
        
        String[] str = new String[]{"a","b","c","d","e","f","g"};
        Random random = new Random();
        long start = System.nanoTime();
        
        String data = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        
        Map<Long,String> map = new HashMap<Long,String>();
        
        for (int i = 0; i < 2*INIT_COUNT; i++) {
        	String n = String.valueOf(random.nextInt(30000));

        	long l = System.currentTimeMillis();
        	String d = data+i;
        	engine.put(n, n, l, d.getBytes());
        	map.put(l,n + "-" + d);
        }
        int total=0;
        int miss = 0;
        int error = 0;
        for(Entry<Long,String> entry:map.entrySet()){
        	String d[] = entry.getValue().split("-");
        	total++;
        	byte[] s = engine.get(d[0],d[0],entry.getKey());
        	if(s != null){
        		String dd = new String(s);
        		if(d[1].equals(dd)){
        			System.out.println("OK");
        		}else{
        			System.out.println(++error+"error ");
        			System.out.println("实际值："+d[1]);
        			System.out.println("存储值："+dd);
        		}
        	}else{
        		System.out.println(++miss+"not found "+entry.getValue()+"-"+entry.getKey());
        	}
        }

        System.out.println("total:"+total+"miss:"+miss+"error:"+error);
        long duration = System.nanoTime() - start;
        System.out.printf("Put/get %,d K operations per second single thread%n",
                (int) (INIT_COUNT * 2 * 1e6 / duration));
		
	}
}
