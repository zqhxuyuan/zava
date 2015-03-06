package com.ctriposs.bigcache.sample;

import java.io.IOException;

import com.ctriposs.bigcache.*;
import com.ctriposs.bigcache.CacheConfig.StorageMode;

/**
 * Hello BigCache!
 *
 */
public class HelloBigCache 
{
	// directory to store bigcache data files.
	static String cacheDir = "d:/sample/hello";
	
    public static void main( String[] args )
    {
        
    	ICache<String> cache = null;
    	
        try {
        	
        	// new BigCache with provided cache directory
        	CacheConfig config = new CacheConfig();
        	config.setStorageMode(StorageMode.OffHeapPlusFile); // use offheap memory + file mode
            cache = new BigCache<String>(cacheDir, config);
        	
	        // put key/value into the cache
	        cache.put("helloKey", "helloValue".getBytes());
	        // get value from the cache by key
	        byte[] valueBytes = cache.get("helloKey");
	        
	        System.out.println("value for helloKey is " + new String(valueBytes));
	        
	        // delete key/value from cache by key
	        cache.delete("helloKey");
	        
	        // get non-exiting or already deleted key/value will get null value
	        valueBytes = cache.get("helloKey");
	        if (valueBytes == null) {
	        	System.out.println("helloKey has been deleted");
	        }
	        
	        
	        // put more key/value pairs
	        for(int i = 0; i < 1024; i++) {
	        	cache.put("key" + i, ("value" + i).getBytes());
	        }
	        
	        // get more key/value pairs
	        for(int i = 0; i < 1024; i++) {
	        	valueBytes = cache.get("key" + i);
	        	System.out.println(new String(valueBytes));
	        }
	        
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            // make sure you close the cache to avoid possible resource leaking.
            try {
    			cache.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
        }
    }
}
