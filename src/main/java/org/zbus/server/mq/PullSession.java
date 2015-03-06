package org.zbus.server.mq;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.zbus.common.remoting.Message;
import org.zbus.common.remoting.nio.Session;

public class PullSession {
	public static final int HIGHT_WATER_MARK = 100;
	Session session;
    Message pullMsg;
    AtomicInteger window = new AtomicInteger(1);
    
    final ReentrantLock pullMsgLock = new ReentrantLock();
	final Set<String> topicSet = new HashSet<String>(); 
	final BlockingQueue<Message> msgQ = new LinkedBlockingQueue<Message>(); 
	
	public PullSession(Session sess, Message msg) { 
		this.session = sess;
		this.setPullMsg(msg);
	}
	
	public void subscribeTopics(String topicString){
		if(topicString == null) return;  
		String[] ts = topicString.split("[,]");
		for(String t : ts){
			if(t.trim().isEmpty()) continue;
			topicSet.add(t.trim());
		}
	}
	
	public boolean isTopicMatched(String topic){
		if(topic == null) return false;  
		if(topicSet.contains("*")) return true;
		return topicSet.contains(topic);
	}
	
	
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	public Message getPullMsg() {
		return this.pullMsg;
	}
	
	public void setPullMsg(Message msg) { 
		this.pullMsg = msg;
		if(msg == null) return;
		
		String window = this.pullMsg.getWindow();
		if(window != null){
			try{
				this.window.set(Integer.valueOf(window));
			} catch(Exception e){
				e.printStackTrace();//
			}
		} else {
			this.window.set(1);; //default to 1
		}
		
		String topic = this.pullMsg.getTopic();
		if(topic != null){
			this.subscribeTopics(topic);
		}
	} 
	
	public Set<String> getTopics(){
		return this.topicSet;
	}

	public BlockingQueue<Message> getMsgQ() {
		return msgQ;
	}
}

