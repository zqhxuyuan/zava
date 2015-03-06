package org.zbus.common.protocol;

import java.util.List;

public class MqInfo {
	private String broker;
	private String name;
	private int mode;
	private String creator;
	private long createdTime;
	private long unconsumedMsgCount;
	private List<ConsumerInfo> consumerInfoList;
	
	public String getBroker() {
		return broker;
	}
	public void setBroker(String broker) {
		this.broker = broker;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	public long getUnconsumedMsgCount() {
		return unconsumedMsgCount;
	}
	public void setUnconsumedMsgCount(long unconsumedMsgCount) {
		this.unconsumedMsgCount = unconsumedMsgCount;
	}
	public int getConsumerCount(){
		if(this.consumerInfoList == null) return 0;
		return this.consumerInfoList.size();
	}
	public List<ConsumerInfo> getConsumerInfoList() {
		return consumerInfoList;
	}
	public void setConsumerInfoList(List<ConsumerInfo> consumerInfoList) {
		this.consumerInfoList = consumerInfoList;
	}
	
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	
	public double getLoadFactor(){
		if(this.consumerInfoList == null ||
				this.consumerInfoList.size() == 0) return Double.MAX_VALUE;
		return 1.0*(unconsumedMsgCount+0.000001)/this.consumerInfoList.size();
	}
	
	@Override
	public String toString() {
		return "MqInfo [broker=" + broker + ", name=" + name + ", mode=" + mode
				+ ", creator=" + creator + ", createdTime=" + createdTime
				+ ", unconsumedMsgCount=" + unconsumedMsgCount
				+ ", consumerInfoList=" + consumerInfoList + "]";
	} 
	
	
}
