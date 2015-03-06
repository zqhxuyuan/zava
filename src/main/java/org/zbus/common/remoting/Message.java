package org.zbus.common.remoting;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import org.zbus.common.logging.Logger;
import org.zbus.common.logging.LoggerFactory;
 


public class Message implements Serializable { 
	private static final long serialVersionUID = 4379223525215626137L;
	private static final Logger log = LoggerFactory.getLogger(Message.class);
	
	public static final String HEARTBEAT         = "heartbeat"; //心跳消息
	
	//使用到的标准HTTP头部
	public static final String HEADER_REMOTE_ADDR      = "remote-addr";
	public static final String HEADER_CONTENT_ENCODING = "content-encoding";
	public static final String HEADER_CONTENT_LENGTH   = "content-length";
	public static final String HEADER_CONTENT_TYPE     = "content-type";
	
	//扩展HTTP协议头部
	public static final String HEADER_CMD    	 = "cmd"; 
	public static final String HEADER_SUBCMD     = "sub_cmd";    
	public static final String HEADER_MQ         = "mq";
	public static final String HEADER_MQ_REPLY   = "mq_reply";
	public static final String HEADER_MSGID      = "msgid";	    //消息ID
	public static final String HEADER_MSGID_RAW  = "msgid_raw"; //原始消息ID
	
	public static final String HEADER_TOKEN      = "token";
	public static final String HEADER_BROKER     = "broker"; 
	public static final String HEADER_TOPIC      = "topic"; //使用,分隔 
	public static final String HEADER_ACK        = "ack";	 	 
	public static final String HEADER_WINDOW     = "window";
	public static final String HEADER_REPLY_CODE = "reply_code";
	
	
	 
	protected Meta meta = new Meta(); 
	protected Map<String, String> head = new ConcurrentHashMap<String, String>();
	protected byte[] body; 
	
	
	
	public Message(){
		setBody((byte[])null);
	} 
	
	public static Message copyWithoutBody(Message msg){
		Message res = new Message();
		res.meta = new Meta(msg.meta);
		res.head = new HashMap<String, String>(msg.head);
		res.body = msg.body;
		return res;
	}
	
	public String getMetaString() {
		return meta.toString();
	}
	
	public Meta getMeta(){
		return meta;
	}
	
	public void setMeta(String meta) { 
		this.meta = new Meta(meta);
	}
	
	public void setMeta(Meta meta) { 
		this.meta = meta;
	}
	
	public Map<String, String> getHead() {
		return head;
	} 
	
	public void setHead(Map<String, String> head) {
		this.head = head;
	} 
	
	public String getHead(String key){
		return this.head.get(key);
	}
	
	public void setHead(String key, String value){
		if(value == null) return;
		this.head.put(key, value);
	} 
	
	public String removeHead(String key){
		return this.head.remove(key);
	}
	
	public String getParam(String key){
		return meta.getParam(key); 
	}
	
	public String getHeadOrParam(String key){ 
		String value = getHead(key);
		if(value == null){
			value = getParam(key); 
		} 
		return value;
	}
	
	public String getHeadOrParam(String key, String defaultValue) { 
		String value = getHeadOrParam(key);
		if(value == null){
			value = defaultValue;
		}
		return value;
	}   
	
	public byte[] getBody() {
		byte[] b = body;
		String bodyOfHead = getHead("body");
		if(b == null && bodyOfHead != null){
			b = bodyOfHead.getBytes();
		}
		return b;
	}
	
	public void setBody(byte[] body) {
		int len = 0;
		if( body != null){
			len = body.length;
		}
		this.setHead(HEADER_CONTENT_LENGTH, ""+len);
		this.body = body;
	}
	
	public void setBody(String body){
		setBody(body.getBytes());
	} 
	
	public Message setBody(String format, Object ...args) { 
		this.setBody(String.format(format, args));
		return this;
	} 
	
	public void setJsonBody(String body){
		this.setJsonBody(body.getBytes());
	}
	
	public void setJsonBody(byte[] body){
		this.setHead(HEADER_CONTENT_TYPE, "application/json");
		this.setBody(body);
	}
	
	public String getBodyString() {
		if (this.getBody() == null) return null;
		return new String(this.getBody());
	}

	public String getBodyString(String encoding) {
		if (this.getBody() == null) return null;
		try {
			return new String(this.getBody(), encoding);
		} catch (UnsupportedEncodingException e) {
			return new String(this.getBody());
		}
	}
	
	//////////////////////////////////////////////////////////////
	
	public void decodeHeaders(byte[] data, int offset, int size){
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(new 
				ByteArrayInputStream(data, offset, size)));
	
			String meta = in.readLine();
			if(meta == null) return;
			this.meta = new Meta(meta);
			
