package com.github.yuriyao.FLRMI.FLRMIAL;

/**
 * 用来从target获取对应的服务
 *
 * @author fengjing.yfj
 * @version $Id: ServiceFinder.java, v 0.1 2014年1月28日 上午10:27:25 fengjing.yfj Exp $
 */
public interface ServiceFinder {
    /**
     * 是否可以处理这个target
     *
     * @param target 目标对象
     * @return 能够处理，就返回true；否则返回false
     */
    public boolean accept(Object target);

    /**
     * 获取服务，只有能够accept的服务，才会调用这个函数
     *
     * @param target 目标对象
     * @return 服务对象
     */
    public Object getService(Object target);
}