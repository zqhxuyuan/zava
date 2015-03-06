package org.zbus.server.mq.store;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.zbus.common.json.JSON;
import org.zbus.common.logging.Logger;
import org.zbus.common.logging.LoggerFactory;
import org.zbus.common.protocol.MessageMode;
import org.zbus.common.protocol.MqInfo;
import org.zbus.common.remoting.Message;
import org.zbus.common.remoting.MessageCodec;
import org.zbus.common.remoting.nio.IoBuffer;
import org.zbus.server.mq.MessageQueue;
import org.zbus.server.mq.RequestQueue;

import redis.clients.jedis.Jedis;

/**
 * NOT yet fully tested
 * @author 洪磊明(rushmore)
 *
 */
public class MessageStoreRedis implements MessageStore {
	private static final Logger log = LoggerFactory.getLogger(MessageStoreRedis.class);private Jedis jedis;

	private static final MessageCodec codec = new MessageCodec(); 
	
	private final Properties props = new Properties();
	private final static String CONFIG_FILE = "redis.properties";
	
	private final String brokerKey;
	
	public MessageStoreRedis(String broker){ 
		this.brokerKey = broker;
		//从配置文件中读取配置信息
		InputStream stream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE);
		try {
			if(stream != null){
				props.load(stream);
			} else {
				log.warn("missing properties: "+ CONFIG_FILE);
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		String host = props.getProperty("host", "localhost").trim();
		String portString = props.getProperty("port", "6379").trim();
		String password = props.getProperty("password", "").trim();
		int port = 6379;
		try{
			port = Integer.valueOf(portString);
		} catch (Exception e){
		}
		this.jedis = new Jedis(host, port); 
		if(!"".equals(password)){
			jedis.auth(password);
		}
	}
	
	private String msgKey(Message msg){
		return msg.getMsgId();
	}
	
	private String mqKey(String mq){
		return String.format("%s%s", brokerKey, mq);
	}
	
	@Override
	public void saveMessage(Message msg) {  
		String msgKey = msgKey(msg);
		String mqKey = mqKey(msg.getMq());  
		jedis.set(msgKey, msg.toString());  
		jedis.rpush(mqKey, msgKey);
	}

	@Override
	public void removeMessage(Message msg) {
		String msgKey = msgKey(msg);
		String mqKey = mqKey(msg.getMq());  
		
		jedis.del(msgKey);  
		jedis.lrem(mqKey, 1, msgKey); 
	}
	
	@Override
	public void onMessageQueueCreated(MessageQueue mq) { 
		String json = JSON.toJSONString(mq.getMqInfo());
		jedis.hset(this.brokerKey, mq.getName(), json);
	}
	
	@Override
	public void onMessageQueueRemoved(MessageQueue mq) { 
		jedis.hdel(this.brokerKey, mq.getName());
	}
	
	@Override
	public ConcurrentMap<String, MessageQueue> loadMqTable() { 
		Map<String, String> mqs = jedis.hgetAll(this.brokerKey);
		ConcurrentHashMap<String, MessageQueue> res = new ConcurrentHashMap<String, MessageQueue>();
		Iterator<Entry<String, String>> iter = mqs.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, String> e = iter.next();
			String mqName = e.getKey();
			String mqInfoString = e.getValue();
			MqInfo info = JSON.parseObject(mqInfoString, MqInfo.class);
			int mode = info.getMode();
			if(!MessageMode.isEnabled(mode, MessageMode.MQ)){
				log.warn("message queue mode not support");
				continue;
			} 
			
			RequestQueue mq = new RequestQueue(info.getBroker(),
					mqName, null, mode);
			mq.setCreator(info.getCreator());
			mq.setMessageStore(this);
			
			String mqKey = mqKey(mqName);
			
			//TODO batch
			List<String> msgIds = jedis.lrange(mqKey, 0, -1); 
			if(msgIds.size() == 0) continue;
			
			List<String> msgStrings = jedis.mget(msgIds.toArray(new String[0]));
			List<Message> msgs = new ArrayList<Message>();
			for(String msgString : msgStrings){
				if(msgString == null){
					log.warn("message missing");
					continue;
				}
				
				IoBuffer buf = IoBuffer.wrap(msgString);
				Message msg = (Message) codec.decode(buf);
				if(msg != null){
					msgs.add(msg);
				} else {
					log.error("message decode error");
				}
			}
			mq.loadMessageList(msgs);
			res.put(mqName, mq);
		}
		return res;
	}
	
	@Override
	public void start() { 
		
	}
	
	@Override
	public void shutdown() { 
		
	}
}
