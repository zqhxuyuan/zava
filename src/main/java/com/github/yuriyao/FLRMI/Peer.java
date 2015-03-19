package com.github.yuriyao.FLRMI;


/**
 * 进行通讯的对等端
 *
 * @author fengjing.yfj
 *
 */
public interface Peer {
    /**
     * 写信元
     *
     * @param messageMeta 信元
     */
    void writeMessageMeta(MessageMeta messageMeta);

    /**
     * 读取一个信元
     *
     * @return 返回信元
     */
    MessageMeta readMessageMeta();

    /**
     * 设置超时时间
     *
     * @param timeout 超时时间（ms）
     */
    void setTimeout(int timeout);

    /**
     * 关闭对等端
     */
    void shutdown();
}