package org.zbus.server.mq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

import org.zbus.common.logging.Logger;
import org.zbus.common.logging.LoggerFactory;
import org.zbus.common.protocol.ConsumerInfo;
import org.zbus.common.remoting.Message;
import org.zbus.common.remoting.nio.Session;

public class ReplyQueue extends MessageQueue {    
	private static final long serialVersionUID = -2343230968503191635L;

	private static final Logger log = LoggerFactory.getLogger(ReplyQueue.class); 
	
	protected final ConcurrentMap<String, Message> msgQ = new ConcurrentHashMap<String, Message>();
	transient PullSession pullSession = null;
	
	public ReplyQueue(String broker, String name, ExecutorService executor, int mode){
		super(broker, name, executor, mode); 
	}  
	
	public void produce(Message msg, Session sess) throws IOException{
		String msgId = msg.getMsgId(); 
		if(msg.isAck()){
			ReplyHelper.reply200(msgId, sess);
		} 
		msgQ.put(msgId, msg);
    	this.dispatch();
	}
	
	public void consume(Message msg, Session sess) throws IOException{ 
		if( this.pullSession == null){
			this.pullSession = new PullSession(sess, msg);
		}
		this.pullSession.setSession(sess);
		this.pullSession.setPullMsg(msg);
		this.dispatch();
	} 
	
	public void cleanSession(){
	}
	
	@Override
	void doDispatch() throws IOException{  
		if(this.pullSession == null) return;
		if(this.pullSession.getSession() == null) return;
		if(this.pullSession.getPullMsg() == null) return;
		
		String msgId = this.pullSession.getPullMsg().getMsgId();
		
		if(this.msgQ.containsKey(msgId)){
			Message msg = this.msgQ.get(msgId);  
			if(!msgId.equals(msg.getMsgId())){
				return;
			} 
			this.msgQ.remove(msgId);
			try { 
				prepareMessageStatus(msg);
				this.pullSession.getSession().write(msg); 
				
				if(this.pullSession.window.get()>0){
					this.pullSession.window.decrementAndGet();
				}
				
			} catch (IOException ex) {   
				log.error(ex.getMessage(), ex);  
			} 

			if(this.pullSession.window.get() != -1 && this.pullSession.window.get() <= 0){
				this.pullSession.setSession(null);
			}

		} 
	}
	
	public List<ConsumerInfo> getConsumerInfoList() {
		List<ConsumerInfo> res = new ArrayList<ConsumerInfo>();
		if(this.pullSession != null && this.pullSession.getSession() != null){
			PullSession value = this.pullSession;
			Session sess = value.getSession(); 
			ConsumerInfo info = new ConsumerInfo();
			info.setStatus(sess.getStatus().toString());
			info.setRemoteAddr(sess.getRemoteAddress());
			res.add(info);
		}
		return res;
	}
	@Override
	public int getMessageQueueSize() {
		return this.msgQ.size();
	}
	 
}
