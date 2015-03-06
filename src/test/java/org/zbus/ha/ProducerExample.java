package org.zbus.ha;

import org.zbus.client.Broker;
import org.zbus.client.Producer;
import org.zbus.client.broker.HaBroker;
import org.zbus.client.broker.HaBrokerConfig;
import org.zbus.common.remoting.Message;
import org.zbus.common.remoting.ticket.ResultCallback;

public class ProducerExample {
	public static void main(String[] args) throws Exception {
		// 1）创建Broker代表
		HaBrokerConfig config = new HaBrokerConfig();
		config.setTrackAddrList("127.0.0.1:16666:127.0.0.1:16667");
		Broker broker = new HaBroker(config); 
		
		// 2) 创建生产者
		Producer producer = new Producer(broker, "MyMQ");
		while(true){
			Message msg = new Message();
			msg.setBody("hello world");
			try{
				producer.send(msg, new ResultCallback() {
					@Override
					public void onCompleted(Message result) {
						System.out.println(result);
					}
				});
				
			}catch(Exception ex){
				ex.printStackTrace();
			} finally{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
