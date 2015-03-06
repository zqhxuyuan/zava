package org.zbus.common.remoting.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;

 

public abstract class EventAdaptor {   
	/**
	 * 服务器端侦听到链接接入回调，此时Session尚未注册，默认注册该Session
	 * @param sess
	 * @throws IOException
	 */
	public void onSessionAccepted(Session sess) throws IOException { 
		sess.dispatcherManager().registerSession(SelectionKey.OP_READ, sess); 
	}
	/**
	 * Session注册到Engine成功后回调
	 * @param sess
	 * @throws IOException
	 */
	public void onSessionRegistered(Session sess) throws IOException {  
	
	} 
	/**
	 * 客户端链接成功后（Engine注册已经完成）回调
	 * @param sess
	 * @throws IOException
	 */
	public void onSessionConnected(Session sess) throws IOException{
		//默认关注读写事件
		sess.interestOps(SelectionKey.OP_READ|SelectionKey.OP_WRITE);
	}
	/**
	 * Session注销前回调
	 * @param sess
	 * @throws IOException
	 */
	public void onSessionDestroyed(Session sess) throws IOException{
		
	}
	/**
	 * Session接受到消息
	 * @param msg
	 * @param sess
	 * @throws IOException
	 */
	public abstract void onMessage(Object msg, Session sess) throws IOException; 
	/**
	 * Session各类错误发生时回调
	 * @param e
	 * @param sess
	 * @throws IOException
	 */
	public void onException(Throwable e, Session sess) throws IOException{
		if(e instanceof IOException){
			throw (IOException) e;
		} else if (e instanceof RuntimeException){
			throw (RuntimeException)e;
		} else {
			throw new RuntimeException(e.getMessage(), e); //rethrow by default
		}
	}
}
