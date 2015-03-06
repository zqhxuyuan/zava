package org.zbus.server.mq.store;

import org.zbus.common.logging.Logger;
import org.zbus.common.logging.LoggerFactory;

 

public class MessageStoreFactory {  
	private static final Logger log = LoggerFactory.getLogger(MessageStoreFactory.class);
	
	public static final String SQL   = "sql"; 
	public static final String REDIS = "redis";  
	public static final String DUMMY = "dummy";  
	
    public static MessageStore getMessageStore(String borker, String type) {   
    	try{
	    	if(REDIS.equals(type)){ 
				log.info("Using Redis store");
				MessageStore store = new MessageStoreRedis(borker);
				store.start();
				return store;
	        } 
	        
	        if(SQL.equals(type)){ 
				log.info("Using SQL store");
				MessageStore store = new MessageStoreSql(borker);
				store.start();
				return store;
	        } 
	        
    	} catch (Exception e){
    		log.error(e.getMessage(), e);
    		log.warn("default to dummy store");
    	}
    	
        return new MessageStoreDummy();
    }
}
