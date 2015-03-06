package org.zbus.client;

public class ZbusException extends RuntimeException {  
	private static final long serialVersionUID = 6006204188240205218L;
    public ZbusException(String message){
    	super(message);
    }
	public ZbusException() {
		super(); 
	}
	public ZbusException(String message, Throwable cause) {
		super(message, cause); 
	}
	public ZbusException(Throwable cause) {
		super(cause); 
	}  
    
}
