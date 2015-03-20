package com.github.wangxuehui.rpc.client;

import com.github.wangxuehui.rpc.snrpc.SnRpcServer;
import com.github.wangxuehui.rpc.snrpc.conf.SnRpcConfig;
import com.github.wangxuehui.rpc.snrpc.server.SnNettyRpcServer;
import com.github.wangxuehui.rpc.snrpc.zookeeper.provider.ServiceProvider;

public class ServerDemo {
	public static void main(String[] args) {
		SnRpcInterface inter = new SnRpcImpl();
		SnRpcServer server = new SnNettyRpcServer(new Object[] { inter });		
		SnRpcConfig snRpcConfig = SnRpcConfig.getInstance();
		ServiceProvider provider = new ServiceProvider();
        provider.publish(snRpcConfig.getProperty("snrpc.http.host", "127.0.0.1"),
                Integer.parseInt(snRpcConfig.getProperty("snrpc.http.port","8080")));
		try {
			server.start();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
