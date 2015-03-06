package org.zbus.client.broker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.zbus.client.Broker;
import org.zbus.client.ClientHint;
import org.zbus.client.ZbusException;
import org.zbus.common.Helper;
import org.zbus.common.logging.Logger;
import org.zbus.common.logging.LoggerFactory;
import org.zbus.common.protocol.MessageMode;
import org.zbus.common.protocol.MqInfo;
import org.zbus.common.protocol.Proto;
import org.zbus.common.protocol.TrackTable;
import org.zbus.common.remoting.Message;
import org.zbus.common.remoting.RemotingClient;
import org.zbus.common.remoting.ticket.ResultCallback;
 

public class HaBroker implements Broker, TrackListener {
	private static final Logger log = LoggerFactory.getLogger(HaBroker.class);
	private volatile TrackTable trackTable = new TrackTable();
	private String trackAddressList;
	private HaBrokerConfig config;
	public TrackAgent trackAgent;
	private final String requestIp = Helper.getLocalIp();
 
	private Map<String, SingleBroker> brokers = new ConcurrentHashMap<String, SingleBroker>();

	public HaBroker(HaBrokerConfig config) {
		this.config = config;
		this.trackAddressList = config.getTrackAddrList(); 
		try {
			this.trackAgent = new TrackAgent(this.trackAddressList);
			this.trackAgent.addTrackListener(this);
			this.trackAgent.waitForReady(3000);
		} catch (IOException e) { 
			log.error(e.getMessage(), e);
		}
	} 
	
	private SingleBroker getBrokerByAddress(String address){
		return this.brokers.get(address);
	} 
	
