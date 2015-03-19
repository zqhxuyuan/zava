package com.github.yuriyao.FLRMI.FLRMIALImpl;


import com.github.yuriyao.FLRMI.FLRMIAL.TargetProtocol;

/**
 * 目标协议解析的实现
 *
 * @author fengjing.yfj
 * @version $Id: TargetProtocolImpl.java, v 0.1 2014年1月27日 下午7:31:18 fengjing.yfj Exp $
 */
public class TargetProtocolImpl implements TargetProtocol {

    /** 目标字符串 */
    private String target;

    /** 协议前缀 */
    private String prefix;

    /** 主机名 */
    private String host;

    /** 端口号 */
    private int    port;

    /** 路径 */
    private String path;

    public TargetProtocolImpl(String target) {
        this.target = target;

        //进行协议解析
        resolve();
    }

    /**
     * 协议解析，进行协议分解
     */
    private void resolve() {
        if (target.startsWith("/")) {

        }
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getPath() {
        return path;
    }

}