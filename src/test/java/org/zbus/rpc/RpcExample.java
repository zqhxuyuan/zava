package org.zbus.rpc;

import java.util.Arrays;
import java.util.Random;

import org.zbus.client.Broker;
import org.zbus.client.broker.SingleBroker;
import org.zbus.client.broker.SingleBrokerConfig;
import org.zbus.client.rpc.RpcConfig;
import org.zbus.client.rpc.RpcProxy;
import org.zbus.rpc.biz.Interface;
import org.zbus.rpc.biz.User;

public class RpcExample {
	public static User getUser(String name) {
		User user = new User();
		user.setName(name);
		user.setPassword("password" + System.currentTimeMillis());
		user.setAge(new Random().nextInt(100));
		user.setItem("item_1");
		user.setRoles(Arrays.asList("admin", "common"));
		return user;
	}

	public static void main(String[] args) throws Exception {
		// 1）创建Broker代表
		SingleBrokerConfig config = new SingleBrokerConfig();
		config.setBrokerAddress("127.0.0.1:15555");
		Broker broker = new SingleBroker(config);

		RpcConfig rpcConfig = new RpcConfig();
		rpcConfig.setBroker(broker);
		rpcConfig.setMq("MyRpc"); 
		
		Interface hello = RpcProxy.getService(Interface.class, rpcConfig);

		Object[] res = hello.objectArray();
		for (Object obj : res) {
			System.out.println(obj);
		}

		Object[] array = new Object[] { getUser("rushmore"), "hong", true, 1,
				String.class };
		
		
		int saved = hello.saveObjectArray(array);
		System.out.println(saved);
		 
		Class<?> ret = hello.classTest(String.class);
		System.out.println(ret);
	}
}
