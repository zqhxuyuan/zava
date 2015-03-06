package org.zbus.client;

import org.zbus.common.Helper;

public class ClientHint {
	private final static String StartupIpAddress = Helper.getLocalIp();
	
	private String mq;
	private String broker;
	private String requestIp = StartupIpAddress; 
	
	public String getMq() {
		return mq;
	}
	public void setMq(String mq) {
		this.mq = mq;
	}
	public String getBroker() {
		return broker;
	}
	public void setBroker(String broker) {
		this.broker = broker;
	}
	public String getRequestIp() {
		return requestIp;
	}
	public void setRequestIp(String requestIp) {
		this.requestIp = requestIp;
	} 
	public static void main(String[] args){
		System.out.println(StartupIpAddress);
	}
}
