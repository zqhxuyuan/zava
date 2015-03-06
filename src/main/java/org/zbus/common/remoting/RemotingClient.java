package org.zbus.common.remoting;
 

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.zbus.common.logging.Logger;
import org.zbus.common.logging.LoggerFactory;
import org.zbus.common.remoting.callback.ConnectedCallback;
import org.zbus.common.remoting.callback.ErrorCallback;
import org.zbus.common.remoting.callback.MessageCallback;
import org.zbus.common.remoting.nio.Session;
import org.zbus.common.remoting.ticket.ResultCallback;
import org.zbus.common.remoting.ticket.Ticket;
import org.zbus.common.remoting.ticket.TicketManager;
 
public class RemotingClient { 
	private static final Logger log = LoggerFactory.getLogger(RemotingClient.class);     
	
	protected final ClientDispatcherManager dispatcherManager;   
	private static volatile ClientDispatcherManager defaultDispactherManager = null; 
	
	static ClientDispatcherManager getDefaultDispatcherManager(){
		if(defaultDispactherManager == null){
			synchronized (RemotingClient.class) {
				if(defaultDispactherManager == null){
					try {
						defaultDispactherManager = new ClientDispatcherManager();
						defaultDispactherManager.start();
					} catch (IOException e) { 
						//ignore
						log.error(e.getMessage(), e);
					}
				}
			}
		}
		return defaultDispactherManager;
	} 
	
	protected String serverHost = "127.0.0.1";
	protected int serverPort = 15555;   
    protected Session session;
	protected int readTimeout = 3000;
	protected int connectTimeout = 3000;
	private ConcurrentMap<String, Object> attributes = null;
	
	protected MessageCallback messageCallback;
	protected ConnectedCallback connectedCallback;
	protected ErrorCallback errorCallback; 

	protected final ScheduledExecutorService heartbeator = Executors.newSingleThreadScheduledExecutor();
	
	public RemotingClient(String serverHost, int serverPort){
		this(serverHost, serverPort, getDefaultDispatcherManager());
	}
	public RemotingClient(String serverHost, int serverPort, ClientDispatcherManager dispatcherManager) { 
    	this(String.format("%s:%d", serverHost, serverPort), dispatcherManager);
    }  
	
	public RemotingClient(String address){
		this(address, getDefaultDispatcherManager());
	} 
	
