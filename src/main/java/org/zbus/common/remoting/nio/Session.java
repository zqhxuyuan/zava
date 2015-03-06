package org.zbus.common.remoting.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.zbus.common.Helper;
import org.zbus.common.logging.Logger;
import org.zbus.common.logging.LoggerFactory;

public class Session {
	public static enum SessionStatus {
		NEW, CONNECTED, ON_ERROR, CLOSED
	}
	private static final Logger log = LoggerFactory.getLogger(Session.class); 
	
	private SessionStatus status = SessionStatus.NEW;
	private long lastOperationTime = System.currentTimeMillis();
	private final String id; 
	
	private int bufferSize = 1024*8;
	private IoBuffer readBuffer = null;
	private Queue<ByteBuffer> writeBufferQ = new LinkedBlockingQueue<ByteBuffer>();
	
	private CountDownLatch connectLatch = new CountDownLatch(1);
	
	private final DispatcherManager dispatcherManager;
	private final SocketChannel channel;
	private SelectionKey registeredKey = null;
	
	private ConcurrentMap<String, Object> attributes = null;

	private Object attachment;
	private final EventAdaptor eventAdaptor;
	
	public Session(DispatcherManager dispatcherManager, SocketChannel channel, EventAdaptor eventAdaptor){
		this(dispatcherManager, channel, null, eventAdaptor); 
	}
	
	public Session(DispatcherManager dispatcherManager, SocketChannel channel, Object attachment, EventAdaptor eventAdaptor){
		this.dispatcherManager = dispatcherManager;
		this.id = UUID.randomUUID().toString();
		this.channel = channel; 
		this.attachment = attachment;
		this.eventAdaptor = eventAdaptor;
	}
	
	
	
	public String id(){
		return ""+this.id;
	} 
	
	public void close() throws IOException {
		if(this.status == SessionStatus.CLOSED){
			return;
		}
		this.status = SessionStatus.CLOSED;
		if(this.channel != null){
			this.channel.close();  
		}
		
		if(this.registeredKey != null){
			this.registeredKey.cancel();
			this.registeredKey = null;
		} 
	}
	
	public void asyncClose() throws IOException{ 
		if(this.registeredKey == null){
			return;
		} 
		Dispatcher dispatcher = dispatcherManager.getDispatcher(this.registeredKey);
		if(dispatcher == null){
			throw new IOException("failed to find dispatcher for session: "+this);
		}
		
		dispatcher.unregisterSession(this);
	}
	
	public void write(Object msg) throws IOException{
		write(dispatcherManager.getCodec().encode(msg));
	}
	
	public void write(IoBuffer buf) throws IOException{
		if(this.registeredKey == null){
			throw new IOException("Session not registered yet:"+this);
		}
		
		if(!writeBufferQ.offer(buf.buf())){
			String msg = "Session write buffer queue is full, message count="+writeBufferQ.size();
			log.warn(msg);
			throw new IOException(msg);
		}
		
		registeredKey.interestOps(registeredKey.interestOps() | SelectionKey.OP_WRITE);
		registeredKey.selector().wakeup(); //TODO
	}
	
	
	
	public void doRead() throws IOException { 
		if(readBuffer == null){
			readBuffer = IoBuffer.allocate(bufferSize);
		}
		ByteBuffer data = ByteBuffer.allocate(1024*4);
		
		int n = 0;
		while((n = channel.read(data)) > 0){
			data.flip();
			readBuffer.put(data.array(), data.position(), data.remaining());
			data.clear();
		}
		
		if(n < 0){
			eventAdaptor.onSessionDestroyed(this);
			asyncClose();
			return;
		} 
		
		IoBuffer tempBuf = readBuffer.duplicate().flip();
		Object msg = null;
		while(true){
			tempBuf.mark();
			if(tempBuf.remaining()>0){
				msg = dispatcherManager.getCodec().decode(tempBuf);
			} else {
				msg = null;
			}
			if(msg == null){ 
				tempBuf.reset();
				readBuffer = resetIoBuffer(tempBuf);
				break;
			}
			
			final Object theMsg = msg;  
			dispatcherManager.getExecutor().execute(new Runnable() { 
				@Override
				public void run() { 
					try{
						eventAdaptor.onMessage(theMsg, Session.this);
					} catch(Throwable e){ 
						//log.error(e.getMessage(), e);
						try {
							eventAdaptor.onException(e, Session.this);
						} catch (IOException e1) { 
							try {
								close();
							} catch (Throwable e2) {
								log.error(e2.getMessage(), e2);
							}
						}  
					}
				}
			});  
			
		}  
		
	}
	protected IoBuffer resetIoBuffer(IoBuffer buffer) {
		IoBuffer newBuffer = null;

		if (buffer != null && buffer.remaining() > 0) {
			int len = buffer.remaining();
			byte[] bb = new byte[len];
			buffer.get(bb);
			newBuffer = IoBuffer.wrap(bb);
			newBuffer.position(len);
		}

		return newBuffer;
	}
	
