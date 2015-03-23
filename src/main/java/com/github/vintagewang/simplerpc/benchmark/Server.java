package com.github.vintagewang.simplerpc.benchmark;


import com.github.vintagewang.simplerpc.DefaultRPCServer;
import com.github.vintagewang.simplerpc.RPCProcessor;
import com.github.vintagewang.simplerpc.RPCServer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;


/**
 * 简单功能测试，Server端
 *
 * AcceptSocketService service started
 receive new connection, /127.0.0.1:58516
 ReadSocketService service started
 WriteSocketService service started

 read socket < 0
 processReadEvent error
 ReadSocketService service end

 * @author vintage.wang@gmail.com shijia.wxr@taobao.com
 */
public class Server {
    static class ServerRPCProcessor implements RPCProcessor {
        private final AtomicLong invokeTimesTotal = new AtomicLong(0);

        //requestId, msg body
        public byte[] process(int upId, ByteBuffer upstream) {
            // String upstr =
            // new String(upstream.array(), upstream.position(),
            // upstream.limit() - upstream.position());
            // Long value = this.invokeTimesTotal.getAndIncrement();
            // //System.out.println("server process, receive [" + upstr + "], "
            // + value);
            // return value.toString().getBytes();

            int length = upstream.limit() - upstream.position();
            byte[] response = new byte[length];
            upstream.get(response);
            return response;
        }


        public AtomicLong getInvokeTimesTotal() {
            return invokeTimesTotal;
        }
    }


    public static void main(String[] args) {
        try {
            if (args.length > 2) {
                System.err.println("Useage: mtclient [listenPort] [threadCnt]");
                return;
            }

            // args
            int listenPort = args.length > 0 ? Integer.valueOf(args[0]) : 2012;
            int threadCnt = args.length > 1 ? Integer.valueOf(args[1]) : 256;

            RPCServer rpcServer = new DefaultRPCServer(listenPort, threadCnt, threadCnt);
            ServerRPCProcessor serverRPCProcessor = new ServerRPCProcessor();
            rpcServer.registerProcessor(serverRPCProcessor);
            rpcServer.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

