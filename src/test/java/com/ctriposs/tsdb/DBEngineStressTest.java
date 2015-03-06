package com.ctriposs.tsdb;

import java.io.IOException;
import java.util.Date;


public class DBEngineStressTest {

    private static final String TEST_DIR = "d://tsdb_test/stress/";

    private static DBEngine engine;

    public static void main(String[] args) throws IOException {


        DBConfig config = new DBConfig(TEST_DIR);
        engine = new DBEngine(config);
        

        System.out.println("Start from date " + new Date());
        long start = System.currentTimeMillis();
        for (long counter = 0;; counter++) {
        	
        	
        	engine.put("a", "b", System.currentTimeMillis(), "fdsafasdfasdfasdfsdafsdafasdfasdfasdfasfsda".getBytes());

         
   

            if (counter  % 1000000 == 0) {
                System.out.println("Current date:" + new Date());
                System.out.println("counter:     " + counter);
                System.out.println("store        " + engine.getStoreCounter(0));
                System.out.println("store error  " + engine.getStoreErrorCounter(0));


                System.out.println();

                System.out.println();
            }
   
        }
    }


}