	protected int doWrite() throws IOException{ 
		int n = 0;
		synchronized (writeBufferQ) {
			while(true){
				ByteBuffer buf = writeBufferQ.peek();
				if(buf == null){
					registeredKey.interestOps(SelectionKey.OP_READ);
					//registeredKey.selector().wakeup(); //TODO
					break;
				}
				
				int wbytes = this.channel.write(buf);
				
				if(wbytes == 0 && buf.remaining() > 0){
					//registeredKey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
					//registeredKey.selector().wakeup(); //TODO
					break;
				}
				
				n += wbytes;
				if(buf.remaining() == 0){
					writeBufferQ.remove();
					continue;
				} else {
					break;
				}
			} 
		}
		return n;
	}
	
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) { 
		if(obj instanceof Session){
			Session other = (Session)obj;
			return (this.hashCode() == other.hashCode());
		}
		return false;
	}

	public long getLastOperationTime() {
		return lastOperationTime;
	}

	public void updateLastOperationTime() {
		this.lastOperationTime = System.currentTimeMillis();
	}  
	
	public String getRemoteAddress() {
		if (this.status != SessionStatus.CLOSED) { 
			InetAddress addr = this.channel.socket().getInetAddress();
			return String.format("%s:%d", addr.getHostAddress(),channel.socket().getPort());
		} 
		return null;
	}
	
	public String getLocalAddress() {
		if (this.status != SessionStatus.CLOSED) { 
			return Helper.localAddress(this.channel);
		} 
		return null;
	}


	public int interestOps() throws IOException{
		if(this.registeredKey == null){
			throw new IOException("Session not registered yet:"+this);
		}
		return this.registeredKey.interestOps();
	}
	
	
	public void register(int interestOps) throws IOException{
		dispatcherManager.registerSession(interestOps, this);
	}
	
	public void interestOps(int ops){
		if(this.registeredKey == null){
			throw new IllegalStateException("registered session required");
		}
		this.registeredKey.interestOps(ops); 
	}
	
	public void interestOpsAndWakeup(int ops){
		interestOps(ops);
		this.registeredKey.selector().wakeup();
	}
	

	public SelectionKey getRegisteredKey() {
		return registeredKey;
	}
	public void setRegisteredKey(SelectionKey key) {
		this.registeredKey = key;
	}
	public SessionStatus getStatus() {
		return status;
	}
	
	public boolean isActive(){
		return this.status == SessionStatus.CONNECTED;
	}
	public boolean isNew(){
		return this.status == SessionStatus.NEW;
	}

	public void setStatus(SessionStatus status) {
		this.status = status;
	}

	public SocketChannel getChannel() {
		return channel;
	} 
	

	public DispatcherManager dispatcherManager() {
		return dispatcherManager;
	}

	public void finishConnect(){
		this.connectLatch.countDown();
	}
	
	
	public boolean waitToConnect(long millis){
		try { 
			return this.connectLatch.await(millis, TimeUnit.MILLISECONDS); 
		}catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
		return false;
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

	public String toString() {
		return "Session ["
	            +"id=" + id 
				+ ", status=" + status 
				+ ", registeredKey=" + registeredKey 
				+ ", attributes="+ attributes 
				+ ", lastOperationTime=" + lastOperationTime 
				+ ", readBuffer=" + readBuffer 
				+ ", writeBufferQ=" + writeBufferQ
				+ ", connectLatch=" + connectLatch 
				+ ", channel=" + channel 
				+ "]";
	}



	public Object getAttachment() {
		return attachment;
	}

	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

	public EventAdaptor getEventAdaptor() {
		return eventAdaptor;
	}
	
}