			String line = in.readLine();
	        while (line != null && line.trim().length() > 0) {
	            int p = line.indexOf(':');
	            if (p >= 0){ 
	                head.put(line.substring(0, p).trim().toLowerCase(), line.substring(p + 1).trim());
	            } 
	            line = in.readLine();
	        }
	        //合并: header优先，url参数次之
	        if(this.meta.params != null){
	        	 for(Map.Entry<String, String> kv : this.meta.params.entrySet()){
	        		 String key = kv.getKey().toLowerCase();
	        		 if(!head.containsKey(key)){
	        			 head.put(key, kv.getValue());
	        		 }
	        	 }
	        }
	       
		} catch(IOException e){ 
			log.error(e.getMessage(), e);
		}
	}
	
	public int estimatedSize(){
		int size = 0;
		size += meta.toString().length();
		
		Iterator<Entry<String, String>> iter = head.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, String> e = iter.next();
			size += e.getKey().length() + e.getKey().length();
		} 
		
		if(body != null){
			size += body.length;
		}
		return size;
	} 
	
	public String getCommand() { 
		return this.getHeadOrParam(HEADER_CMD);
	} 
	public Message setCommand(String value) {
		this.setHead(HEADER_CMD, value); 
		return this;
	}  
	
	public String getSubCommand() { 
		return this.getHeadOrParam(HEADER_SUBCMD);
	} 
	public Message setSubCommand(String value) {
		this.setHead(HEADER_SUBCMD, value); 
		return this;
	}   
	
	public String getBroker(){
		return this.getHeadOrParam(HEADER_BROKER);
	}
	
	public void setBroker(String value){
		this.setHead(HEADER_BROKER, value);
	}
	
	public String getMqReply() {
		return this.getHeadOrParam(HEADER_MQ_REPLY);
	}
	public Message setMqReply(String value) {
		this.setHead(HEADER_MQ_REPLY, value);
		return this;
	}
	
	public String getEncoding() {
		return this.getHeadOrParam(HEADER_CONTENT_ENCODING);
	}
	public Message setEncoding(String encoding) {
		this.setHead(HEADER_CONTENT_ENCODING, encoding);
		return this;
	}
	
	public String getMsgId() {
		return this.getHeadOrParam(HEADER_MSGID);
	}
	public Message setMsgId(String msgId) {
		if(msgId == null) return this;
		this.setHead(HEADER_MSGID, msgId);
		return this;
	}
	
	
	public String getMsgIdRaw() {
		return this.getHeadOrParam(HEADER_MSGID_RAW);
	}
	public Message setMsgIdRaw(String value) {
		if(value == null) return this;
		this.setHead(HEADER_MSGID_RAW, value);
		return this;
	}
	
	public boolean isAck() {
		String ack = this.getHeadOrParam(HEADER_ACK);
		if(ack == null) return true; //默认ack为true
		ack = ack.trim().toLowerCase();
		return ack.equals("1") || ack.equals("true");
	}
	
	public void setAck(boolean ack){
		String value = ack? "1":"0";
		this.setHead(HEADER_ACK, value);
	}
	
	public String getMq(){
		String value = this.getHeadOrParam(HEADER_MQ);
		if(value == null){
			value = getPath();
		}
		return value;
	}
	
	public String getUri(){
		return this.meta.uri;
	}
	
	public String getPath(){
		return this.meta.path;
	}
	 
	 
	public Message setMq(String mq) {
		this.setHead(HEADER_MQ, mq);
		return this;
	} 
	
	public String getToken() {
		return this.getHeadOrParam(HEADER_TOKEN);
	}
	public Message setToken(String token) {
		this.setHead(HEADER_TOKEN, token);
		return this;
	} 
	
	public String getTopic() {
		return getHeadOrParam(HEADER_TOPIC);
	}

	public Message setTopic(String topic) {
		this.setHead(HEADER_TOPIC, topic);
		return this;
	} 
	
	public String getWindow() {
		return getHeadOrParam(HEADER_WINDOW);
	}

	public Message setWindow(int window) {
		this.setHead(HEADER_WINDOW, ""+window);
		return this;
	} 
	
	public String getReplyCode() {
		return getHeadOrParam(HEADER_REPLY_CODE);
	}
	public Message setReplyCode(String value) {
		this.setHead(HEADER_REPLY_CODE, value);
		return this;
	} 
	
	
	public String getStatus() {  
		return meta.status;
	}
	public Message setStatus(String status) { 
		meta.status = status;
		return this; 
	}
	
	public boolean isStatus200() {
		return "200".equals(this.getStatus());
	}

	public boolean isStatus404() {
		return "404".equals(this.getStatus());
	}

	public boolean isStatus500() {
		return "500".equals(this.getStatus());
	}  
	
	protected String getBodyPrintString() {
		if (this.body == null)
			return null;
		if (this.body.length > 1024) {
			return new String(this.body, 0, 1024) + "...";
		} else {
			return getBodyString();
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(meta+"\r\n");
		
		List<String> keys = new ArrayList<String>(head.keySet());
		Collections.sort(keys);
		
		for(String key : keys){ 
			String val = head.get(key);
			sb.append(key+": "+val+"\r\n");
		}
		sb.append("\r\n");
		String bodyString = getBodyPrintString();
		if(bodyString != null){
			sb.append(bodyString);
		}
		return sb.toString();
	} 
	public static void main(String[] args){
		Message msg = new Message(); 
		msg.setStatus("200"); 
		msg.setStatus(null);
		System.out.println(msg);
	}
	 
}



