package com.github.zangxiaoqiang.io.nio;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public interface MessageHandler {
	public void processMessage(SocketChannel channel, ByteBuffer message);
}
