package com.github.vintagewang.simplerpc.benchmark;


import com.github.vintagewang.simplerpc.DefaultRPCClient;
import com.github.vintagewang.simplerpc.RPCClient;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;


/**
 * 简单功能测试，Client端
 *
 * connect server OK
 ReadSocketService service started
 WriteSocketService service started
 call result, nice0
 call result, nice1
 call result, nice2
 call result, nice3

 * @author vintage.wang@gmail.com shijia.wxr@taobao.com
 */
public class Client {
    public static void main(String[] args) {
        RPCClient rpcClient = new DefaultRPCClient();
        boolean connectOK = rpcClient.connect(new InetSocketAddress("127.0.0.1", 2012), 1);
        System.out.println("connect server " + (connectOK ? "OK" : "Failed"));
        rpcClient.start();

        for (long i = 0;i<10; i++) {
            try {
                String reqstr = "nice" + i;
                //send msg body to server
                ByteBuffer repdata = rpcClient.call(reqstr.getBytes());
                if (repdata != null) {
                    String repstr = new String(repdata.array(), repdata.position(), repdata.limit() - repdata.position());
                    System.out.println("call result, " + repstr);
                }
                else {
                    return;
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
