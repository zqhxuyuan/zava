package org.zbus.common.remoting.ticket;

public class TicketExpiredException extends RuntimeException {  
	private static final long serialVersionUID = 6006204188240205218L;
    public TicketExpiredException(String message){
    	super(message);
    }
	public TicketExpiredException() {
		super(); 
	}
	public TicketExpiredException(String message, Throwable cause) {
		super(message, cause); 
	}
	public TicketExpiredException(Throwable cause) {
		super(cause); 
	}  
    
}
