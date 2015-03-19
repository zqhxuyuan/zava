package com.github.yuriyao.FLRMI.FLRMIAL;


/**
 * 协议解析
 * 协议格式 prefix://host:port/path
 *
 * @author fengjing.yfj
 * @version $Id: TargetProtocol.java, v 0.1 2014年1月27日 下午7:26:30 fengjing.yfj Exp $
 */
public interface TargetProtocol {

    /**
     * 获取协议的前缀
     *
     * @return
     */
    public String getPrefix();

    /**
     * 获取目标主机
     *
     * @return
     */
    public String getHost();

    /**
     * 获取协议端口
     *
     * @return
     */
    public int getPort();

    /**
     * 获取路径
     *
     * @return
     */
    public String getPath();
}