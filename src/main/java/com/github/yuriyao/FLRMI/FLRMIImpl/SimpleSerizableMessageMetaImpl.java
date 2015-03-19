package com.github.yuriyao.FLRMI.FLRMIImpl;


import com.github.yuriyao.FLRMI.FLRMISerizableCenter;
import com.github.yuriyao.FLRMI.FLRMIException;

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;


/**
 * 简单的序列化信元方法
 *
 * @author fengjing.yfj
 * @version $Id: SimpleSerizableMessageMetaImpl.java, v 0.1 2014年1月27日 下午3:01:10 fengjing.yfj Exp $
 */
public class SimpleSerizableMessageMetaImpl extends AbstractSerizableMessageMeta {
    /** mark,每一个序列化信元方法必须是唯一的 */
    private static final Integer                        MARK   = 0x199205;

    /** 用于单例 */
    private static final SimpleSerizableMessageMetaImpl simple = new SimpleSerizableMessageMetaImpl();

    //注册序列化方法到注册中心
    static {
        FLRMISerizableCenter.registerSerizableMessageMetaIfNotExist(simple);
    }

    /**
     * 创建一个单例对象
     *
     * @return 单例对象
     */
    public static SimpleSerizableMessageMetaImpl create() {
        return simple;
    }

    /**
     * 默认构造函数，私有化，实现单例
     */
    private SimpleSerizableMessageMetaImpl() {

    }

    @Override
    public Integer getMark() {
        return MARK;
    }

    @Override
    protected void writeTarget(ObjectOutput output, Object target) {
        try {
            output.writeObject(target);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FLRMIException("无法序列化目标对象[" + target + "]");
        }
    }

    @Override
    protected void writeMethod(ObjectOutput output, Method method) {
        try {
            //参数为空
            if (method == null) {
                output.writeObject(method);
                return;
            }
            //
            output.writeObject(method.getName());
            output.writeObject(method.getParameterTypes());
            output.writeObject(method.getDeclaringClass());
        } catch (Exception e) {
            e.printStackTrace();
            throw new FLRMIException("无法序列化方法对象[" + method + "]");
        }
    }

    @Override
    protected void writeParams(ObjectOutput output, Object[] params) {
        try {
            output.writeObject(params);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FLRMIException("无法序列化参数对象[" + params + "]");
        }
    }

    @Override
    protected Object readTarget(ObjectInput input) {
        try {
            return input.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new FLRMIException("无法读取目标对象");
        }
    }

    @Override
    protected Method readMethod(ObjectInput input) {
        try {
            //return (Method) input.readObject();
            Object object = input.readObject();
            if (object == null) {
                return null;
            }
            //读取方法名
            String name = (String) object;
            //读取参数列表
            Class<?>[] params = (Class<?>[]) input.readObject();
            //读取类
            Class<?> clz = (Class<?>) input.readObject();
            //获得方法
            return clz.getDeclaredMethod(name, params);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FLRMIException("无法读取方法对象");
        }
    }

    @Override
    protected Object[] readParams(ObjectInput input) {
        try {
            return (Object[]) input.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new FLRMIException("无法读取参数对象");
        }
    }

}