	@Override
	public void onTrackTableUpdated(TrackTable trackTable) {
		this.trackTable = trackTable; 
		for (String brokerAddress : trackTable.brokerAddresses()) {
			SingleBroker broker = this.brokers.get(brokerAddress);
			if (broker != null) continue;
			
			SingleBrokerConfig singleConfig = new SingleBrokerConfig(
					this.config.getPoolConfig());
			singleConfig.setBrokerAddress(brokerAddress);
			
			try {
				broker = new SingleBroker(singleConfig);
				this.brokers.put(brokerAddress, broker);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			} 
		}
		
		Iterator<Entry<String, SingleBroker>> iter = this.brokers.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, SingleBroker> entry = iter.next();
			String brokerAddress = entry.getKey();
			SingleBroker broker = entry.getValue();
			if(!this.trackTable.brokerAddresses().contains(brokerAddress)){
				broker.destroy();
				iter.remove();
			}
		} 
	}
	
	@Override
	public void destroy() { 
		for (SingleBroker broker : this.brokers.values()) {
			broker.destroy();
		}
	}

	private boolean isProducer(Message msg){
		return Proto.Produce.equals(msg.getCommand())||
				Proto.Request.equals(msg.getCommand());
	}
	
	private void invokeAsyncByBroker(String brokerAddress, Message msg, ResultCallback callback) throws IOException{
		SingleBroker broker = getBrokerByAddress(brokerAddress);
		if(broker == null){
			String errorMsg = brokerAddress+" zbus broker missing";
			log.error(errorMsg);
			throw new ZbusException(errorMsg);
		}
		broker.invokeAsync(msg, callback);
	}
	
	private Message invokeSyncByBroker(String brokerAddress, Message msg, int timeout) throws IOException{
		SingleBroker broker = getBrokerByAddress(brokerAddress);
		if(broker == null){
			String errorMsg = brokerAddress+" zbus broker missing";
			log.error(errorMsg);
			throw new ZbusException(errorMsg);
		}
		return broker.invokeSync(msg, timeout);
	}
	
	@Override
	public void invokeAsync(Message msg, ResultCallback callback)
			throws IOException { 
		if(!isProducer(msg)){//
			log.warn("produce message required");
			throw new ZbusException("produce message required");
		}
		String brokerAddress = msg.getBroker();
		//1)指定Broker优先
		if(brokerAddress != null){
			invokeAsyncByBroker(brokerAddress, msg, callback);
			return;
		}
		//2)根据命令类型选择合适Broker
		List<MqInfo> mqInfos = trackTable.getMqInfo(msg.getMq());
		if(mqInfos == null || mqInfos.size() == 0){ 
			throw new ZbusException("no broker available");
		}
		//对发布订阅特殊处理
		int mode = mqInfos.get(0).getMode();
		if(MessageMode.isEnabled(mode, MessageMode.PubSub)){
			for(MqInfo info: mqInfos){
				brokerAddress = info.getBroker(); 
				//TODO call the callback once only
				invokeAsyncByBroker(brokerAddress, msg, callback); 
			}
		} 
		//最合适的的排位第一
		brokerAddress = mqInfos.get(0).getBroker(); 
		invokeAsyncByBroker(brokerAddress, msg, callback);
	}

	@Override
	public Message invokeSync(Message msg, int timeout) throws IOException { 
		if(!isProducer(msg)){//
			log.warn("produce message required");
			throw new ZbusException("produce message required");
		}
		String brokerAddress = msg.getBroker();
		//1)指定Broker优先
		if(brokerAddress != null){
			return invokeSyncByBroker(brokerAddress, msg, timeout );
		}
		//2)根据MQ选择合适Broker
		List<MqInfo> mqInfos = trackTable.getMqInfo(msg.getMq()); 
		if(mqInfos == null || mqInfos.size() == 0){ 
			throw new ZbusException("no broker available");
		}
		//对发布订阅特殊处理
		int mode = mqInfos.get(0).getMode();
		if(MessageMode.isEnabled(mode, MessageMode.PubSub)){
			Message res = null;
			for(MqInfo info: mqInfos){
				brokerAddress = info.getBroker(); 
				//TODO collect results
				res = invokeSyncByBroker(brokerAddress, msg, timeout); 
			}
			return res;
		} 
		
		//最合适的的排位第一
		brokerAddress = mqInfos.get(0).getBroker(); 
		return invokeSyncByBroker(brokerAddress, msg, timeout);
	}

	private RemotingClient getClientByBroker(String brokerAddress) throws IOException{
		SingleBroker broker = getBrokerByAddress(brokerAddress);
		if(broker == null){
			String errorMsg = brokerAddress+" zbus broker missing";
			log.error(errorMsg);
			throw new IOException(errorMsg);
		}
		ClientHint hint = new ClientHint();
		hint.setBroker(brokerAddress);
		return broker.getClient(hint);
	}
	@Override
	public RemotingClient getClient(ClientHint hint) throws IOException {
		String brokerAddress = hint.getBroker();
		//1)指定Broker优先
		if(brokerAddress != null){
			return getClientByBroker(brokerAddress);
		}
		//2)根据MQ来找有滞留消息的队列
		String mq = hint.getMq();
		List<MqInfo> mqInfos = null;
		if(mq != null){
			mqInfos = trackTable.getMqInfo(hint.getMq()); 
			if(mqInfos != null && mqInfos.size() > 0){ 
				MqInfo info = mqInfos.get(mqInfos.size()-1);
				if(info.getUnconsumedMsgCount()>0){ //有没有消费掉消息优先补上
					return getClientByBroker(info.getBroker());
				}
			}
		}
		
		//3)默认根据请求IP簇集
		List<String> list = new ArrayList<String>(trackTable.brokerAddresses());
		if(list.size() == 0){
			throw new IOException("no broker available");
		}
		String requestIp = hint.getRequestIp();
		if(requestIp == null){
			requestIp = this.requestIp;
		}
		brokerAddress = list.get(Math.abs(requestIp.hashCode())%list.size());
		return getClientByBroker(brokerAddress);
	}

	@Override
	public void closeClient(RemotingClient client) throws IOException {
		String brokerAddress = client.attr("broker");
		if(brokerAddress == null){
			log.warn("unable to find client's broker, missing attribute");
			client.close();
		} else {
			Broker broker = getBrokerByAddress(brokerAddress);
			if(broker != null){
				broker.closeClient(client);
			} else {
				log.warn("unable to find client's broker");
				client.close();
			}
		}
	}
}
