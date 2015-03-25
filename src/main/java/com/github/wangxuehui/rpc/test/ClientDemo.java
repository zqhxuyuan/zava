package com.github.wangxuehui.rpc.test;

import com.github.wangxuehui.rpc.snrpc.SnRpcClient;
import com.github.wangxuehui.rpc.snrpc.SnRpcConnectionFactory;
import com.github.wangxuehui.rpc.snrpc.client.CommonSnRpcClient;
import com.github.wangxuehui.rpc.snrpc.client.SnNettyRpcConnectionFactory;
import com.github.wangxuehui.rpc.snrpc.zookeeper.consumer.ServiceConsumer;

public class ClientDemo {

    public static void main(String[] args) {
        ServiceConsumer consumer = new ServiceConsumer();
        String provider = consumer.lookup();
        //zk中的格式是: skyim:127.0.0.1:8081 配置的是netty服务端的地址和端口
        //客户端根据netty服务端信息建立到服务端的连接
        String[] providers = provider.split(":");

        if(providers.length == 3) {
            System.out.println(providers);
            //RpcConnection连接的工厂类,通过它可以获得Connection连接,进而用于发送数据
            SnRpcConnectionFactory factory = new SnNettyRpcConnectionFactory(
                    providers[1], Integer.parseInt(providers[2]));
            SnRpcClient client = new CommonSnRpcClient(factory);
            try {
                SnRpcInterface clazz = client.proxy(SnRpcInterface.class);
                String message = clazz.getMessage("come on");
                System.out.println("client receive message .... : " + message);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}

