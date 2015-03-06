package org.zbus.ha;

import org.zbus.client.Broker;
import org.zbus.client.Consumer;
import org.zbus.client.broker.HaBroker;
import org.zbus.client.broker.HaBrokerConfig;
import org.zbus.common.remoting.Message;

public class ConsumerExample {
	public static void main(String[] args) throws Exception{  
		//1）创建Broker代表
		HaBrokerConfig config = new HaBrokerConfig();
		config.setTrackAddrList("127.0.0.1:16666:127.0.0.1:16667");
		Broker broker = new HaBroker(config);
		
		//2) 创建消费者
		Consumer c = new Consumer(broker, "MyMQ");
		while(true){
			try{
				Message msg = c.recv(10000);
				if(msg == null) continue;
				System.out.println(msg);
			} catch(Exception e){
				Thread.sleep(1000);
				e.printStackTrace();
			}
		}
	} 
}
