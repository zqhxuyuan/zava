package vintagewang.simplerpc.benchmark;


import vintagewang.simplerpc.DefaultRPCClient;
import vintagewang.simplerpc.RPCClient;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;


/**
 * 简单功能测试，Client端
 *
 * @author vintage.wang@gmail.com shijia.wxr@taobao.com
 */
public class Client {
    public static void main(String[] args) {
        RPCClient rpcClient = new DefaultRPCClient();
        boolean connectOK = rpcClient.connect(new InetSocketAddress("127.0.0.1", 2012), 1);
        System.out.println("connect server " + (connectOK ? "OK" : "Failed"));
        rpcClient.start();

        for (long i = 0;; i++) {
            try {
                String reqstr = "nice" + i;
                ByteBuffer repdata = rpcClient.call(reqstr.getBytes());
                if (repdata != null) {
                    String repstr =
                            new String(repdata.array(), repdata.position(), repdata.limit() - repdata.position());
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
