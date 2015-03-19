package com.github.yuriyao.FLRMI.FLRMIAL;


import java.util.ArrayList;
import java.util.List;

/**
 * 目标服务查找器的注册中心
 *
 * @author fengjing.yfj
 * @version $Id: TargetFinderCenter.java, v 0.1 2014年1月28日 上午10:41:40 fengjing.yfj Exp $
 */
public abstract class TargetFinderCenter {
    /** 所有的服务查找器 */
    private static final List<ServiceFinder> finders           = new ArrayList<ServiceFinder>();

    /** 内置的服务查找器 */
    private static final String              INTENAL_FINDERS[] = { "com.github.yuriyao.FLRMI.FLRMIALImpl.FLRMIServiceFinder" };

    //注册所有的内置的查找器
    static {
        for (String string : INTENAL_FINDERS) {
            try {
                Class.forName(string);
            } catch (Exception e) {
                //忽略异常
            }
        }
    }

    /**
     * 注册服务查找器
     *
     * @param finder
     */
    public static synchronized void registerFinder(ServiceFinder finder) {
        if (!finders.contains(finder)) {
            finders.add(finder);
        }
    }

    /**
     * 注销服务查找器
     *
     * @param finder 查找器
     */
    public static synchronized void unregisterFinder(ServiceFinder finder) {
        finders.remove(finder);
    }

    /**
     * 根据目标对象查找对应的服务查找器
     *
     * @param target 目标对象
     * @return 获取对应的服务查找器
     */
    public static synchronized ServiceFinder getFinder(Object target) {
        for (ServiceFinder finder : finders) {
            if (finder.accept(target)) {
                return finder;
            }
        }
        return null;
    }

}