package com.github.wangxuehui.rpc.client;

import com.github.wangxuehui.rpc.snrpc.SnRpcClient;
import com.github.wangxuehui.rpc.snrpc.SnRpcConnectionFactory;
import com.github.wangxuehui.rpc.snrpc.client.CommonSnRpcClient;
import com.github.wangxuehui.rpc.snrpc.client.SnNettyRpcConnectionFactory;
import com.github.wangxuehui.rpc.snrpc.zookeeper.consumer.ServiceConsumer;

public class ClientDemo {

    public static void main(String[] args) {
        ServiceConsumer consumer = new ServiceConsumer();
        String provider = consumer.lookup();
        String[] providers = provider.split(":");

        if(providers.length == 3) {
            System.out.println(providers);
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

