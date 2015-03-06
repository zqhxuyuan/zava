package org.zbus.common.protocol;

import java.util.Map;

import org.zbus.common.json.JSON;
import org.zbus.common.remoting.Message;

public class Proto {
	public static final String Heartbeat = Message.HEARTBEAT; //心跳消息 
	public static final String Produce   = "produce";         //生产消息
	public static final String Consume   = "consume";         //消费消息  
	public static final String Request   = "request";         //请求等待应答消息 
	public static final String Admin     = "admin";           //管理类消息  
	//管理类命令二级子命令 
	public static final String AdminCreateMQ    = "create_mq";  
	
	//TrackServer命令
	public static final String TrackReport      = "track_report"; 
	public static final String TrackSub         = "track_sub";  
	public static final String TrackPub         = "track_pub"; 
	
	
	public static Message buildSubCommandMessage(String cmd, String subCmd, Map<String, String> params){
    	Message msg = new Message();
    	msg.setCommand(cmd); 
    	msg.setSubCommand(subCmd); 
    	if(params != null){
    		msg.setJsonBody(JSON.toJSONBytes(params));
    	}
    	return msg;
    }
	 
}
