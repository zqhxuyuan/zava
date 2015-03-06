package org.zbus.client.rpc;

import org.zbus.client.MqConfig;

public class RpcConfig extends MqConfig {
	public static final String DEFAULT_ENCODING = "UTF-8";  
	
	protected String module = ""; 
	protected int timeout = 10000;
	protected String encoding = DEFAULT_ENCODING;
	
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
}
