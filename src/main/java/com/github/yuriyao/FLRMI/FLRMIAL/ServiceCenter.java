package com.github.yuriyao.FLRMI.FLRMIAL;


/**
 * 服务中心，用于服务的注册和查找
 * 服务由三部分组成：服务前缀，服务标志以及实际的服务
 *
 * @author fengjing.yfj
 * @version $Id: ServiceCenter.java, v 0.1 2014年1月27日 下午5:20:30 fengjing.yfj Exp $
 */
public interface ServiceCenter {
    /**
     * 注册服务
     *
     * @param prefix 服务前缀
     * @param mark 服务标志
     * @param service 实际的服务
     */
    void registerService(String prefix, Object mark, Object service);

    /**
     * 如果服务不存在就才进行注册，不会覆盖原先的服务
     *
     * @param prefix
     * @param mark
     * @param Service
     */
    void registerServiceIfNotExist(String prefix, Object mark, Object Service);

    /**
     * 获取服务，查找服务的过程必须是前缀和mark都相同
     *
     * @param prefix
     * @param mark
     * @return
     */
    Object getService(String prefix, Object mark);

    /**
     * 注销所有服务前缀为prefix的服务
     *
     * @param prefix
     */
    void unregister(String prefix);

    /**
     * 注销前缀为prefix，标志为mark的服务
     *
     * @param prefix
     * @param mark
     */
    void unregister(String prefix, Object mark);
}