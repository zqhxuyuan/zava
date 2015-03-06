package org.zbus.server;
 

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.zbus.common.Helper;
import org.zbus.common.logging.Logger;
import org.zbus.common.logging.LoggerFactory;
import org.zbus.common.protocol.MessageMode;
import org.zbus.common.protocol.Proto;
import org.zbus.common.remoting.Message;
import org.zbus.common.remoting.MessageHandler;
import org.zbus.common.remoting.RemotingServer;
import org.zbus.common.remoting.nio.Session;
import org.zbus.server.mq.MessageQueue;
import org.zbus.server.mq.ReplyHelper;
import org.zbus.server.mq.ReplyQueue;
import org.zbus.server.mq.store.MessageStore;
import org.zbus.server.mq.store.MessageStoreFactory;


public class ZbusServer extends RemotingServer {
	private static final Logger log = LoggerFactory.getLogger(ZbusServer.class);
	
	private ExecutorService mqExecutor = new ThreadPoolExecutor(4, 16, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	private final ConcurrentMap<String, MessageQueue> mqTable = new ConcurrentHashMap<String, MessageQueue>();  

	private long mqCleanDelay = 1000;
	private long mqCleanInterval = 3000;   
	protected final ScheduledExecutorService mqSessionCleanService = Executors.newSingleThreadScheduledExecutor();

	private MessageStore messageStore;
	private String messageStoreType = "dummy"; 
	
	private String adminToken = "";  
	
	private AdminHandler adminHandler;
	
	
	public ZbusServer(int serverPort) throws IOException {
		this("0.0.0.0", serverPort);
	}
	
	public ZbusServer(String serverHost, int serverPort) throws IOException {
		super(serverHost, serverPort);
		
		this.serverName = "ZbusServer";
    	this.adminHandler = new AdminHandler(mqTable, mqExecutor, serverAddr);
    	this.adminHandler.setAccessToken(this.adminToken); 
	}  
	 
	
	private MessageQueue findMQ(Message msg, Session sess) throws IOException{
		String mqName = msg.getMq();
		if(mqName == null){
			mqName = msg.getPath(); //support browser
		}
		MessageQueue mq = mqTable.get(mqName);
    	boolean ack = msg.isAck();
    	if(mq == null){
    		if(ack){
    			ReplyHelper.reply404(msg, sess);
    		}
    		return null;
    	} 
    	
    	if(!"".equals(mq.getAccessToken())){ 
    		if(!mq.getAccessToken().equals(msg.getToken())){ 
    			if(ack){
    				ReplyHelper.reply403(msg, sess);
    			}
    			return null;
    		}
    	} 
    	
    	return mq;
    	
	}
	
	public void init(){  
		
		this.registerGlobalHandler(new MessageHandler() { 
			@Override
			public void handleMessage(Message msg, Session sess) throws IOException {
				String mqReply = msg.getMqReply();
				if(mqReply == null ||  mqReply.equals("")){
					msg.setMqReply(sess.id()); //reply default to self
				}   
				if(msg.getMsgId() == null){ //msgid should be set
					msg.setMsgId(UUID.randomUUID().toString());
				}
				msg.setHead(Message.HEADER_REMOTE_ADDR, sess.getRemoteAddress());
				msg.setHead(Message.HEADER_BROKER, serverAddr);  
				if(!Message.HEARTBEAT.equals(msg.getCommand())){
					log.debug("%s", msg);
				}
			}
		}); 

		this.registerHandler(Proto.Produce, new MessageHandler() { 
			@Override
			public void handleMessage(Message msg, Session sess) throws IOException { 
				MessageQueue mq = findMQ(msg, sess);
				if(mq == null) return;
				mq.produce(msg, sess); 
			}
		});
		
		this.registerHandler(Proto.Consume, new MessageHandler() { 
			@Override
			public void handleMessage(Message msg, Session sess) throws IOException { 
				MessageQueue mq = findMQ(msg, sess);
				if(mq == null) return;
				mq.consume(msg, sess); 
			}
		});
		
		
		this.registerHandler(Proto.Request, new MessageHandler() { 
			@Override
			public void handleMessage(Message requestMsg, Session sess) throws IOException { 
				MessageQueue requestMq = findMQ(requestMsg, sess);
				if(requestMq == null) return;
				
				
				String replyMqName = requestMsg.getMqReply();  
				MessageQueue replyMq = mqTable.get(replyMqName);
				if(replyMq == null){
					int mode = MessageMode.intValue(MessageMode.MQ, MessageMode.Temp);
					replyMq = new ReplyQueue(serverAddr, replyMqName, mqExecutor, mode); 
					replyMq.setCreator(sess.getRemoteAddress());
					mqTable.putIfAbsent(replyMqName, replyMq);
				} 
				requestMsg.setAck(false);
				
				Message msgConsume = Message.copyWithoutBody(requestMsg);
				requestMq.produce(requestMsg, sess); 
				replyMq.consume(msgConsume, sess);
			}
		});
		
		this.registerHandler(Proto.Admin, adminHandler); 
	} 
	 
	public void setAdminToken(String adminToken) {
		this.adminToken = adminToken;
	} 
	
	@Override
	public void start() throws Exception { 
		super.start();
		//build message store
		this.messageStore = MessageStoreFactory.getMessageStore(this.serverAddr, this.messageStoreType);
		this.adminHandler.setMessageStore(this.messageStore); 
		{
			log.info("message store loading ....");
			this.mqTable.clear();
			
			ConcurrentMap<String, MessageQueue> mqs = this.messageStore.loadMqTable();
			Iterator<Entry<String, MessageQueue>> iter = mqs.entrySet().iterator();
			while(iter.hasNext()){
				MessageQueue mq = iter.next().getValue();
				mq.setExecutor(this.mqExecutor);
			}
			
			this.mqTable.putAll(mqs);
			log.info("message store loaded");
		}
		
		
		
		this.mqSessionCleanService.scheduleAtFixedRate(new Runnable() { 
			@Override
			public void run() {  
				Iterator<Entry<String, MessageQueue>> iter = mqTable.entrySet().iterator();
		    	while(iter.hasNext()){
		    		Entry<String, MessageQueue> e = iter.next();
		    		MessageQueue mq = e.getValue(); 
		    		mq.cleanSession();
		    	}
				
			}
		}, mqCleanDelay, mqCleanInterval, TimeUnit.MILLISECONDS);
	}
	
	public void close(){ 
		this.mqSessionCleanService.shutdown(); 
	}
	
	public void setupTrackServer(String trackServerAddr){
		this.adminHandler.setupTrackServer(trackServerAddr);
	}

	public void setMessageStoreType(String messageStoreType) {
		this.messageStoreType = messageStoreType;
	}
	
	 
    @Override
    public void onException(Throwable e, Session sess) throws IOException {
    	if(! (e instanceof IOException) ){
			super.onException(e, sess);
		}
    	this.cleanMQ(sess);
    }
    
    @Override
    public void onSessionDestroyed(Session sess) throws IOException {  
    	this.cleanMQ(sess);
    }
    
    
    @Override
    public String findHandlerKey(Message msg) {
    	String cmd = msg.getCommand();
    	if(cmd == null){ 
    		cmd = msg.getPath(); 
    	}
    	if(cmd == null || "".equals(cmd.trim())){  
    		cmd = Proto.Admin; 
    	}
    	return cmd;
    }
    
    private void cleanMQ(Session sess){
    	if(this.mqTable == null) return;
    	String creator = sess.getRemoteAddress();
    	Iterator<Entry<String, MessageQueue>> iter = this.mqTable.entrySet().iterator();
    	while(iter.hasNext()){
    		Entry<String, MessageQueue> e = iter.next();
    		MessageQueue mq = e.getValue();
    		if(MessageMode.isEnabled(mq.getMode(), MessageMode.Temp)){
    			if(mq.getCreator().equals(creator)){
        			iter.remove();
        		}
    		} 
    	}
    } 
    static boolean isAvailable(String classname) {
        try {
            return Class.forName(classname) != null;
        }
        catch(ClassNotFoundException cnfe) {
            return false;
        }
    }

    public static void main(String[] args) throws Exception{
		int serverPort = Helper.option(args, "-p", 15555); 
		String adminToken = Helper.option(args, "-admin", "");
		String trackServerAddr = Helper.option(args, "-track", "127.0.0.1:16666;127.0.0.1:16667");
		String storeType = Helper.option(args, "-store", "sql"); 
		String serviceBase = Helper.option(args, "-serviceBase", null); 
		
		ZbusServer zbus = new ZbusServer(serverPort);  
		zbus.setAdminToken(adminToken);
		zbus.setMessageStoreType(storeType);
		zbus.setupTrackServer(trackServerAddr); 
		zbus.start();
		
		if(serviceBase != null){
			try{
				Class<?> loaderClass = Class.forName("org.zbus.client.service.ServiceLoader");
				Method m = loaderClass.getMethod("load", String.class, String.class);
				String brokerAddress = String.format("127.0.0.1:%d", serverPort);
				m.invoke(null, serviceBase, brokerAddress); 
			} catch(Exception e){
				//ignore
				log.warn("loading service error, ignore");
			}
		}
	}  
}

