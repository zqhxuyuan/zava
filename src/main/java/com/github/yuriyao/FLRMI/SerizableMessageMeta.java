package com.github.yuriyao.FLRMI;


import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * 进行信元序列化时使用的序列化方法
 *
 * @author fengjing.yfj
 *
 */
public interface SerizableMessageMeta {

    /**
     * 获取序列化的标志
     *
     * @return
     */
    public Integer getMark();

    /**
     * 进行序列化，不用序列化mark，调用的会自动进行序列化mark
     *
     * @param output 序列化的流
     * @param messageMeta 信元
     */
    public void write(ObjectOutput output, MessageMeta messageMeta);

    /**
     * 反序列化，注意反序列化mark已经完成
     *
     * @param input 反序列化的流
     * @return 反序列化好的信元
     */
    public void read(ObjectInput input, MessageMeta messageMeta);
}