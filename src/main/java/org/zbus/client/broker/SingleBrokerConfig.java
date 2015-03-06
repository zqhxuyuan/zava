package org.zbus.client.broker;

import org.zbus.common.pool.impl.GenericObjectPoolConfig;

public class SingleBrokerConfig{
	private GenericObjectPoolConfig poolConfig;
	private String brokerAddress = "127.0.0.1:15555";
	
	public SingleBrokerConfig(){
		this.poolConfig = new GenericObjectPoolConfig();//default
	}
	public SingleBrokerConfig(GenericObjectPoolConfig poolConfig){
		this.poolConfig = poolConfig;
	}
	
	public String getBrokerAddress() {
		return brokerAddress;
	}
	public void setBrokerAddress(String brokerAddress) {
		this.brokerAddress = brokerAddress;
	}
	public GenericObjectPoolConfig getPoolConfig() {
		return poolConfig;
	}
	public void setPoolConfig(GenericObjectPoolConfig poolConfig) {
		this.poolConfig = poolConfig;
	}
}
