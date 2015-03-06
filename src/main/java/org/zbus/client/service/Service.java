package org.zbus.client.service;

import java.io.IOException;

import org.zbus.client.Consumer;
import org.zbus.common.logging.Logger;
import org.zbus.common.logging.LoggerFactory;
import org.zbus.common.remoting.Message;

public class Service extends Thread {   
	private static final Logger log = LoggerFactory.getLogger(Service.class);
	private final ServiceConfig config; 
	private Thread[] workerThreads;
	
	public Service(ServiceConfig config){
		this.config = config;
		if(config.getMq() == null || "".equals(config.getMq())){
			throw new IllegalArgumentException("MQ required");
		}
		if(config.getServiceHandler() == null){
			throw new IllegalArgumentException("serviceHandler required");
		}  
	}
	
	public void run(){   
		
		this.workerThreads = new Thread[config.getThreadCount()];
		for(int i=0;i<workerThreads.length;i++){
			WorkerThread thread = new WorkerThread(config); 
			this.workerThreads[i] = thread; 
			this.workerThreads[i].start();
		}
		
		for(Thread thread : this.workerThreads){
			try {
				thread.join();
			} catch (InterruptedException e) { 
				log.error(e.getMessage(), e);
			}
		}
	} 
}



class WorkerThread extends Thread{
	private static final Logger log = LoggerFactory.getLogger(WorkerThread.class);
	private ServiceConfig config = null; 
	
	public WorkerThread(ServiceConfig config){ 
		this.config  = config;  
	} 
	
	@Override
	public void run() {
		Consumer consumer =new Consumer(config.getBroker(), config.getMq());
		final int timeout = config.getReadTimeout(); //ms 
		consumer.setAccessToken(config.getAccessToken());
		consumer.setRegisterToken(config.getRegisterToken());
		
		while(true){
			try {  
				Message msg = consumer.recv(timeout); 
				if(msg == null) continue;  
				log.debug("Request: %s", msg);
				
				final String mqReply = msg.getMqReply();
				final String msgId  = msg.getMsgIdRaw(); //必须使用原始的msgId
				
				Message res = config.getServiceHandler().handleRequest(msg);
				
				if(res != null){ 
					res.setMsgId(msgId); 
					res.setMq(mqReply);		
					consumer.reply(res);
				} 
				
			} catch (IOException e) { 
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) { 
					//ignore
				}
			}
		}
	}
}

