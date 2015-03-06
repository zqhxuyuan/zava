package org.zbus.common.protocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TrackTable {  
	
	private Map<String, BrokerInfo> brokerTable = new ConcurrentHashMap<String, BrokerInfo>();
	private Map<String, List<MqInfo>> mqTable = new ConcurrentHashMap<String, List<MqInfo>>();
	
	public Map<String, BrokerInfo> getBrokerTable() {
		return brokerTable;
	}

	public Set<String> brokerAddresses(){
		return brokerTable.keySet();
	}
	
	public Map<String, List<MqInfo>> getMqTable() {
		return mqTable;
	} 
	
	public void sortMqTable(){
		this.mqTable.clear();
		Iterator<Entry<String, BrokerInfo>> iter = brokerTable.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, BrokerInfo> entry = iter.next();
			BrokerInfo brokerInfo = entry.getValue();
			for(Entry<String, MqInfo> e : brokerInfo.getMqTable().entrySet()){
				String mqName = e.getKey();
				MqInfo mqInfo = e.getValue();
				List<MqInfo> mqInfos = this.mqTable.get(mqName);
				if(mqInfos == null){
					mqInfos = new ArrayList<MqInfo>();
					this.mqTable.put(mqName, mqInfos);
				}
				mqInfos.add(mqInfo);
			}
		}
		for(List<MqInfo> mqInfos : mqTable.values()){
			Collections.sort(mqInfos, new Comparator<MqInfo>() {
				@Override
				public int compare(MqInfo m1, MqInfo m2) {
					double f1 = m1.getLoadFactor();
					double f2 = m2.getLoadFactor();
					if(f1 < f2){
						return 1;
					} else if(f1 == f2){
						return 0;
					}
					return -1;
				}
			});
		}
	}

	public void addBroker(String brokerAddress, BrokerInfo info){
		brokerTable.put(brokerAddress, info);
		sortMqTable();
	}
	
	public void removeBroker(String brokerAddress){
		brokerTable.remove(brokerAddress);
		sortMqTable();
	}
	
	public List<MqInfo> getMqInfo(String mq){
		return this.mqTable.get(mq);
	}

	public void setBrokerTable(Map<String, BrokerInfo> brokerTable) {
		this.brokerTable = brokerTable;
		sortMqTable();
	}

	public void setMqTable(Map<String, List<MqInfo>> mqTable) {
		this.mqTable = mqTable;
		sortMqTable();
	}

	@Override
	public String toString() {
		return "TrackTable [brokerTable=" + brokerTable + ", mqTable="
				+ mqTable + "]";
	}
	
	
}
