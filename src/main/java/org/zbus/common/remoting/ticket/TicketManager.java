package org.zbus.common.remoting.ticket;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.zbus.common.remoting.Message;
 
public class TicketManager { 
	
	private static ConcurrentMap<String, Ticket> tickets = new ConcurrentHashMap<String, Ticket>();
	private static ScheduledExecutorService ticketThread = Executors.newSingleThreadScheduledExecutor();
	static void norun() {
		
		ticketThread.scheduleAtFixedRate(
			new Runnable() {
				long currentTime = -1;

				public void run() {
					Collection<Ticket> values = tickets.values();
					currentTime = System.currentTimeMillis();
					for (Ticket t : values) {
						if ((currentTime - t.startTime) > t.timeout) {
							removeTicket(t.getId());
							t.expired();
						}
					}
				}

			}, 500, 500, TimeUnit.MILLISECONDS);
	}
	
	public static void shutdown(){
		ticketThread.shutdown();
	}

 
	public static Ticket getTicket(String id) {
		if(id == null) return null;
		return tickets.get(id);
	}
 
	public static Ticket createTicket(Message req, long timeout) {
		return createTicket(req, timeout, null);
	}
 
	public static Ticket createTicket(Message req, long timeout, ResultCallback callback) {
		Ticket ticket = new Ticket(req, timeout);
		ticket.setCallback(callback);

		if (tickets.putIfAbsent(ticket.getId(), ticket) != null) {
			throw new IllegalArgumentException("duplicate ticket number.");
		}

		return ticket;
	} 
	
	public static void removeTicket(String id) {
		tickets.remove(id);
	}
	
}
