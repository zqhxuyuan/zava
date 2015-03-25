package com.github.wangxuehui.rpc.snrpc.client;

import java.net.InetSocketAddress;

import com.github.wangxuehui.rpc.snrpc.SnRpcConnection;
import com.github.wangxuehui.rpc.snrpc.SnRpcConnectionFactory;

/**
 * @author skyim E-mail:wxh64788665@gmail.com
 * 类说明
 */
public class SnNettyRpcConnectionFactory implements SnRpcConnectionFactory{
	
	private InetSocketAddress serverAddr;
	
	public SnNettyRpcConnectionFactory(String host,int port){
		this.serverAddr = new InetSocketAddress(host,port);
	}
	
	@Override
	public SnRpcConnection getConnection() throws Throwable {
		return new SnNettyRpcConnection(this.serverAddr.getHostName(), this.serverAddr.getPort());
	}

	@Override
	public void recycle(SnRpcConnection connection) throws Throwable {
	}

}
