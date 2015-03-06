package org.zbus.client.broker;

import org.zbus.common.pool.impl.GenericObjectPoolConfig;

public class HaBrokerConfig {
	private GenericObjectPoolConfig poolConfig;
	private String trackAddrList = "127.0.0.1:16666;127.0.0.1:16667";
	
	public HaBrokerConfig(){
		this.poolConfig = new GenericObjectPoolConfig();//default
	}
	public HaBrokerConfig(GenericObjectPoolConfig poolConfig){
		this.poolConfig = poolConfig;
	}
	
	public String getTrackAddrList() {
		return trackAddrList;
	}
	public void setTrackAddrList(String trackAddrList) {
		this.trackAddrList = trackAddrList;
	}
	public GenericObjectPoolConfig getPoolConfig() {
		return poolConfig;
	}
	public void setPoolConfig(GenericObjectPoolConfig poolConfig) {
		this.poolConfig = poolConfig;
	} 
	
}
