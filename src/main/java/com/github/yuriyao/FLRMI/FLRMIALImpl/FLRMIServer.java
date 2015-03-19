package com.github.yuriyao.FLRMI.FLRMIALImpl;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.github.yuriyao.FLRMI.FLRMIException;

/**
 * 这是服务端，这个类是线程不安全的，应该在单线程中调用或者做好并发控制
 *
 * @author fengjing.yfj
 * @version $Id: FLRMIServer.java, v 0.1 2014年1月27日 下午6:57:46 fengjing.yfj Exp $
 */
public class FLRMIServer {

    /** 服务端套接字 */
    private final ServerSocket serverSocket;

    /** 默认的端口 */
    public static final int    DEFAULT_FL_RMI_PORT = 1223;

    /** 默认线程池 */
    private ExecutorService    threadPool          = null;

    /** 运行状态 */
    private volatile boolean   active              = false;

    /** 线程池的线程数量 */
    private int                threadNumber        = 20;

    public FLRMIServer() throws IOException {
        this(DEFAULT_FL_RMI_PORT);
    }

    public FLRMIServer(int port) throws IOException {
        this(port, 20);
    }

    public FLRMIServer(int port, int backLen) throws IOException {
        serverSocket = new ServerSocket(port, backLen);
    }

    /**
     * 使用线程池
     */
    public void useThreadPool() {
        if (active) {
            throw new FLRMIException("服务已经启动，无法启动线程池");
        }
        threadPool = Executors.newFixedThreadPool(threadNumber);
    }

    /**
     * 开启服务
     */
    public void start() {
        active = true;
        while (active) {
            try {
                Socket socket = serverSocket.accept();
                //启动新的线程，进行服务
                if (threadPool == null) {
                    new Thread(new ServerTask(socket)).start();
                }
                //使用线程池
                else {
                    threadPool.execute(new ServerTask(socket));
                }
                //关闭客户端线程
                /* try {
                     socket.close();
                 } catch (IOException e) {
                     // 忽略异常
                     e.printStackTrace();
                 }*/
            } catch (IOException e) {
                active = false;
                e.printStackTrace();
                throw new FLRMIException("服务器出现异常");
            }
        }
    }

    /**
     * 停止服务
     */
    public void stop() {
        active = false;
        if (threadPool != null) {
            threadPool.shutdown();
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            //忽略异常
        }
    }
}