class Meta implements Serializable{ 
	private static final long serialVersionUID = -8557063231118504061L;
	//HTTP响应头部: 状态(200)
	String status; //根据status是否设置来决定Meta是请求还是应答	
	//HTTP请求头部: 方法(GET/POST)-RequestString-KV参数
	String method = "GET"; 
	String uri = "/";
	
	//请求分析出来的两个部分：path + kv组
	String path; 
	Map<String,String> params;
	
	
	
	static Set<String> httpMethod = new HashSet<String>();
	static Map<String,String> httpStatus = new HashMap<String, String>();
	
	static{ 
		httpMethod.add("GET");
		httpMethod.add("POST"); 
		httpMethod.add("PUT");
		httpMethod.add("DELETE");
		httpMethod.add("HEAD");
		httpMethod.add("OPTIONS"); 
		
		httpStatus.put("101", "Switching Protocols"); 
		httpStatus.put("200", "OK");
		httpStatus.put("201", "Created");
		httpStatus.put("202", "Accepted");
		httpStatus.put("204", "No Content"); 
		httpStatus.put("206", "Partial Content"); 
		httpStatus.put("301", "Moved Permanently");
		httpStatus.put("304", "Not Modified"); 
		httpStatus.put("400", "Bad Request"); 
		httpStatus.put("401", "Unauthorized"); 
		httpStatus.put("403", "Forbidden");
		httpStatus.put("404", "Not Found"); 
		httpStatus.put("405", "Method Not Allowed"); 
		httpStatus.put("416", "Requested Range Not Satisfiable");
		httpStatus.put("500", "Internal Server Error");
	}
	

	
	@Override
	public String toString() { 
		//如果status存在，理解为响应包，否则默认就是请求包
		if(this.status != null){
			String desc = httpStatus.get(this.status);
			if(desc == null){
				desc = "Unknown Status";
			}
			return String.format("HTTP/1.1 %s %s", status, desc); 
		}
		String method = this.method;
		String uri = this.uri;
		if(this.method == null) method = "";
		if(this.uri == null) uri = "";
		return String.format("%s %s HTTP/1.1", method, uri);
	}
	
	public Meta(){}
	
	public Meta(Meta m){
		this.uri = m.uri;
		this.path = m.path;
		this.method = m.method;
		this.status = m.status;
		if(m.params != null){
			this.params = new HashMap<String, String>(m.params);
		}
	}
	
	public Meta(String meta){
		if("".equals(meta)){
			return;
		}
		StringTokenizer st = new StringTokenizer(meta);
		String firstWord = st.nextToken();
		if(firstWord.toUpperCase().startsWith("HTTP")){ //理解为响应
			this.status = st.nextToken();
			return;
		}
		//理解为请求
		this.method = firstWord;  
		this.uri = st.nextToken();
		decodeURI(this.uri);
	} 
	
	private void decodeURI(String commandString){
		int idx = commandString.indexOf('?');
		if(idx < 0){
			this.path = decodeUrl(commandString);
		} else {
			this.path = commandString.substring(0, idx);
		}
		if(this.path.startsWith("/")){
			this.path = this.path.substring(1);
		}
		if(idx < 0) return;
		
		this.params = new HashMap<String, String>(); 
		String paramString = commandString.substring(idx+1); 
		StringTokenizer st = new StringTokenizer(paramString, "&");
        while (st.hasMoreTokens()) {
            String e = st.nextToken();
            int sep = e.indexOf('=');
            if (sep >= 0) {
                this.params.put(decodeUrl(e.substring(0, sep)).trim(),
                		decodeUrl(e.substring(sep + 1)));
            } else {
                this.params.put(decodeUrl(e).trim(), "");
            }
        } 
	}
	
	private String decodeUrl(String str) {
        String decoded = null;
        try {
            decoded = URLDecoder.decode(str, "UTF8");
        } catch (UnsupportedEncodingException ignored) {
        }
        return decoded;
    }
	
	public String getParam(String key){
		if(params == null) return null;
		return params.get(key);
	}
	
	public String getParam(String key, String defaultValue){
		String value = getParam(key);
		if(value == null){
			value = defaultValue;
		}
		return value;
	}
}