package com.github.vintagewang.simplerpc;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 服务端实现
 *
 * @author vintage.wang@gmail.com shijia.wxr@taobao.com
 */
public class DefaultRPCServer implements RPCServer {
    private final int listenPort;
    private SocketAddress socketAddressListen;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private final ThreadPoolExecutor executor;

    private RPCProcessor rpcServerProcessor;

    private List<Connection> connectionList = new LinkedList<Connection>();
    private final AcceptSocketService acceptSocketService = new AcceptSocketService();

    //创建RPC服务器
    public DefaultRPCServer(final int listenPort, final int minPoolSize, final int maxPoolSize) throws IOException {
        this.listenPort = listenPort;
        this.socketAddressListen = new InetSocketAddress(this.listenPort);
        this.serverSocketChannel = ServerSocketChannel.open();
        this.selector = Selector.open();
        this.serverSocketChannel.socket().setReuseAddress(true);
        this.serverSocketChannel.socket().bind(this.socketAddressListen);
        this.serverSocketChannel.configureBlocking(false);
        this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);

        this.executor = new ThreadPoolExecutor(minPoolSize, maxPoolSize, 60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(), new ThreadFactory() {
                private volatile long threadCnt = 0;

                public Thread newThread(Runnable r) {
                    return new Thread(r, "RPCHandleThreadPool_" + String.valueOf(this.threadCnt++));
                }
            }
        );
    }

    //注册业务处理逻辑实现类
    public void registerProcessor(RPCProcessor processor) {
        this.rpcServerProcessor = processor;
    }

    //启动
    public void start() {
        this.acceptSocketService.start();
    }

    //停止
    public void shutdown() {
        this.acceptSocketService.shutdown();

        for (Connection c : this.connectionList) {
            c.shutdown();
        }

        try {
            this.selector.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.serverSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //接受客户端连接的服务线程
    class AcceptSocketService extends ServiceThread {

        public void run() {
            System.out.println(this.getServiceName() + " service started");

            while (!this.isStoped()) {
                try {
                    DefaultRPCServer.this.selector.select(1000);
                    Set<SelectionKey> selected = DefaultRPCServer.this.selector.selectedKeys();
                    ArrayList<SelectionKey> selectedList = new ArrayList<SelectionKey>(selected);
                    Collections.shuffle(selectedList);

                    for (SelectionKey k : selectedList) {
                        if ((k.readyOps() & SelectionKey.OP_ACCEPT) != 0) {
                            SocketChannel sc = ((ServerSocketChannel) k.channel()).accept();
                            System.out.println("receive new connection, " + sc.socket().getRemoteSocketAddress());
                            //客户端连接上服务器,服务器就为客户端创建一个连接. 这个连接是每个请求都产生出来的.
                            //Per-Request-Per-Thread:服务器为每个客户端的连接请求都创建单独的线程来处理
                            //这样避免了服务器只用一个线程处理所有客户端请求造成的线程阻塞问题.随之而来的问题是服务端的线程管理.
                            Connection newConnection = new Connection(
                                            sc,  //客户端连接, 数据传送的基础通道
                                            DefaultRPCServer.this.rpcServerProcessor, //客户端传入的业务处理逻辑实现类
                                            DefaultRPCServer.this.executor); //要交给服务器的线程池进行管理

                            // if (DefaultRPCServer.this.clientConnection !=
                            // null) {
                            // System.out.println("close old client connection, "
                            // +
                            // DefaultRPCServer.this.clientConnection.getSocketChannel().socket()
                            // .getRemoteSocketAddress());
                            // DefaultRPCServer.this.clientConnection.shutdown();
                            // }

                            //添加到服务器维护的连接列表中, 并启动Connection线程开始处理
                            DefaultRPCServer.this.connectionList.add(newConnection);
                            newConnection.start();
                        }
                        // TODO， CLOSE SOCKET
                        else {
                            System.out.println("Unexpected ops in select " + k.readyOps());
                        }
                    }

                    selected.clear();
                }
                catch (Exception e) {
                    System.out.println(this.getServiceName() + " service has exception.");
                    System.out.println(e.getMessage());
                }
            }

            System.out.println(this.getServiceName() + " service end");
        }


        @Override
        public String getServiceName() {
            return AcceptSocketService.class.getSimpleName();
        }
    }
}
