package org.zbus.server;
 

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.zbus.common.Helper;
import org.zbus.common.json.JSON;
import org.zbus.common.logging.Logger;
import org.zbus.common.logging.LoggerFactory;
import org.zbus.common.protocol.BrokerInfo;
import org.zbus.common.protocol.Proto;
import org.zbus.common.protocol.TrackTable;
import org.zbus.common.remoting.Message;
import org.zbus.common.remoting.MessageHandler;
import org.zbus.common.remoting.RemotingClient;
import org.zbus.common.remoting.RemotingServer;
import org.zbus.common.remoting.nio.Session;
 
 
public class TrackServer extends RemotingServer {  
	private static final Logger log = LoggerFactory.getLogger(TrackServer.class); 
	private long publishInterval = 10000;
	private long probeInterval = 3000; 
	
	private final TrackTable trackTable = new TrackTable(); 
	
	private Map<String, Session> subscribers = new ConcurrentHashMap<String, Session>();
	private Map<String, RemotingClient> brokerProbes = new ConcurrentHashMap<String, RemotingClient>();
	
	private final ScheduledExecutorService trackPubService = Executors.newSingleThreadScheduledExecutor();
	private ExecutorService trackExecutor = new ThreadPoolExecutor(4,16, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	
	public TrackServer(int serverPort){
		this("0.0.0.0", serverPort);
	}
	
	public TrackServer(String serverHost, int serverPort) {
		super(serverHost, serverPort);
		
		this.serverName = "TrackServer";
		this.trackPubService.scheduleAtFixedRate(new Runnable() {	
			@Override
			public void run() {
				publishTrackTable();
			}
		}, 0, publishInterval, TimeUnit.MILLISECONDS);
		
		this.trackPubService.scheduleAtFixedRate(new Runnable() {	
			@Override
			public void run() {
				probeBrokers();
			}
		}, 0, probeInterval, TimeUnit.MILLISECONDS);
	}
	
	
	private void probeBrokers(){ 
		Iterator<Entry<String, RemotingClient>> iter = brokerProbes.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, RemotingClient> entry = iter.next();
			String brokerAddress = entry.getKey();
			RemotingClient client = entry.getValue();
			if(!client.hasConnected()){
				trackTable.removeBroker(brokerAddress); 
				iter.remove();
			}
		}
		log.info("Track: "+ trackTable); 
	}
	 
	
	private void publishTrackTable(){ 
		if(subscribers.size()<1) return;  
		
		String json = JSON.toJSONString(this.trackTable);
		Message msg = new Message();
		msg.setCommand(Proto.TrackPub);
		msg.setBody(json);
		
		Iterator<Entry<String, Session>> iter = subscribers.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, Session> entry = iter.next();
			Session sess = entry.getValue();
			if(!sess.isActive()){
				iter.remove();
				continue;
			}
			try {
				sess.write(msg);
			} catch (IOException e) {  
				iter.remove();
				//ignore
			}
		}
		
	}
	
	
	@Override
	public void init() { 	
		this.registerHandler(Proto.TrackReport, new MessageHandler() {  
			@Override
			public void handleMessage(Message msg, Session sess) throws IOException {  
				
				final BrokerInfo brokerInfo = JSON.parseObject(msg.getBodyString(), BrokerInfo.class);
				
				final String brokerAddress = brokerInfo.getBroker(); 
				if(!brokerProbes.containsKey(brokerAddress)){
					final RemotingClient client = new RemotingClient(brokerAddress);
					trackExecutor.submit(new Runnable() {
						@Override
						public void run() { 
							try {
								client.connectIfNeed();
								brokerProbes.put(brokerAddress, client);
							} catch (IOException e) {
								log.error(e.getMessage(), e);
							}
						}
					});
				}
				
				trackTable.addBroker(brokerAddress, brokerInfo);
				 
				publishTrackTable(); 
			}
		});
		
		this.registerHandler(Proto.TrackSub, new MessageHandler() { 
			@Override
			public void handleMessage(Message msg, Session sess) throws IOException {
				subscribers.put(sess.id(), sess);
				String json = JSON.toJSONString(trackTable);
				msg.setStatus("200");
				msg.setBody(json);
				sess.write(msg);
			}
		}); 
	}     
	
	
	
	public static void main(String[] args) throws Exception{
		int serverPort = Helper.option(args, "-p", 16666);
		TrackServer track = new TrackServer(serverPort); 
		track.start(); 
	} 
	
}
