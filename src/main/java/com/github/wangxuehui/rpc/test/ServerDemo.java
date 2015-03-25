package com.github.wangxuehui.rpc.test;

import com.github.wangxuehui.rpc.snrpc.SnRpcServer;
import com.github.wangxuehui.rpc.snrpc.conf.SnRpcConfig;
import com.github.wangxuehui.rpc.snrpc.server.SnNettyRpcServer;
import com.github.wangxuehui.rpc.snrpc.zookeeper.provider.ServiceProvider;

public class ServerDemo {
	public static void main(String[] args) {
        //注册实现类到RPCServer中,并启动RPCServer服务器
		SnRpcInterface inter = new SnRpcImpl();
		SnRpcServer server = new SnNettyRpcServer(new Object[] { inter });

        //初始化ZooKeeper信息,创建ZK节点
        SnRpcConfig snRpcConfig = SnRpcConfig.getInstance();
        ServiceProvider provider = new ServiceProvider();
        provider.publish(snRpcConfig.getProperty("snrpc.http.host", "127.0.0.1"),
                Integer.parseInt(snRpcConfig.getProperty("snrpc.http.port","8080")));

        //启动Rpc服务器
		try {
			server.start();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
