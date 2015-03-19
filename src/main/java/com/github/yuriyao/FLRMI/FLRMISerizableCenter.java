package com.github.yuriyao.FLRMI;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 序列化工具注册中心
 *
 * @author fengjing.yfj
 * @version $Id: FLRMISerizableCenter.java, v 0.1 2014年1月27日 上午10:52:03 fengjing.yfj Exp $
 */
public abstract class FLRMISerizableCenter {
    /** 所有注册的序列化方法 */
    private static final Map<Integer, SerizableMessageMeta> serizables = new ConcurrentHashMap<Integer, SerizableMessageMeta>();

    /**
     * 注册信元的序列化的函数
     *
     * @param smm 序列化函数
     * @param override 存在相同的mark的是否进行覆盖
     */
    public static synchronized void registerSerizableMessageMeta(SerizableMessageMeta smm,
                                                                 boolean override) {
        if (smm == null) {
            throw new FLRMIException("序列化信元中心注册空指针");
        }
        //检查是否已经存在相同的信元
        if (!override) {
            if (serizables.get(smm.getMark()) != null) {
                throw new FLRMIException("序列化信元工具已经存在");
            }
        }
        //进行注册
        serizables.put(smm.getMark(), smm);
    }

    /**
     * 采用覆盖的方法注册信元序列化工具
     *
     * @param smm 信元序列化工具
     */
    public static void registerSerizableMessageMeta(SerizableMessageMeta smm) {
        registerSerizableMessageMeta(smm, true);
    }

    /**
     * 只有当不存在指定的序列化工具才会进行注册
     *
     * @param smm 信元序列化工具
     */
    public static synchronized void registerSerizableMessageMetaIfNotExist(SerizableMessageMeta smm) {
        if (smm == null) {
            throw new FLRMIException("序列化信元中心注册空指针");
        }
        if (serizables.get(smm.getMark()) == null) {
            serizables.put(smm.getMark(), smm);
        }
    }

    /**
     * 获取指定mark的序列化信元工具
     *
     * @param mark 信元序列化的标志
     * @return
     */
    public static synchronized SerizableMessageMeta getSerizableMessageMeta(Integer mark) {
        return serizables.get(mark);
    }
}