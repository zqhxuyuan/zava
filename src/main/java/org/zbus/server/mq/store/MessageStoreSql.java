package org.zbus.server.mq.store;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
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

public class MessageStoreSql implements MessageStore {
	private static final Logger log = LoggerFactory.getLogger(MessageStoreSql.class);
	private static final MessageCodec codec = new MessageCodec();
	
	private final static String CONFIG_FILE = "sql.properties";
	private final Properties props = new Properties();
	private Connection connection; 
	
	private String driver = "org.hsqldb.jdbcDriver";
	private String url = "jdbc:hsqldb:db/zbus";
	private String user = "sa";
	private String password = "";
	private String sqlMsgs = "CREATE TABLE IF NOT EXISTS msgs(id VARCHAR(128), msg_str VARCHAR(10240000), PRIMARY KEY(id) )";
	private String sqlMqMsgs = "CREATE TABLE IF NOT EXISTS mq_msgs(mq_id VARCHAR(128), msg_id VARCHAR(128) )";
	private String sqlMqs = "CREATE TABLE IF NOT EXISTS mqs(id VARCHAR(512), mq_info VARCHAR(10240000), PRIMARY KEY(id) )";
	
	
	private final String brokerKey;
	public MessageStoreSql(String broker) throws Exception {  
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
	}
	
	private Connection getConnection(){
		try { 
			url = props.getProperty("url", url).trim();
			user = props.getProperty("sa", user).trim();
			password = props.getProperty("password", password).trim();
			return DriverManager.getConnection(this.url, this.user, this.password);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}
	
	public void start() throws Exception{
		driver = props.getProperty("driver", driver).trim();
		Class.forName(driver);
		connection = getConnection();
		this.initDbTable();
	}
	
	
	public void shutdown() throws Exception{
		if(connection == null) return;
		try{
			Statement st = connection.createStatement();
			st.execute("SHUTDOWN");
			connection.close();
		}catch(Exception e){
			log.error(e.getMessage(), e);
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
		try{
			String msgId = msgKey(msg);
			String mqId = mqKey(msg.getMq());
			this.update("INSERT INTO msgs(id, msg_str) VALUES(?,?)",msgId, msg.toString());
			this.update("INSERT INTO mq_msgs(mq_id, msg_id) VALUES(?,?)", mqId, msgId);
			if(log.isDebugEnabled()){
				log.debug("save " + msgId);
			}
		} catch(Exception e){
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void removeMessage(Message msg) { 
		try{
			String msgId = msgKey(msg);
			this.update("DELETE FROM msgs WHERE id=?", msgId);
			this.update("DELETE FROM mq_msgs WHERE msg_id=?", msgId);
			if(log.isDebugEnabled()){
				log.debug("delete " + msgId);
			}
		} catch(Exception e){
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void onMessageQueueCreated(MessageQueue mq) {
		try{
			String mqId = mqKey(mq.getName());
			String json = JSON.toJSONString(mq.getMqInfo());
			this.update("INSERT INTO mqs(id, mq_info) VALUES(?,?)", mqId, json); 
		} catch(Exception e){
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void onMessageQueueRemoved(MessageQueue mq) {
		try{
			String mqId = mqKey(mq.getName());
			this.update("DELETE FROM mqs WHERE id=?", mqId); 
		} catch(Exception e){
			log.error(e.getMessage(), e);
		}
	}

	
	@Override
	public ConcurrentMap<String, MessageQueue> loadMqTable() throws SQLException {
		ConcurrentHashMap<String, MessageQueue> res = new ConcurrentHashMap<String, MessageQueue>();
		ResultSet mqRs = this.query("SELECT * FROM mqs");
		while(mqRs.next()) {
			String mqId = mqRs.getString("id");
			String mqName = mqId.substring(brokerKey.length());
			String mqInfoString = mqRs.getString("mq_info");  
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
			
			String sql = String.format("SELECT msg_str FROM mq_msgs, msgs WHERE mq_msgs.msg_id = msgs.id AND mq_msgs.mq_id='%s'", mqId);
			ResultSet msgRs = this.query(sql);
			List<Message> msgs = new ArrayList<Message>();
			while(msgRs.next()){
				String msgString = msgRs.getString("msg_str");
				IoBuffer buf = IoBuffer.wrap(msgString);
				Message msg = (Message) codec.decode(buf);
				if(msg != null){
					msgs.add(msg);
				} else {
					log.error("message decode error");
				}
			}
			msgRs.close();
			mq.loadMessageList(msgs);
			res.put(mqName, mq);
		}
		mqRs.close();
		return res;
	}
	
	private void initDbTable() throws SQLException{
		sqlMsgs = props.getProperty("sql_msgs", sqlMsgs).trim();
		sqlMqMsgs = props.getProperty("sql_mq_msgs", sqlMqMsgs).trim();
		sqlMqs = props.getProperty("sql_mqs", sqlMqs).trim();
		this.update(sqlMsgs);
		this.update(sqlMqMsgs);
		this.update(sqlMqs);
	}
	
	private synchronized ResultSet query(String sql) throws SQLException {
		if(this.connection == null) return null;
		Statement st = connection.createStatement();
		return st.executeQuery(sql); 
	}
	
	private synchronized void update(String sql, Object... args) throws SQLException { 
		if(this.connection == null) return;
		PreparedStatement st = connection.prepareStatement(sql);
		for(int i=0;i<args.length;i++){
			st.setObject(i+1, args[i]);
		}
		int i = st.executeUpdate();
		if (i == -1) {
			log.error("db error : " + sql);
		}
		st.close();
	}
}
