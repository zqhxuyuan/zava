package com.ctriposs.tsdb;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class DBEngineSeekTest {

    private static final String TEST_DIR = "d:\\tsdb_test\\seek_test";
    private static final int INIT_COUNT = 10*1000*1000;
    private static DBEngine engine;

    public static void main(String[] args) throws IOException {


        DBConfig config = new DBConfig(TEST_DIR);
        engine = new DBEngine(config);

        String[] str = new String[]{"a","b","c","d","e","f","g"};
        Random random = new Random();
        long start = System.nanoTime();

        String data = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        Map<Long,String> map = new LinkedHashMap<Long,String>();

        long s = 0;
        String table = null;
        String last = null;
        long lt = 0;
        for (int i = 0; i < 2 * INIT_COUNT; i++) {
            int n = random.nextInt(7);
            
            long l = System.currentTimeMillis();
            if(i==0){
            	s = l;
            	table = str[n];
            }

            String d = data+i;
            engine.put(str[n], str[n], l, d.getBytes());
            map.put(l,str[n] + "-" + d);
            if(table.equals(str[n])){
            	last = d;
            	lt = l;
            }
        }
        
        System.out.println(new String(engine.get(table, table, s)));

        ISeekIterator<InternalKey, byte[]> iterator = engine.iterator();

        iterator.seek(table, table, s);
        while(iterator.hasNext()){
        	iterator.next();
        	System.out.println(iterator.time()+":"+iterator.table()+":"+new String(iterator.value()));
        }
        
        System.out.println(lt+":"+table+":last:"+last);
        long duration = System.nanoTime() - start;
        System.out.printf("Put/get %,d K operations per second single thread%n",
                (int) (INIT_COUNT * 2 * 1e6 / duration));

    }
}
