package com.github.wangxuehui.rpc.snrpc.client;

import java.net.InetSocketAddress;

import com.github.wangxuehui.rpc.snrpc.SnRpcConnection;
import com.github.wangxuehui.rpc.snrpc.serializer.SnRpcRequestEncoder;
import com.github.wangxuehui.rpc.snrpc.serializer.SnRpcResponse;
import com.github.wangxuehui.rpc.snrpc.serializer.SnRpcRequest;
import com.github.wangxuehui.rpc.snrpc.serializer.SnRpcResponseDecoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author skyim E-mail:wxh64788665@gmail.com
 */
public class SnNettyRpcConnection extends SimpleChannelInboundHandler<SnRpcResponse> implements SnRpcConnection {

	private InetSocketAddress inetAddr;
	private SnRpcResponse response;
	private Object obj = new Object();


	public SnNettyRpcConnection(String host, int port) {
		this.inetAddr = new InetSocketAddress(host, port);
	}

	public SnRpcResponse sendRequest(final SnRpcRequest request) throws Throwable {
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap(); // (1)
			b.group(workerGroup); // (2)
			b.channel(NioSocketChannel.class); // (3)
			b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
                    //依次处理连接信息SnNettyRpcConnection,Request编码,Response解码
					ch.pipeline().addLast(new SnRpcResponseDecoder());
					ch.pipeline().addLast(new SnRpcRequestEncoder());
					ch.pipeline().addLast(SnNettyRpcConnection.this);
				}
			});

			Channel ch = b.connect(inetAddr).sync().channel();
            //发送rpc调用请求
			ch.writeAndFlush(request);
            //等待收到rpc调用结果
			waitForResponse();
            //执行到这里一定是收到了服务端的响应
			SnRpcResponse resp = this.response;
			if (resp != null) {
				ch.closeFuture().sync();
			}
			return resp;
		} finally {
			workerGroup.shutdownGracefully();
		}
	}

	public void waitForResponse() {
		synchronized (obj) {
			try {
                //如果channelRead0收到消息,则说明服务端的响应数据过来了,不需要再等待了,
                //执行上面方法sendRequest中waitForResponse()后面的语句
				obj.wait();
			} catch (InterruptedException e) {
			}
		}
	}

    //如果读取到服务端发送的消息,那么这个消息就是服务端发送的rpc调用结果的响应信息
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, SnRpcResponse msg) throws Exception {
		response = msg;
		synchronized (obj) {
            //触发obj通知有结果了
			obj.notifyAll();
		}
	}

}
