package org.zbus.common.protocol;

import java.util.List;

public class ConsumerInfo {
	private String remoteAddr;
	private String status;
	private List<String> topics;
	
	public String getRemoteAddr() {
		return remoteAddr;
	}
	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<String> getTopics() {
		return topics;
	}
	public void setTopics(List<String> topics) {
		this.topics = topics;
	}
	@Override
	public String toString() {
		return "ConsumerInfo [remoteAddr=" + remoteAddr + ", status=" + status
				+ ", topics=" + topics + "]";
	} 
	
}
