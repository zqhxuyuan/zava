package org.zbus.rpc;

import java.io.IOException;

import org.zbus.client.Broker;
import org.zbus.client.broker.SingleBroker;
import org.zbus.client.broker.SingleBrokerConfig;
import org.zbus.client.rpc.RpcServiceHandler;
import org.zbus.client.service.Service;
import org.zbus.client.service.ServiceConfig;
import org.zbus.common.Helper;
import org.zbus.rpc.biz.InterfaceImpl;

public class RpcServiceExample {
	public static void main(String[] args) throws IOException{  
		String address = Helper.option(args, "-b", "127.0.0.1:15555"); 
		int threadCount = Helper.option(args, "-c", 1);
		String service = Helper.option(args, "-s", "MyRpc");
		
		ServiceConfig config = new ServiceConfig();
		config.setThreadCount(threadCount); 
		config.setMq(service);
		//配置Broker
		SingleBrokerConfig brokerCfg = new SingleBrokerConfig();
		brokerCfg.setBrokerAddress(address);
		Broker broker = new SingleBroker(brokerCfg);
		config.setBroker(broker);
		
		
		RpcServiceHandler handler = new RpcServiceHandler(); 
		//增加模块，模块名在调用时需要指定
		handler.addModule(new InterfaceImpl());   
				
		//处理逻辑
		config.setServiceHandler(handler);
		
		Service svc = new Service(config);
		svc.start();  
	} 
}
