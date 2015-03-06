package org.zbus.server;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.zbus.common.remoting.Message;
import org.zbus.common.remoting.MessageHandler;
import org.zbus.common.remoting.nio.Session;
import org.zbus.server.mq.ReplyHelper;


public class SubCommandHandler implements MessageHandler {   
	protected String accessToken = ""; 
	protected Map<String, MessageHandler> handlerMap = new ConcurrentHashMap<String, MessageHandler>();

	
	public void registerHandler(String command, MessageHandler handler){
    	this.handlerMap.put(command, handler);
    }
	
	@Override
	public void handleMessage(Message msg, Session sess) throws IOException {
		if(!accessToken.equals("") && !accessToken.equals(msg.getToken())){
    		ReplyHelper.reply403(msg, sess);
    		return;
    	}
		String subCmd = msg.getSubCommand(); 
		if(subCmd == null){
			subCmd = "";
		}
		
		MessageHandler handler = this.handlerMap.get(subCmd);
		if(handler == null){  
			msg.setBody("sub_cmd=%s Not Found", subCmd);
			ReplyHelper.reply404(msg, sess);
    		return; 
		} 
		handler.handleMessage(msg, sess);
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String value) {
		this.accessToken = value;
	} 
	
}
