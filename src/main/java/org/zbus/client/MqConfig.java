package org.zbus.client;

import org.zbus.common.protocol.MessageMode;

public class MqConfig { 
	protected Broker broker; //Broker，必须设置
	protected String mq;     //MQ标识，必须设置
	protected String accessToken = "";   //访问控制码
	protected String registerToken = ""; //MQ注册控制码, 不注册无需设置
	
	protected int mode = MessageMode.MQ.intValue(); //创建消息队列时采用到
	protected String topic = null; //发布订阅模式下使用
	
	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}

	public String getMq() {
		return mq;
	}

	public void setMq(String mq) {
		this.mq = mq;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRegisterToken() {
		return registerToken;
	}

	public void setRegisterToken(String registerToken) {
		this.registerToken = registerToken;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
	
}
