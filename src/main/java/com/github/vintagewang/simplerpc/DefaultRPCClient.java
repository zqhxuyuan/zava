package com.github.vintagewang.simplerpc;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 客户端实现
 *
 * @author vintage.wang@gmail.com shijia.wxr@taobao.com
 */
public class DefaultRPCClient implements RPCClient {
    // private Connection connection;
    private List<Connection> connectionList = new ArrayList<Connection>();
    private final AtomicInteger requestId = new AtomicInteger(0);

    private final ConcurrentHashMap<Integer, CallResponse> callRepTable =
            new ConcurrentHashMap<Integer, CallResponse>(1000000);

    private final ClientRPCProcessor clientRPCProcessor = new ClientRPCProcessor();

    class ClientRPCProcessor implements RPCProcessor {

        public byte[] process(int repId, ByteBuffer response) {
            CallResponse cr = DefaultRPCClient.this.callRepTable.get(repId);
            if (cr != null) {
                cr.setReponseId(repId);
                cr.setResponseBody(response);
                cr.getCountDownLatch().countDown();
            }
            return null;
        }
    }

    class CallResponse {
        private int reponseId;
        private ByteBuffer responseBody;
        private CountDownLatch countDownLatch = new CountDownLatch(1);


        public int getReponseId() {
            return reponseId;
        }


        public void setReponseId(int reponseId) {
            this.reponseId = reponseId;
        }


        public ByteBuffer getResponseBody() {
            return responseBody;
        }


        public void setResponseBody(ByteBuffer responseBody) {
            this.responseBody = responseBody;
        }


        public CountDownLatch getCountDownLatch() {
            return countDownLatch;
        }


        public void setCountDownLatch(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }


        public CallResponse(int reponseId) {
            this.reponseId = reponseId;
        }
    }


    public DefaultRPCClient() {

    }


    public void start() {
        for (Connection c : this.connectionList) {
            c.start();
        }
    }


    public void shutdown() {
        for (Connection c : this.connectionList) {
            c.shutdown();
        }
    }


    private Connection findConnection(int id) {
        int pos = Math.abs(id) % this.connectionList.size();
        return this.connectionList.get(pos);
    }


    public ByteBuffer call(byte[] request) throws InterruptedException {
        int id = this.requestId.incrementAndGet();
        CallResponse response = new CallResponse(id);
        this.callRepTable.put(id, response);
        this.findConnection(id).putRequest(id, request);
        boolean waitOK = response.getCountDownLatch().await(5000, TimeUnit.MILLISECONDS);
        ByteBuffer result = null;
        if (waitOK) {
            result = response.getResponseBody();
        }
        else {
            System.out.println("timeout, reqId = " + id);
        }

        this.callRepTable.remove(id);
        return result;
    }


    public boolean connect(InetSocketAddress remote) {
        SocketChannel sc = null;
        try {
            sc = SocketChannel.open();
            sc.configureBlocking(true);
            sc.socket().setSoLinger(false, -1);
            sc.socket().setTcpNoDelay(true);

            boolean connected = sc.connect(remote);
            if (connected) {
                sc.configureBlocking(false);
                Connection c = new Connection(sc, this.clientRPCProcessor, null);
                this.connectionList.add(c);
            }
            else {
                sc.close();
            }

            return connected;
        }
        catch (IOException e) {
            if (sc != null) {
                try {
                    sc.close();
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        return false;
    }


    public boolean connect(InetSocketAddress remote, int cnt) {
        int i;

        for (i = 0; i < cnt && this.connect(remote); i++) {
        }

        return i == cnt;
    }
}
