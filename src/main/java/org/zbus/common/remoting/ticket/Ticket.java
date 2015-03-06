package org.zbus.common.remoting.ticket;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.zbus.common.remoting.Message;
  
 
public class Ticket {   
	private static TicketListener ticketListener = null; 
	private CountDownLatch latch = new CountDownLatch(1);
	private String id = "";
	private Message response = null; 
	private Message request = null; 
	private ResultCallback callback = null; 
	
	private volatile boolean expired = false; 
	protected long timeout = 1000; 
	public long startTime = System.currentTimeMillis(); 
	
	
	public Ticket(Message request, long timeout) {   
		this.id = uuidTicket(); 
		if(request != null){
			request.setMsgId(this.id);
		}
		
		this.request = request; 
		this.timeout = timeout;
	} 
	public static String uuidTicket(){
		return UUID.randomUUID().toString(); 
	}
 
	public boolean await(long timeout, TimeUnit unit)
			throws InterruptedException {
		boolean status = this.latch.await(timeout, unit);
		checkExpired();
		return status;
	}
 
	public void await() throws InterruptedException {
		this.latch.await();
		checkExpired();
	}
 
	public void expired() {
		this.expired = true; 
		this.countDown();
		if (ticketListener != null)
			ticketListener.onResponseExpired(this);
	}
 
	private void countDown() {
		this.latch.countDown();
	}
 
	public boolean isDone() {
		return this.latch.getCount() == 0;
	}
 
	public void notifyResponse(Message response) {
		this.response = response;
		if (this.callback != null)
			this.callback.onCompleted(response);
		if (ticketListener != null)
			ticketListener.onResponseReceived(this);
		this.countDown();
	} 
 
	public ResultCallback getCallback() {
		return callback;
	}
 
	public void setCallback(ResultCallback callback) {
		this.callback = callback;
	} 
	 
	public String getId() {
		return id;
	}

	public Message request() {
		return this.request;
	}
	public Message response() {
		return this.response;
	}
	
	protected void checkExpired() {
		if (this.expired)
			throw new TicketExpiredException("Ticket expired");
	}

	public static void setTicketListener(TicketListener listener) {
		ticketListener = listener;
	} 
}
