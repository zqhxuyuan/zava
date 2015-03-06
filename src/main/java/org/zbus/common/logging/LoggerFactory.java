package org.zbus.common.logging;

import org.zbus.common.logging.impl.JDKLogger;
import org.zbus.common.logging.impl.Log4JLogger;
import org.zbus.common.logging.impl.Slf4JLogger;


public class LoggerFactory { 
	public static final boolean IS_SLF4J_AVAILABLE; 
    public static final boolean IS_LOG4J_AVAILABLE;  
    
    static {  
        IS_LOG4J_AVAILABLE = isAvailable("org.apache.log4j.Logger");
        IS_SLF4J_AVAILABLE = isAvailable("org.slf4j.Logger");
    }


    public static String loggerType() {  
    	if(IS_LOG4J_AVAILABLE) return "log4j"; 
    	if(IS_SLF4J_AVAILABLE) return "slf4j"; 
        return "jdk";
    }

    protected static boolean isAvailable(String classname) {
        try {
            return Class.forName(classname) != null;
        }
        catch(ClassNotFoundException cnfe) {
            return false;
        }
    }

    public static Logger getLogger(Class<?> clazz) {   
        if(IS_LOG4J_AVAILABLE)
        	return new Log4JLogger(clazz); 
        if(IS_SLF4J_AVAILABLE)
        	return new Slf4JLogger(clazz);

        return new JDKLogger(clazz);
    }

    public static Logger getLogger(String category) { 
        if(IS_LOG4J_AVAILABLE)
            return new Log4JLogger(category); 
        
        if(IS_SLF4J_AVAILABLE)
            return new Slf4JLogger(category);

        return new JDKLogger(category);
    }
}
