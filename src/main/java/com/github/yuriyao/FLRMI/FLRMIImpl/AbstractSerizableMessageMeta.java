package com.github.yuriyao.FLRMI.FLRMIImpl;


import com.github.yuriyao.FLRMI.FLRMIException;
import com.github.yuriyao.FLRMI.MessageMeta;
import com.github.yuriyao.FLRMI.SerizableMessageMeta;

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;

/**
 * 抽象一个通用的序列化信元的工具
 *
 * @author fengjing.yfj
 * @version $Id: AbstractSerizableMessageMeta.java, v 0.1 2014年1月27日 上午11:23:29 fengjing.yfj Exp $
 */
public abstract class AbstractSerizableMessageMeta implements SerizableMessageMeta {

    @Override
    public void write(ObjectOutput output, MessageMeta messageMeta) {
        try {
            //序列化各个部分
            writeTarget(output, messageMeta.getTarget());
            writeMethod(output, messageMeta.getMethod());
            writeParams(output, messageMeta.getParams());
        } catch (FLRMIException e) {
            //重新抛出
            throw e;
        } catch (Exception e) {
            throw new FLRMIException("序列化对象失败，序列化方法[" + this + "],序列化信元对象[" + messageMeta + "]");
        }
    }

    @Override
    public void read(ObjectInput input, MessageMeta messageMeta) {
        //传递的参数必须非空，否则出现异常直接有系统抛出空指针异常

        try {
            messageMeta.setTarget(readTarget(input));
            messageMeta.setMethod(readMethod(input));
            messageMeta.setParams(readParams(input));
        } catch (FLRMIException e) {
            throw e;
        } catch (Exception e) {
        }
    }

    /**
     * 序列化target对象
     *
     * @param output 序列化流
     * @param target 目标
     */
    protected abstract void writeTarget(ObjectOutput output, Object target);

    /**
     * 序列化所要调用的方法
     *
     * @param output  序列化流
     * @param method 调用的方法
     */
    protected abstract void writeMethod(ObjectOutput output, Method method);

    /**
     * 序列化参数
     *
     * @param output 序列化流
     * @param params 参数
     */
    protected abstract void writeParams(ObjectOutput output, Object[] params);

    /**
     * 读取目标对象
     *
     * @param input 序列化输入流
     * @return 反序列化好的target
     */
    protected abstract Object readTarget(ObjectInput input);

    /**
     * 读取方法对象
     *
     * @param input 序列化输入流
     * @return 反序列化好的方法
     */
    protected abstract Method readMethod(ObjectInput input);

    /**
     * 读取参数对象
     *
     * @param input 序列化输入流
     * @return 反序列化好的参数
     */
    protected abstract Object[] readParams(ObjectInput input);
}