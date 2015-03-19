package com.github.vintagewang.simplerpc;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;


/**
 * 客户端接口
 *
 * @author vintage.wang@gmail.com shijia.wxr@taobao.com
 */
public interface RPCClient {
    public void start();


    public void shutdown();


    public boolean connect(final InetSocketAddress remote, final int cnt);


    public ByteBuffer call(final byte[] req) throws InterruptedException;
}
