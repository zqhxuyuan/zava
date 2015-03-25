package com.github.wangxuehui.rpc.snrpc.server;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.github.wangxuehui.rpc.snrpc.conf.RpcService;
import com.github.wangxuehui.rpc.snrpc.conf.SnRpcConfig;
import com.github.wangxuehui.rpc.snrpc.serializer.SnRpcRequest;
import com.github.wangxuehui.rpc.snrpc.serializer.SnRpcResponse;
import com.github.wangxuehui.rpc.snrpc.util.ReflectionCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

/**
 * @author skyim E-mail:wxh64788665@gmail.com
 */
public class SnNettyRpcServerHandler extends SimpleChannelInboundHandler<SnRpcRequest> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SnNettyRpcServer.class);

	private final static Map<String, RpcService> serviceMap = new HashMap<String, RpcService>();
	private Map<String, Object> handlersMap;

    //handlersMap是初始化Netty服务器时,将SnNettyRpcServer的handlersMap设进来
	public SnNettyRpcServerHandler(Map<String, Object> handlersMap) {
		this.handlersMap = handlersMap;
	}

    //在解析xml文件后,将xml的服务名和RpcService服务类添加到serviceMap
	public static void putService(RpcService service) {
		if (null != service) {
			serviceMap.put(service.getName(), service);
		}
	}

	public static RpcService getServiceMap(String serviceName) {
		return serviceMap.get(serviceName);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, SnRpcRequest msg) throws Exception {
		try {
            //参数msg是rpc client发送的rpc请求信息
			SnRpcRequest request = (SnRpcRequest) msg;
            //最终我们要将rpc的调用结果封装成RpcResponse对象传给客户端
			SnRpcResponse response = new SnRpcResponse(request.getRequestID());

			try {
				LOGGER.debug(request+"");
				Object result = handler(request);
                //完成rpc的调用,将返回结果设置到Response对象中
				response.setResult(result);
			} catch (Throwable t) {
				LOGGER.warn("handler rpc request fail! request:<{}>", new Object[] { request }, t);
				response.setException(t);
			}

		    final ChannelFuture f = ctx.writeAndFlush(response); //将调用结果写给客户端
		    f.addListener(new ChannelFutureListener() {
	            @Override
	            public void operationComplete(ChannelFuture future) {
	                assert f == future;
	                ctx.close();
	            }
	        });
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}

    //SnRpcRequest封装了客户端要调用的rpc信息. 实现了序列化接口.
    //根据客户端发送的请求信息,完成rpc在服务端的调用
    private Object handler(SnRpcRequest request) throws Throwable {
        if (SnRpcConfig.getInstance().getDevMod()) {
            StatisticsService.reportBeforeInvoke(request);
        }
        String className = request.getClassName();
        String[] classNameSplits = className.split("\\.");
        String serviceName = classNameSplits[classNameSplits.length - 1];
        //从serviceMap中根据服务名获得RpcService, 这个RpcService封装了服务类的元数据.
        //注意:不是服务类! 服务类是在handlersMap中. 存放的是接口名和其实现类.
        RpcService rpcService = getServiceMap(serviceName);
        if (null == rpcService) {
            throw new NullPointerException("server interface config is null");
        }

        //实现类的类型
        Class<?> clazz = rpcService.getRpcImplementor().getProcessorClass();
        Method method = ReflectionCache.getMethod(clazz.getName(),
                request.getMethodName(), request.getParameterTypes());
        Object[] parameters = request.getParameters();

        // get handler 取得实现类对象.
        Object handler = handlersMap.get(request.getClassName());
        // invoke 反射
        Object result = method.invoke(handler, parameters);
        return result;
    }
}
