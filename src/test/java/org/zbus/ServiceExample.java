package org.zbus;

import java.io.IOException;

import org.zbus.client.Broker;
import org.zbus.client.broker.SingleBroker;
import org.zbus.client.broker.SingleBrokerConfig;
import org.zbus.client.service.Service;
import org.zbus.client.service.ServiceConfig;
import org.zbus.client.service.ServiceHandler;
import org.zbus.common.Helper;
import org.zbus.common.remoting.Message;

public class ServiceExample {
	
	public static void main(String[] args) throws IOException{  
		String address = Helper.option(args, "-b", "127.0.0.1:15555"); 
		int threadCount = Helper.option(args, "-c", 1);
		String service = Helper.option(args, "-s", "MyService");
		
		ServiceConfig config = new ServiceConfig();
		config.setThreadCount(threadCount); 
		config.setMq(service);
		//配置Broker
		SingleBrokerConfig brokerCfg = new SingleBrokerConfig();
		brokerCfg.setBrokerAddress(address);
		Broker broker = new SingleBroker(brokerCfg);
		config.setBroker(broker);
		
		//处理逻辑
		config.setServiceHandler(new ServiceHandler() { 
			@Override
			public Message handleRequest(Message request) { 
				System.out.println(request);
				Message result = new Message();
				result.setStatus("200");
				result.setBody("Server time: "+System.currentTimeMillis());	
				return result;
			}
		});
		
		Service svc = new Service(config);
		svc.start();  
	} 
	
}