	public RemotingClient(String address, ClientDispatcherManager dispatcherManager){
		if(dispatcherManager == null){
			dispatcherManager = getDefaultDispatcherManager();
		}
		String[] blocks = address.split("[:]");
		if(blocks.length > 2){
			throw new IllegalArgumentException("Illegal address: "+address);
		}
		if(!dispatcherManager.isStarted()){
			throw new IllegalStateException("ClientDispachterManager not started yet");
		}
		
		this.serverHost = blocks[0].trim();
		if(blocks.length > 1){
			this.serverPort = Integer.valueOf(blocks[1].trim());
		} 
		
		this.dispatcherManager = dispatcherManager;
		
		this.heartbeator.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if(RemotingClient.this.hasConnected()){
					Message hbt = new Message();
					hbt.setCommand(Message.HEARTBEAT);
					try {
						RemotingClient.this.send(hbt);
					} catch (IOException e) {  
						//ignore
					}
				}
			}
		}, 1000, 10000, TimeUnit.MILLISECONDS);

	}
	
    
    
    protected void initCallback() throws IOException {
    	if(this.connectedCallback != null){
    		onConnected(this.connectedCallback); 
    	}
    	if(this.errorCallback != null){
    		onError(this.errorCallback);
    	}
    	if(this.messageCallback != null){
    		onMessage(this.messageCallback);
    	} 
    }
    
    protected Session doConnect() throws IOException { 
    	if(this.session != null ){
    		if (this.session.isActive() || this.session.isNew()){  
    			return this.session; 
    		}
    	} 
    	
    	SocketChannel channel = SocketChannel.open();
    	channel.configureBlocking(false);
    	channel.connect(new InetSocketAddress(this.serverHost, this.serverPort)); 
    	
    	this.session = new Session(dispatcherManager, channel, dispatcherManager.buildEventAdaptor()); 
    	
    	initCallback();
    	
    	dispatcherManager.registerSession(SelectionKey.OP_CONNECT, this.session);
    	
    	return this.session;
    }
    
    
    public void connect(int timeoutMillis) throws IOException{  
    	this.session = doConnect(); 
    	this.session.waitToConnect(timeoutMillis);
    }
    
    public boolean hasConnected(){
    	return session != null && session.isActive();
    }
  
    
    public void ensureConnected(){ 
		while(!this.hasConnected()){
			try {
				this.connect(connectTimeout);
			} catch (IOException e) {
				log.info(e.getMessage(), e);
			}
		}
	}  
    
    public void connectIfNeed() throws IOException{
    	if(!this.hasConnected()){
    		//同步进行连接操作
    		this.connect(this.connectTimeout);
    	}
    	
    	if(!this.hasConnected()){
    		throw new IOException("Connection failed");
    	} 
    }
        
   
    public void invokeAsync(Message req, ResultCallback callback) throws IOException { 
    	connectIfNeed();
    	
		Ticket ticket = null;
		if(callback != null){
			ticket = TicketManager.createTicket(req, readTimeout, callback);
		} else {
			if("".equals(req.getMsgId()) || req.getMsgId() == null){//没有设置消息ID则自动生成
				req.setMsgId(Ticket.uuidTicket());
			}
		} 
		try{
			session.write(req);  
		} catch(IOException e) {
			if(ticket != null){
				TicketManager.removeTicket(ticket.getId());
			}
			throw e;
		}  
	} 
    public Message invokeSync(Message req) throws IOException {
    	return this.invokeSync(req, this.readTimeout);
    }
    public Message invokeSync(Message req, int timeout) throws IOException {
    	Ticket ticket = null; 
		try {
			connectIfNeed();
			ticket = TicketManager.createTicket(req, timeout); 
			session.write(req);
			
			if(!ticket.await(timeout, TimeUnit.MILLISECONDS)){
				if(!session.isActive()){
					throw new IOException("Connection reset by peer");
				} else {
					return null;
				}
			}
		    return ticket.response(); 
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		} finally{
			if(ticket != null){
				TicketManager.removeTicket(ticket.getId());
			}
		}  
		return null;
    } 
     
     
    public void close(){
    	if(this.session != null){
    		try {
				this.session.close();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
    	}
    	this.heartbeator.shutdown();
    }  
    
    
	public String getServerHost() {
		return serverHost;
	}

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public Session getSession() {
		return session;
	}  
	 
	@SuppressWarnings("unchecked")
	public <T> T attr(String key){
		if(this.attributes == null){
			return null;
		}
		
		return (T)this.attributes.get(key);
	}
	
	public <T> void attr(String key, T value){
		if(this.attributes == null){
			synchronized (this) {
				if(this.attributes == null){
					this.attributes = new ConcurrentHashMap<String, Object>();
				}
			} 
		}
		this.attributes.put(key, value);
	}

	
	 /**
     * asynchronous send message, return message fall into client's callback 
     * 异步发送消息，消息没有Ticket匹配，由Client的消息回调处理
     * @param msg
     * @throws IOException
     */
    public void send(Message msg) throws IOException{
    	connectIfNeed();
    	//没有设置消息ID则自动生成
    	if("".equals(msg.getMsgId()) || msg.getMsgId() == null){
			msg.setMsgId(Ticket.uuidTicket());
		}
    	
    	this.session.write(msg);
    } 
    
	public void onMessage(MessageCallback messageCallback){
    	this.messageCallback = messageCallback;
    	if(this.session != null && this.messageCallback != null){
    		ClientEventAdaptor handler = (ClientEventAdaptor) this.session.getEventAdaptor(); 
        	handler.setMessageCallback(this.messageCallback);
    	} 
    }
    
    public void onError(ErrorCallback errorCallback){
    	this.errorCallback = errorCallback;
    	if(this.session != null && this.errorCallback != null){
    		ClientEventAdaptor handler = (ClientEventAdaptor) this.session.getEventAdaptor(); 
        	handler.setErrorCallback(errorCallback);
    	} 
    } 
    
    public void onConnected(ConnectedCallback connectedCallback){
    	this.connectedCallback = connectedCallback;
    	if(this.session != null && connectedCallback != null){
    		ClientEventAdaptor handler = (ClientEventAdaptor) this.session.getEventAdaptor(); 
        	handler.setConnectedCallback(connectedCallback);
    	} 
    }
    
}
