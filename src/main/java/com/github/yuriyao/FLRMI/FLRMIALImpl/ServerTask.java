package com.github.yuriyao.FLRMI.FLRMIALImpl;


import java.net.Socket;

import com.github.yuriyao.FLRMI.FLRMIImpl.MessageMetaImpl;
import com.github.yuriyao.FLRMI.FLRMIException;
import com.github.yuriyao.FLRMI.MessageMeta;
import com.github.yuriyao.FLRMI.FLRMIAL.ServiceFinder;
import com.github.yuriyao.FLRMI.FLRMIAL.TargetFinderCenter;
import com.github.yuriyao.FLRMI.FLRMIImpl.TCPPeerImpl;

/**
 * 服务端的工作线程
 *
 * @author fengjing.yfj
 * @version $Id: ServerTesk.java, v 0.1 2014年1月27日 下午7:16:23 fengjing.yfj Exp $
 */
public class ServerTask implements Runnable {

    /** 和客户端的连接 */
    private Socket socket;

    public ServerTask(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            //存放返回结果
            Object result = null;
            TCPPeerImpl peer = new TCPPeerImpl();
            try {

                peer.setSocket(socket);

                //读取信元
                MessageMeta messageMeta = peer.readMessageMeta();

                //根据信元获取服务
                ServiceFinder finder = TargetFinderCenter.getFinder(messageMeta.getTarget());
                //服务查找器还没有注册
                if (finder == null) {
                    throw new FLRMIException("在服务查找中心TargetFindCenter没有获取到[" + messageMeta.getTarget() + "]的服务查找器");
                }
                //获取服务
                Object service = finder.getService(messageMeta.getTarget());
                if (service == null) {
                    throw new FLRMIException("没有获取到目标[" + messageMeta.getTarget() + "]的服务");
                }
                //没有可以调用的服务方法
                if (messageMeta.getMethod() == null) {
                    throw new FLRMIException("");
                }

                //经过一系列的坎坷，终于可以进行服务的调用了
                result = messageMeta.getMethod().invoke(service, messageMeta.getParams());

            } catch (Exception e) {
                e.printStackTrace();
                result = new FLRMIException(e);
            }

            //给客户端返回结果
            MessageMetaImpl messageMetaImpl = new MessageMetaImpl();
            messageMetaImpl.setTarget(result);
            //发送信息
            peer.writeMessageMeta(messageMetaImpl);
            //关闭对等点
            peer.shutdown();

        } catch (Exception e) {
            //忽略异常
            e.printStackTrace();
        }
    }
}