package com.ctriposs.sdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ctriposs.sdb.utils.TestUtil;

public class SDBUnitTest {
	
    private static String testDir = TestUtil.TEST_BASE_DIR + "sdb_unit_test";
    private static SDB db;
    
    @Test
    public void oneMillionPutGet(){
        Put_Get("oneMillionPutGet", 1000000, true, 8, 128);
    }

    @Test
    public void twoMillionPutGet(){
        Put_Get("twoMillionPutGet", 2000000, true, 8, 1024);
    }
	
    private void Put_Get(String testName,int count, boolean reopen, int keyLen,int valueLen) {
        System.out.print(testName + " test on \n");
        db = new SDB(testDir);
        String key = "";
        String value = "";
        if(keyLen > 0) {
            key = TestUtil.randomString(keyLen);
        } else {
            key = TestUtil.randomString(8);
        }
        if(valueLen > 0) {
            value = TestUtil.randomString(valueLen);
        }else{
            value = TestUtil.randomString(32);
        }
        List<String> keys = new ArrayList<String>();

        // put
        for(int i = 0; i < count; i++) {
            db.put(TestUtil.getBytes(key + i), TestUtil.getBytes(value + i));
            keys.add(key + i);
        }
        
        // stats
        TestUtil.getSDBStats(db.getStats());
        
        // close
        try {
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // reopen
        db = new SDB(testDir);
            
        // get & assert
        for (String k : keys){
            byte[] vBytes = db.get(TestUtil.getBytes(k));
            String getValue = new String(vBytes);
            String index = k.substring(keyLen > 0 ? keyLen : 8);
            assertEquals(value+index, getValue);
        }
        
        // delete
        for(String k : keys){
            db.delete(TestUtil.getBytes(k));
        }
        
        // get & assert
        for (String k : keys){
            byte[] vBytes = db.get(TestUtil.getBytes(k));
            assertNull(vBytes);
        }

        // stats
        TestUtil.getSDBStats(db.getStats());
        
        // close & destory
        try {
            db.close();
            db.destory();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

}
