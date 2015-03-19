package com.github.yuriyao.FLRMI.FLRMIALImpl;


import com.github.yuriyao.FLRMI.FLRMIException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 客户端的代理
 *
 * @author fengjing.yfj
 * @version $Id: FLRMIProxy.java, v 0.1 2014年1月28日 下午1:45:05 fengjing.yfj Exp $
 */
public class FLRMIProxy implements InvocationHandler {

    /** 目标对象 */
    private Object      target;

    /** 客户端 */
    private FLRMIClient client;

    public FLRMIProxy() {
    }

    public FLRMIProxy(Object target) {
        this.target = target;
    }

    public FLRMIProxy(Object target, FLRMIClient client) {
        this.target = target;
        this.client = client;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //参数检查
        if (client == null) {
            throw new FLRMIException("还没有设置客户端，没有办法进行连接");
        }

        //调用服务端
        return client.invoke(target, method, args, -1);
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public FLRMIClient getClient() {
        return client;
    }

    public void setClient(FLRMIClient client) {
        this.client = client;
    }

}