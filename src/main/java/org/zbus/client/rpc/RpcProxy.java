package org.zbus.client.rpc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.zbus.common.logging.Logger;
import org.zbus.common.logging.LoggerFactory;
 
/**
 * 
 * 
 * @author 洪磊明(rushmore)
 *
 */
public class RpcProxy {
	private static final Logger log = LoggerFactory.getLogger(RpcProxy.class);
	private static Constructor<RpcInvoker> rpcInvokerCtor;
	private static Map<String,RpcInvoker> rpcInvokerCache = new ConcurrentHashMap<String, RpcInvoker>();
	
	static {
		try {
			rpcInvokerCtor = RpcInvoker.class.getConstructor(new Class[] {Rpc.class });
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}  
	
	@SuppressWarnings("unchecked")
	public static <T> T getService(Class<T> api, RpcConfig config) throws Exception {   
		String mq = config.getMq(); 
		if(mq == null){
			throw new IllegalArgumentException("Missing argument mq");
		}
		String module = config.getModule();
		if(module == null ||module.trim().length()==0){
			module = api.getSimpleName();
			config.setModule(module);
		}
			
		String encoding = config.getEncoding();
		int timeout = config.getTimeout();
		String accessToken = config.getAccessToken();
		String registerToken = config.getRegisterToken();
		
		String cacheKey = String.format(
				"mq=%s&&module=%s&&encoding=%s&&timeout=%d&&accessToken=%s&&registerToken=%s",
				mq, module, encoding, timeout, accessToken,registerToken);
		
		RpcInvoker rpcInvoker = rpcInvokerCache.get(cacheKey);
		Class<T>[] interfaces = new Class[] { api };
		if(rpcInvoker == null){
			Rpc rpc = new Rpc(config);
			rpcInvoker = rpcInvokerCtor.newInstance(rpc); 
			rpcInvokerCache.put(cacheKey, rpcInvoker); 
		}
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		return (T) Proxy.newProxyInstance(classLoader, interfaces, rpcInvoker);
	} 
}

class RpcInvoker implements InvocationHandler {  
	private Rpc rpc; 
	private static final Object REMOTE_METHOD_CALL = new Object();

	public RpcInvoker(Rpc rpc) {
		this.rpc = rpc;
	}
	
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if(args == null){
			args = new Object[0];
		}
		Object value = handleLocalMethod(proxy, method, args);
		if (value != REMOTE_METHOD_CALL) return value; 
		Class<?> returnType = method.getReturnType(); 
		return rpc.invokeSyncWithType(returnType, method.getName(),method.getParameterTypes(), args);
	}

	protected Object handleLocalMethod(Object proxy, Method method,
			Object[] args) throws Throwable {
		String methodName = method.getName();
		Class<?>[] params = method.getParameterTypes();

		if (methodName.equals("equals") && params.length == 1
				&& params[0].equals(Object.class)) {
			Object value0 = args[0];
			if (value0 == null || !Proxy.isProxyClass(value0.getClass()))
				return new Boolean(false);
			RpcInvoker handler = (RpcInvoker) Proxy.getInvocationHandler(value0);
			return new Boolean(this.rpc.equals(handler.rpc));
		} else if (methodName.equals("hashCode") && params.length == 0) {
			return new Integer(this.rpc.hashCode());
		} else if (methodName.equals("toString") && params.length == 0) {
			return "RpcInvoker[" + this.rpc + "]";
		}
		return REMOTE_METHOD_CALL;
	} 
}