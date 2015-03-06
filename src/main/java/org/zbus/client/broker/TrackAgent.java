package org.zbus.client.broker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.zbus.common.json.JSON;
import org.zbus.common.logging.Logger;
import org.zbus.common.logging.LoggerFactory;
import org.zbus.common.protocol.Proto;
import org.zbus.common.protocol.TrackTable;
import org.zbus.common.remoting.ClientDispatcherManager;
import org.zbus.common.remoting.Message;
import org.zbus.common.remoting.RemotingClient;
import org.zbus.common.remoting.callback.ErrorCallback;
import org.zbus.common.remoting.callback.MessageCallback;
import org.zbus.common.remoting.nio.Session;
import org.zbus.common.remoting.ticket.ResultCallback;

interface TrackListener{
	void onTrackTableUpdated(TrackTable trackTable);
}

public class TrackAgent {
	private static final Logger log = LoggerFactory.getLogger(TrackAgent.class);
	private String trackServerList="127.0.0.1:16666"; 
	private final List<RemotingClient> clients = new ArrayList<RemotingClient>();
	private ClientDispatcherManager clientMgr;  
	private CountDownLatch tableReady = new CountDownLatch(1);
	private ExecutorService executor = new ThreadPoolExecutor(4, 16, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	private List<TrackListener> trackListeners = new ArrayList<TrackListener>();
	
	public TrackAgent(String trackServerList) throws IOException{
		this(trackServerList, null);
	} 
	
	public TrackAgent(String trackServerList, ClientDispatcherManager clientMgr) throws IOException {  	
		this.clientMgr = clientMgr;  
		this.connectToTrackServers();
		
	} 
	
	public void waitForReady(long timeout){
		try {
			this.tableReady.await(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {  
		} 
	}
	
	public void addTrackListener(TrackListener listener){
		this.trackListeners.add(listener);
	}
	
	public void removeTrackListener(TrackListener listener){
		this.trackListeners.remove(listener);
	}
	
	public void printSessions(){
		for(RemotingClient client : this.clients){
			log.info("client session: "+client.getSession());
		}
	}
	
	private void connectToTrackServers(){
		String[] serverAddrs = this.trackServerList.split("[;]");
		for(String addr : serverAddrs){
			addr = addr.trim();
			if( addr.isEmpty() ) continue;
			
			final RemotingClient client = new RemotingClient(addr, this.clientMgr); 
			clients.add(client);
			
			executor.submit(new Runnable() { 
				@Override
				public void run() { 
					try {
						initTrackClient(client);
					} catch (IOException e) {  
						//log.error(e.getMessage(), e);
					} 
				}
			});
			
		}  
	}
	
	
	private void initTrackClient(final RemotingClient client) throws IOException{  
		client.onMessage(new MessageCallback() { 
			@Override
			public void onMessage(Message msg, Session sess) throws IOException { 
				final TrackTable trackTable = JSON.parseObject(msg.getBody(), TrackTable.class);
				for(TrackListener listener : trackListeners){
					listener.onTrackTableUpdated(trackTable);
				}
				tableReady.countDown();
			}
		});
		
		client.onError(new ErrorCallback() { 
			@Override
			public void onError(IOException e, Session sess) throws IOException {
				executor.submit(new Runnable() { 
					@Override
					public void run() { 
						doTrackSub(client);
					}
				});
				
			}
		}); 
		doTrackSub(client); 
		 
	}
	
	private void doTrackSub(final RemotingClient client){
		try {  
			Message msg = new Message();
			msg.setCommand(Proto.TrackSub); 
			client.invokeAsync(msg, new ResultCallback() { 
				@Override
				public void onCompleted(Message result) {
					final TrackTable trackTable = JSON.parseObject(result.getBody(), TrackTable.class);
					for(TrackListener listener : trackListeners){
						listener.onTrackTableUpdated(trackTable);
					}
					tableReady.countDown();
				}  
			}); 
		} catch (IOException e) { 
			log.debug(e.getMessage(), e);;
		}
	}
	
}
