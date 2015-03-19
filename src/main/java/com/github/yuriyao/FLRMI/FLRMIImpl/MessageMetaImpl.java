package com.github.yuriyao.FLRMI.FLRMIImpl;


import com.github.yuriyao.FLRMI.FLRMIException;
import com.github.yuriyao.FLRMI.FLRMISerizableCenter;
import com.github.yuriyao.FLRMI.MessageMeta;
import com.github.yuriyao.FLRMI.SerizableMessageMeta;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;

/**
 * 实现一个信元
 *
 * @author fengjing.yfj
 *
 */
public class MessageMetaImpl implements MessageMeta {

    /** 目标对象,对用时代表调用对象，返回时代表返回值对象，或者抛出的异常对象 */
    private Object                target;

    /** 方法对象 */
    private Method                method;

    /** 参数对象 */
    private Object                params[];

    /** 序列化方法 */
    private SerizableMessageMeta  serizableMessageMeta;

    /** 默认的序列化方法列表 */
    private static final String[] SE_MESSAGE_METAS = new String[] {
            "com.github.yuriyao.FLRMI.FLRMIImpl.XMLSerizableMessageMetaImpl",
            "com.github.yuriyao.FLRMI.FLRMIImpl.SimpleSerizableMessageMetaImpl" };

    //加载默认的序列化方法
    static {
        //加载所有的序列化方法
        //保证这些序列化方法可以被注册到序列化注册中心
        for (String name : SE_MESSAGE_METAS) {
            try {
                Class.forName(name);
            } catch (ClassNotFoundException e) {
                //忽略异常
            }
        }
    }

    /**
     * 默认的构造函数
     */
    public MessageMetaImpl() {
    }

    /**
     * 构造函数
     *
     * @param serizableMessageMeta 序列化工具
     */
    public MessageMetaImpl(SerizableMessageMeta serizableMessageMeta) {
        this.serizableMessageMeta = serizableMessageMeta;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        if (this.serizableMessageMeta == null) {
            this.serizableMessageMeta = SimpleSerizableMessageMetaImpl.create();
        }
        //输出mark
        out.writeInt(serizableMessageMeta.getMark());

        //输出实际的内容
        serizableMessageMeta.write(out, this);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        //读取mark
        Integer mark = in.readInt();
        //获取对应的反序列化工具，因为这在从流中读取的时候是没有序列化方法的，需要
        //通过序列化方法的注册中心查找对应的序列化工具
        this.serizableMessageMeta = FLRMISerizableCenter.getSerizableMessageMeta(mark);
        if (this.serizableMessageMeta == null) {
            throw new FLRMIException("无法获取mark为[" + mark.intValue()
                    + "]的反序列化工具, 请手动注册到序列化工具中心[FLRMISerizableCenter]");
        }
        //获取mark对应的
        serizableMessageMeta.read(in, this);
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getParams() {
        return params;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public void setMethod(Method method) {
        this.method = method;
    }

    @Override
    public void setParams(Object[] objs) {
        this.params = objs;
    }

    @Override
    public void setTarget(Object target) {
        this.target = target;
    }

}