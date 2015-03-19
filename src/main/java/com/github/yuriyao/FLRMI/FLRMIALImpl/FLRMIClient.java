package com.github.yuriyao.FLRMI.FLRMIALImpl;


import com.github.yuriyao.FLRMI.FLRMIException;
import com.github.yuriyao.FLRMI.FLRMIImpl.MessageMetaImpl;
import com.github.yuriyao.FLRMI.FLRMIImpl.TCPPeerImpl;
import com.github.yuriyao.FLRMI.MessageMeta;

import java.lang.reflect.Method;
import java.net.Socket;


/**
 * FLRMI 的客户端
 *
 * @author fengjing.yfj
 * @version $Id: FLRMIClient.java, v 0.1 2014年1月28日 上午11:43:44 fengjing.yfj Exp $
 */
public class FLRMIClient {

    /** 服务端的主机 */
    private String host = "localhost";

    /** 服务端的端口 */
    private int    port = FLRMIServer.DEFAULT_FL_RMI_PORT;

    /**
     * 调用远程方法
     *
     * @param target
     * @param method
     * @param params
     * @param timeout 超时时间
     * @return
     */
    public Object invoke(Object target, Method method, Object params[], int timeout) {
        try {
            //创建客户端套接字
            Socket socket = new Socket(host, port);
            //创建对等端
            TCPPeerImpl peer = new TCPPeerImpl();
            peer.setSocket(socket);
            //设置超时时间
            if (timeout > 0) {
                peer.setTimeout(timeout);
            }
            //创建请求信元
            MessageMetaImpl messageMeta = new MessageMetaImpl();
            messageMeta.setMethod(method);
            messageMeta.setTarget(target);
            messageMeta.setParams(params);
            //请求服务端
            peer.writeMessageMeta(messageMeta);
            //获得响应
            MessageMeta result = peer.readMessageMeta();
            //发生异常
            if (result.getTarget() instanceof FLRMIException) {
                throw (FLRMIException) result.getTarget();
            }
            //关闭对等端
            peer.shutdown();
            //返回响应
            return result.getTarget();
        } catch (FLRMIException e) {
            throw e;
        } catch (Exception e) {
            throw new FLRMIException(e);
        }

    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}