package com.github.yuriyao.FLRMI.FLRMIImpl;


import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;

import com.github.yuriyao.FLRMI.FLRMISerizableCenter;
import com.thoughtworks.xstream.XStream;
import com.github.yuriyao.FLRMI.FLRMIException;

/**
 * xml方式进行序列化的工具
 * 这种方法实现的最好不要用于实际应用中，只要用于测试
 *
 * @author fengjing.yfj
 * @version $Id: XMLSerizableMessageMetaImpl.java, v 0.1 2014年1月27日 下午3:24:49 fengjing.yfj Exp $
 */
public class XMLSerizableMessageMetaImpl extends AbstractSerizableMessageMeta {
    /** mark */
    private static final Integer                     MARK                            = 0x1234123;

    /** 单例 */
    private static final XMLSerizableMessageMetaImpl XML_SERIZABLE_MESSAGE_META_IMPL = new XMLSerizableMessageMetaImpl();

    /** xml序列化工具 */
    private final XStream                            stream                          = new XStream();

    //将工具注册到注册中心
    static {
        FLRMISerizableCenter
                .registerSerizableMessageMetaIfNotExist(XML_SERIZABLE_MESSAGE_META_IMPL);
    }

    /**
     * 默认的构造函数
     */
    private XMLSerizableMessageMetaImpl() {

    }

    /**
     * 创建单例
     * @return
     */
    public static XMLSerizableMessageMetaImpl create() {
        return XML_SERIZABLE_MESSAGE_META_IMPL;
    }

    @Override
    public Integer getMark() {
        return MARK;
    }

    @Override
    protected void writeTarget(ObjectOutput output, Object target) {
        try {
            output.writeObject(stream.toXML(target));
        } catch (Exception e) {
            e.printStackTrace();
            throw new FLRMIException("无法序列化目标对象[" + target + "]");
        }
    }

    @Override
    protected void writeMethod(ObjectOutput output, Method method) {
        try {
            output.writeObject(stream.toXML(method));
        } catch (Exception e) {
            e.printStackTrace();
            throw new FLRMIException("无法序列化方法对象[" + method + "]");
        }
    }

    @Override
    protected void writeParams(ObjectOutput output, Object[] params) {
        try {
            output.writeObject(stream.toXML(params));
        } catch (Exception e) {
            e.printStackTrace();
            throw new FLRMIException("无法序列化参数对象[" + params + "]");
        }
    }

    @Override
    protected Object readTarget(ObjectInput input) {
        try {
            return stream.fromXML((String) input.readObject());
        } catch (Exception e) {
            e.printStackTrace();
            throw new FLRMIException("无法读取目标对象");
        }
    }

    @Override
    protected Method readMethod(ObjectInput input) {
        try {
            return (Method) stream.fromXML((String) input.readObject());
        } catch (Exception e) {
            e.printStackTrace();
            throw new FLRMIException("无法读取方法对象");
        }
    }

    @Override
    protected Object[] readParams(ObjectInput input) {
        try {
            return (Object[]) stream.fromXML((String) input.readObject());
        } catch (Exception e) {
            e.printStackTrace();
            throw new FLRMIException("无法读取参数对象");
        }
    }

}