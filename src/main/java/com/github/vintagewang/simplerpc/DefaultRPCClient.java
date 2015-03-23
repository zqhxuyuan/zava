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

    private final ConcurrentHashMap<Integer, CallResponse> callRepTable = new ConcurrentHashMap<Integer, CallResponse>(1000000);

    private final ClientRPCProcessor clientRPCProcessor = new ClientRPCProcessor();

    public DefaultRPCClient() {
    }

    public boolean connect(InetSocketAddress remote, int cnt) {
        int i;
        for (i = 0; i < cnt && this.connect(remote); i++) {
        }
        return i == cnt;
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
                //建立客户端的Connection连接器. Connection封装了客户端的读写线程
                //注意: Connection对象的第二个参数是RPCProcessor. 对于客户端是这里的内部类, 对于服务端是自定义的业务逻辑实现类
                Connection c = new Connection(sc, this.clientRPCProcessor, null);
                this.connectionList.add(c);
            } else {
                sc.close();
            }
            return connected;
        } catch (IOException e) {
            if (sc != null) {
                try {
                    sc.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return false;
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

        //往建立连接的那个Connection放入请求信息. 注意一个Connection连接了客户端和服务端的数据发送和接收通道.
        //对于客户端和服务端,对一次连接的完成处理,都要创建属于自己的Connection.
        //客户端Connection的写线程发送的数据会被服务端Connection的读线程接收.
        //服务端Connection的写线程发送的结果会被客户端Connection的读线程接收.
        this.findConnection(id).putRequest(id, request);
        //计数器在5秒内如果没有响应,则超时. 即客户端在5秒内没有收到服务端的调用结果,认为调用超时.
        boolean waitOK = response.getCountDownLatch().await(5000, TimeUnit.MILLISECONDS);
        ByteBuffer result = null;
        if (waitOK) {
            result = response.getResponseBody();
        } else {
            System.out.println("timeout, reqId = " + id);
        }

        this.callRepTable.remove(id);
        return result;
    }

    //客户端的RPC处理器, 只是完成赋值Response的操作.
    class ClientRPCProcessor implements RPCProcessor {

        public byte[] process(int repId, ByteBuffer response) {
            //查找table中请求id对应的那个Response
            CallResponse cr = DefaultRPCClient.this.callRepTable.get(repId);
            if (cr != null) {
                //将RPC调用的响应结果设置到Response里
                cr.setReponseId(repId);
                cr.setResponseBody(response);
                //在Connection.this.rpcServerProcessor.process(reqId, request)中rpcServerProcessor实际上是建立Connection时传入的clientRPCProcessor
                //即服务端接收到客户端发送的调用请求后,开始回调这里, 计数器减少.
                cr.getCountDownLatch().countDown();
            }
            return null;
        }
    }

    //RPC调用结果
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
}
