package org.zbus.server.mq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import org.zbus.common.logging.Logger;
import org.zbus.common.logging.LoggerFactory;
import org.zbus.common.protocol.ConsumerInfo;
import org.zbus.common.remoting.Message;
import org.zbus.common.remoting.nio.Session;

public class RequestQueue extends MessageQueue {     
	private static final long serialVersionUID = -7640938066598234399L;

	private static final Logger log = LoggerFactory.getLogger(RequestQueue.class); 
	
	protected final BlockingQueue<Message> msgQ = new LinkedBlockingQueue<Message>();
	transient BlockingQueue<PullSession> sessQ = new LinkedBlockingQueue<PullSession>();
	
	public RequestQueue(String broker, String name, ExecutorService executor, int mode){
		super(broker, name, executor, mode); 
	}  
	 
	void enqueue(final Message msg) { 
		msgQ.offer(msg);  
		
		executor.submit(new Runnable() {
			@Override
			public void run() {
				try{
					if(messageStore != null){
						messageStore.saveMessage(msg);
					}
				} catch (Exception e){
					log.error(e.getMessage(), e);
				}
			}
		});
		
	}
	 
	Message dequeue() { 
		final Message msg = msgQ.poll();
		
		if(msg != null){
			executor.submit(new Runnable() {
				@Override
				public void run() {
					try{
						if(messageStore != null){
							messageStore.removeMessage(msg);
						}
					} catch (Exception e){
						log.error(e.getMessage(), e);
					}
				}
			});
		}
		
		return msg;
	}
	
	
	public void produce(Message msg, Session sess) throws IOException{
		String msgId = msg.getMsgId(); 
		if(msg.isAck()){
			ReplyHelper.reply200(msgId, sess);
		}  
    	enqueue(msg); 
    	this.dispatch();
	}
	
	public void consume(Message msg, Session sess) throws IOException{ 
		for(PullSession pull : sessQ){
			if(pull.getSession() == sess){
				pull.setPullMsg(msg);
				this.dispatch();
				return; 
			}
		} 
		PullSession pull = new PullSession(sess, msg);
		sessQ.offer(pull);  
		this.dispatch();
	} 
	
	public void cleanSession(){
		Iterator<PullSession> iter = sessQ.iterator();
		while(iter.hasNext()){
			PullSession ps = iter.next();
			if(!ps.session.isActive()){
				iter.remove();
			}
		}
	}
	
	@Override
	void doDispatch() throws IOException{  
		while(msgQ.peek() != null && sessQ.peek() != null){
			PullSession pull = sessQ.poll(); 
			if(pull == null || pull.window.get() == 0){
				continue;
			}
			if( !pull.getSession().isActive() ){ 
				continue;
			} 
			
			Message msg = dequeue();
			if(msg == null){
				continue;
			} 
			try { 
				
				Message pullMsg = pull.getPullMsg(); 
				Message writeMsg = Message.copyWithoutBody(msg);
				
				prepareMessageStatus(writeMsg);
				writeMsg.setMsgIdRaw(msg.getMsgId());  //保留原始消息ID
				writeMsg.setMsgId(pullMsg.getMsgId()); //配对订阅消息！
				pull.getSession().write(writeMsg); 
				
				if(pull.window.get()>0){
					pull.window.decrementAndGet();
				}
				
			} catch (IOException ex) {   
				log.error(ex.getMessage(), ex); 
				enqueue(msg); 
			} 

			if(pull.window.get() == -1 || pull.window.get() > 0){
				sessQ.offer(pull);
			}
		} 
	}
	
	public void loadMessageList(List<Message> msgs){
		this.msgQ.clear();
		this.msgQ.addAll(msgs);
	}
	
	public List<ConsumerInfo> getConsumerInfoList() {
		List<ConsumerInfo> res = new ArrayList<ConsumerInfo>();
		
		Iterator<PullSession> it = sessQ.iterator();
		while(it.hasNext()){
			PullSession value = it.next();
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
