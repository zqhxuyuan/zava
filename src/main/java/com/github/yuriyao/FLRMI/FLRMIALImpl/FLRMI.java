package com.github.yuriyao.FLRMI.FLRMIALImpl;


import java.io.IOException;
import java.lang.reflect.Proxy;

import com.github.yuriyao.FLRMI.FLRMIAL.FLRMITarget;

/**
 * 这是实际用于实际调用的编程接口
 *
 * @author fengjing.yfj
 * @version $Id: FLRMI.java, v 0.1 2014年1月28日 下午1:54:28 fengjing.yfj Exp $
 */
public abstract class FLRMI {

    /**
     * 获取FLRMI默认协议的服务
     *
     * @param serviceName
     * @param host
     * @param port
     * @param interfaces
     * @return
     */
    public static Object getFLRMIService(String serviceName, String host, int port,
                                         Class<?> interfaces[]) {
        //创建target
        FLRMITarget target = new FLRMITarget();
        target.setServiceName(serviceName);

        //调用服务
        return getService(target, host, port, interfaces);
    }
    public static Object getFLRMIService(String serviceName, Class<?> interfaces[]) {
        return getFLRMIService(serviceName,"localhost",FLRMIServer.DEFAULT_FL_RMI_PORT,interfaces);
    }


    /**
     * 这是一种通用的服务
     *
     * @param target
     * @param host
     * @param port
     * @param interfaces
     * @return
     */
    public static Object getService(Object target, String host, int port, Class<?> interfaces[]) {
        //创建代理
        FLRMIProxy proxy = new FLRMIProxy();
        //创建client
        FLRMIClient client = new FLRMIClient();
        client.setHost(host);
        client.setPort(port);

        //设置代理
        proxy.setClient(client);
        proxy.setTarget(target);
        return Proxy.newProxyInstance(FLRMIClient.class.getClassLoader(), interfaces, proxy);
    }

    /**
     * 注册FLRMI的服务
     *
     * @param serviceName 服务名
     * @param service 服务
     */
    public static void registerFLRMIService(String serviceName, Object service) {
        registerService(FLRMITarget.FL_RMI_PREFIX, serviceName, service);
    }

    /**
     * 通用的服务注册
     *
     * @param prefix 服务协议前缀
     * @param serviceName 服务名
     * @param service 服务
     */
    public static void registerService(String prefix, Object serviceName, Object service) {
        ServiceCenterImpl.create().registerService(prefix, serviceName, service);
    }

    /**
     * 开始服务
     *
     * @param server
     */
    public static void startServer(FLRMIServer server) {
        server.start();
    }

    /**
     * 使用默认端口开始服务
     *
     * @throws IOException
     */
    public static void startServer() throws IOException {
        startServer(FLRMIServer.DEFAULT_FL_RMI_PORT);
    }

    /**
     * 使用端口为port的开始服务
     *
     * @param port 端口号
     * @throws IOException
     */
    public static void startServer(int port) throws IOException {
        FLRMIServer server = new FLRMIServer(port);
        startServer(server);
    }
}