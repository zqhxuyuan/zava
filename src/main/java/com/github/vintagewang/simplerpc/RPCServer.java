package com.github.vintagewang.simplerpc;

/**
 * 一个简单RPC Server
 *
 * @author vintage.wang@gmail.com  shijia.wxr@taobao.com
 */
public interface RPCServer {
    public void start();


    public void shutdown();


    public void registerProcessor(final RPCProcessor processor);
}
