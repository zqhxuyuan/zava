package com.github.yuriyao.FLRMI;


import java.io.Externalizable;
import java.lang.reflect.Method;

/**
 * rmi进行通讯的信元
 * 这是两端进行通信时数据的最小单元
 * 如果是在方法调用的时候，target，method，params对应于能够进行方法调用的最小单元:target.method(params)
 * 如果是获取执行结果，
 *
 * @author fengjing.yfj
 *
 */
public interface MessageMeta extends Externalizable {

    /**
     * 获取所要调用的方法
     *
     * @return
     */
    Method getMethod();

    /**
     * 获取参数
     *
     * @return
     */
    Object[] getParams();

    /**
     * 获取所要调用的对象
     *
     * @return
     */
    Object getTarget();

    /**
     * 设置方法
     *
     * @param method
     */
    void setMethod(Method method);

    /**
     * 设置参数
     *
     * @param objs
     */
    void setParams(Object[] objs);

    /**
     * 设置目标对象
     *
     * @param target
     */
    void setTarget(Object target);

}
