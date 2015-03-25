package com.github.wangxuehui.rpc.snrpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.List;

import com.github.wangxuehui.rpc.snrpc.SnRpcConnection;
import com.github.wangxuehui.rpc.snrpc.conf.SnRpcConfig;
import com.github.wangxuehui.rpc.snrpc.serializer.SnRpcRequest;
import com.github.wangxuehui.rpc.snrpc.serializer.SnRpcResponse;
import com.github.wangxuehui.rpc.snrpc.util.Sequence;
import com.github.wangxuehui.rpc.snrpc.SnRpcClient;
import com.github.wangxuehui.rpc.snrpc.SnRpcConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author skyim E-mail:wxh64788665@gmail.com
 */
public class CommonSnRpcClient implements SnRpcClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(SnRpcConfig.class);

	private SnRpcConnectionFactory snRpcConnectionFactory;
    private SnRpcInvoker invoker = new SnRpcInvoker();

	public CommonSnRpcClient(SnRpcConnectionFactory snRpcConnectionFactory) {
		if(null == snRpcConnectionFactory) {
			throw new NullPointerException("snRpcConnectionFactory is null ....");
		}
		this.snRpcConnectionFactory = snRpcConnectionFactory;
	}

    @SuppressWarnings("unchecked")
    @Override
    public <T> T proxy(Class<T> interfaceClass) throws Throwable {
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException(interfaceClass.getName() + " " + "is not an interface");
        }
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass }, invoker);
    }

	/**
	 * invoker
	 */
	private class SnRpcInvoker implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			String className = method.getDeclaringClass().getName();
			List<String> parameterTypes = new LinkedList<String>();
			for (Class<?> parameterType : method.getParameterTypes()) {
				parameterTypes.add(parameterType.getName());
			}
			String requestID = generateRequestID();
            //封装rpc调用请求
			SnRpcRequest request = new SnRpcRequest(requestID, className,
					method.getName(), parameterTypes.toArray(new String[0]), args);
            //这是客户端要收到的调用结果
			SnRpcResponse response = null;
			SnRpcConnection connection = null;
			try {
				connection = getConnection();
				response = connection.sendRequest(request);
			}catch(Throwable t){
				LOGGER.warn("send rpc request fail! request: <{}>", new Object[] { request }, t);
				throw new RuntimeException(t);
			}finally {
				recycle(connection);
			}
			if (response.getException() != null) {
				throw response.getException();
			} else {
				return response.getResult();
			}
		}

        //通过工厂类获得连接对象
		private SnRpcConnection getConnection() throws Throwable {
			return snRpcConnectionFactory.getConnection();
		}

		private String generateRequestID() {
			// TODO Auto-generated method stub
			return Sequence.next() + "";
		}
		

		/**
		 * recycle
		 * @param connection
		 */
		private void recycle(SnRpcConnection connection) {
			if (null != connection && null != snRpcConnectionFactory) {
				try {
					snRpcConnectionFactory.recycle(connection);
				} catch (Throwable t) {
					LOGGER.warn("recycle rpc connection fail!", t);
				}
			}
		}

	}


}
