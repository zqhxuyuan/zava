package org.zbus.common.remoting.nio;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.zbus.common.Helper;
import org.zbus.common.logging.Logger;
import org.zbus.common.logging.LoggerFactory;
import org.zbus.common.remoting.nio.Session.SessionStatus;

public class Dispatcher extends Thread {
	private static final Logger log = LoggerFactory.getLogger(Dispatcher.class);
	
	protected volatile Selector selector = null;
	protected final DispatcherManager dispatcherManager;
	private final Queue<Object[]> register = new LinkedBlockingQueue<Object[]>();
	private final Queue<Session> unregister = new LinkedBlockingQueue<Session>();
	
	public Dispatcher(DispatcherManager dispatcherManager, String name) throws IOException{
		super(name);
		this.dispatcherManager = dispatcherManager;
		this.selector = Selector.open();
	}
	
	public Dispatcher(DispatcherManager dispatcherManager) throws IOException{
		this(dispatcherManager, "Dispatcher");
	}
	
	
	public void registerChannel(SelectableChannel channel, int ops) throws IOException{
		registerChannel(channel, ops, null); 
	}
	
	public void registerSession(int ops, Session sess) throws IOException{
		registerChannel(sess.getChannel(), ops, sess);
	}
	
	public void registerChannel(SelectableChannel channel, int ops, Session sess) throws IOException{
		if(Thread.currentThread() == this){
			SelectionKey key = channel.register(this.selector, ops, sess);
			if(sess != null){
				sess.setRegisteredKey(key);
				sess.setStatus(SessionStatus.CONNECTED);
				sess.getEventAdaptor().onSessionRegistered(sess);
			} 
		} else { 
			this.register.offer(new Object[]{channel, ops, sess});
			this.selector.wakeup();
		}
	}
	
	public void unregisterSession(Session sess){
		if(this.unregister.contains(sess)){
			return;
		}
		this.unregister.add(sess);
		this.selector.wakeup();
	}
	
	
	@Override
	public void interrupt() { 
		super.interrupt();
		try {
			this.selector.close();
		} catch (IOException e) { 
			log.error(e.getMessage(), e);
		}
	}
	
	
	@Override
	public void run() { 
		try{
			while(true){
				selector.select(); 
				handleRegister();
				
				Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
				while(iter.hasNext()){
					SelectionKey key = iter.next();
					iter.remove();
					if(!key.isValid()) continue;
					
					Object att = key.attachment();
					if(att != null && att instanceof Session){
						((Session)att).updateLastOperationTime();
					}
					try{ 
						if(key.isAcceptable()){
							handleAcceptEvent(key);
						} else if (key.isConnectable()){
							handleConnectEvent(key);
						} else if (key.isReadable()){
							handleReadEvent(key);
						} else if (key.isWritable()){
							handleWriteEvent(key);
						}
					} catch(Throwable e){ 
						disconnectWithException(key, e); 
					}
				} 
				handleUnregister();
			}
		} catch(Throwable e) {
			log.error(e.getMessage(), e);
		}
	}
	
	private void disconnectWithException(final SelectionKey key, final Throwable e){  
		Session sess = (Session)key.attachment();
		try{ 
			sess.setStatus(SessionStatus.ON_ERROR);
			sess.getEventAdaptor().onException(e, sess);
		} catch (Throwable ex){
			log.error(e.getMessage(), ex);
		}
		try{ 
			if(sess != null){ 
				sess.close();
			} else {
				key.channel().close();  
			}  
			key.cancel();
		} catch(Throwable ex){
			log.error(e.getMessage(), ex);
		}
	}
	
	protected void handleRegister(){
		Object[] item = null;
		while( (item=this.register.poll()) != null){
			try{
				SelectableChannel channel = (SelectableChannel) item[0];
				if (!channel.isOpen() ) continue;
				int ops = (Integer)item[1];
				Session sess = (Session) item[2]; 
				
				SelectionKey key = channel.register(this.selector, ops, sess);
				if(sess != null){
					sess.setRegisteredKey(key);
					sess.getEventAdaptor().onSessionRegistered(sess);
				} 
				
			}catch(Exception e){
				log.error(e.getMessage(), e);
			}
		}
	}
	
	protected void handleUnregister(){
		Session sess = null;
		while( (sess = this.unregister.poll()) != null ){
			try {
				sess.close();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
	}
	
	
	protected void handleAcceptEvent(SelectionKey key) throws IOException{ 
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
		SocketChannel channel = server.accept();
		channel.configureBlocking(false); 
		if(log.isDebugEnabled()){
			log.debug("ACCEPT: %s=>%s", Helper.remoteAddress(channel), Helper.localAddress(channel));
		}
		Session sess = new Session(dispatcherManager, channel, dispatcherManager.buildEventAdaptor()); 
		sess.setStatus(SessionStatus.CONNECTED); //set connected 
		
		sess.getEventAdaptor().onSessionAccepted(sess);
		
	} 
	
	protected void handleConnectEvent(SelectionKey key) throws IOException{
		final SocketChannel channel = (SocketChannel) key.channel();
		if(log.isDebugEnabled()){
			log.debug("CONNECT: %s=>%s", Helper.localAddress(channel), Helper.remoteAddress(channel));
		}
		Session sess = (Session) key.attachment();
		if(sess == null){
			throw new IOException("Session not attached yet to SelectionKey");
		}  
		
		if(channel.finishConnect()){
			sess.finishConnect(); 
		}
		sess.setStatus(SessionStatus.CONNECTED);  
		key.interestOps(0); //!!!clear interest of OP_CONNECT to avoid looping CPU !!!
		sess.getEventAdaptor().onSessionConnected(sess);
	
	}
	
	protected void handleReadEvent(SelectionKey key) throws IOException{
		Session sess = (Session) key.attachment();
		if(sess == null){
			throw new IOException("Session not attached yet to SelectionKey");
		}
		final SocketChannel channel = sess.getChannel();
		if(log.isDebugEnabled()){
			log.debug("READ: %s=>%s", Helper.remoteAddress(channel), Helper.localAddress(channel));
		}
		sess.doRead();
	}
	
	protected void handleWriteEvent(SelectionKey key) throws IOException{
		Session sess = (Session) key.attachment();
		if(sess == null){
			throw new IOException("Session not attached yet to SelectionKey");
		}
		
		final SocketChannel channel = sess.getChannel();
		if(log.isDebugEnabled()){
			log.debug("WRITE: %s=>%s", Helper.remoteAddress(channel), Helper.localAddress(channel));
		}
		sess.doWrite();
	}

}
