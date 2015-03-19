package com.github.vintagewang.simplerpc;

import java.nio.ByteBuffer;


/**
 * Server与Client的读事件处理
 *
 * @author vintage.wang@gmail.com  shijia.wxr@taobao.com
 */
public interface RPCProcessor {
    public byte[] process(final int upId, final ByteBuffer upstream);
}
