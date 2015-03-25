package com.github.wangxuehui.rpc.snrpc.server;

import com.github.wangxuehui.rpc.snrpc.conf.SnRpcConfig;
import com.github.wangxuehui.rpc.snrpc.serializer.SnRpcRequestDecoder;
import com.github.wangxuehui.rpc.snrpc.serializer.SnRpcResponseEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Map;

import com.github.wangxuehui.rpc.snrpc.SnRpcServer;
import com.github.wangxuehui.rpc.snrpc.util.HandlerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author skyim E-mail:wxh64788665@gmail.com
 * 类说明
 */
public class SnNettyRpcServer implements SnRpcServer{
	private static final Logger LOGGER = LoggerFactory.getLogger(SnNettyRpcServer.class);
	
	private SnRpcConfig snRpcConfig = SnRpcConfig.getInstance();
	private Map<String,Object> handlersMap;
	private int httpListenPort;

    //传递自定义Handler对象, 加入到handlerMap中: key是接口名称,value是接口的实现类
	public SnNettyRpcServer(Object...  handlers){
		snRpcConfig.loadProperties("snrpcserver.properties");
		this.handlersMap = HandlerMapper.getHandlerMap(handlers);
	}
	
	public SnNettyRpcServer(String fileName,Object... handlers){
		snRpcConfig.loadProperties(fileName);
		this.handlersMap = HandlerMapper.getHandlerMap(handlers);
	}
	
	//启动服务器
	public void start() throws Throwable {
		initServerInfo();
		initHttpBootstrap();
	}

    private void initServerInfo() {
        httpListenPort = snRpcConfig.getHttpPort();
        //解析XML文件. 在构造服务器的时候,已经将接口和对应的实现类注册到了handlerMap中.为什么还要使用xml解析??
        //解析后的结果是将xml中服务名和RpcService对象放入SnNettyRpcServerHandler的serviceMap中.
        new ParseXmlToService().parse();
    }

    //启动Netty的HTTP服务
	private void initHttpBootstrap() throws InterruptedException {
		if(SnRpcConfig.getInstance().getDevMod()){
			StatisticsService.reportPerformance();
		}
		LOGGER.info("init HTTP Bootstrap ..........");
		
		EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) // (3)
             .childHandler(new ChannelInitializer<SocketChannel>() { // (4) Handler
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     //服务端的顺序是从Request解码,Response编码,再到Handler
                	 ch.pipeline().addLast(new SnRpcRequestDecoder());
                	 ch.pipeline().addLast(new SnRpcResponseEncoder());
                     ch.pipeline().addLast(new SnNettyRpcServerHandler(handlersMap));
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 128)          // (5)
             .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            if(!checkPortConfig(httpListenPort)){
            	throw new IllegalStateException("port: " + httpListenPort + " already in use");
            }
            
            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(httpListenPort).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
	}

	private boolean checkPortConfig(int listenPort) {
		if(listenPort < 0 || listenPort > 65536){
			throw new IllegalArgumentException("Invalid start port: "+listenPort);
		}
		ServerSocket ss = null;
		DatagramSocket ds =null;
		try {
			ss = new ServerSocket(listenPort);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(listenPort);
			ds.setReuseAddress(true);
			return true;
		}catch(IOException e){
			
		}finally {
			if(ds!=null){
				ds.close();
			}
			if(ss!=null){
				try {
					ss.close();
				}catch(IOException e){
					
				}
			}
		}
		
		return false;
	}
}
