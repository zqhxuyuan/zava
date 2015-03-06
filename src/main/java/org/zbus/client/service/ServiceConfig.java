package org.zbus.client.service;

import org.zbus.client.MqConfig;




public class ServiceConfig extends MqConfig {
	private ServiceHandler serviceHandler;  
	private int threadCount = 1; 
	private int readTimeout = 10000;
	
	public int getThreadCount() {
		return threadCount;
	}
	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}
	
	public ServiceHandler getServiceHandler() {
		return serviceHandler;
	}
	public void setServiceHandler(ServiceHandler serviceHandler) {
		this.serviceHandler = serviceHandler;
	}
	public int getReadTimeout() {
		return readTimeout;
	}
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
}
