package org.zbus.common.protocol;

import java.util.HashMap;
import java.util.Map;

public class BrokerInfo{
	private long lastUpdatedTime = System.currentTimeMillis();
	private String broker;
	private Map<String, MqInfo> mqTable = new HashMap<String, MqInfo>(); 
	
	public boolean isObsolete(long timeout){
		return (System.currentTimeMillis()-lastUpdatedTime)>timeout;
	}

	public long getLastUpdatedTime() {
		return lastUpdatedTime;
	}

	public void setLastUpdatedTime(long lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}

	public String getBroker() {
		return broker;
	}

	public void setBroker(String broker) {
		this.broker = broker;
	}

	public Map<String, MqInfo> getMqTable() {
		return mqTable;
	}

	public void setMqTable(Map<String, MqInfo> mqTable) {
		this.mqTable = mqTable;
	}

	
	
	@Override
	public String toString() {
		return "BrokerInfo [lastUpdatedTime=" + lastUpdatedTime + ", broker="
				+ broker + ", mqTable=" + mqTable + "]";
	} 
}