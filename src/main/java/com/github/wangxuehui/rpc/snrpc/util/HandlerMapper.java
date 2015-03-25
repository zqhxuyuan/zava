package com.github.wangxuehui.rpc.snrpc.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author skyim E-mail:wxh64788665@gmail.com
 * 类说明
 */
public class HandlerMapper {

	public static Map<String,Object> getHandlerMap(Object... handlers){
		if(null==handlers||handlers.length==0){
			throw new IllegalArgumentException("handlers not provided");
		}
		HashMap<String,Object> handlerMap = new HashMap<String,Object>();
        //handlers传入的是实现类
		for(Object handler : handlers){
            //实现类实现了哪些接口?
			Class<?>[] interfaces = handler.getClass().getInterfaces();
			for(Class<?> iface : interfaces){
                //接口名称
				String interfaceName = iface.getName();
				if(ignore(interfaceName)){
					continue;
				}
                //接口名称-->实现类
				if(null != handlerMap.put(interfaceName, handler)){
					throw new IllegalArgumentException("more than one handler for the interface [" + interfaceName + "]");
				}
			}
			
		}
		return handlerMap;
	}
	
	private static boolean ignore(String interfaceName){
		if(interfaceName.startsWith("java.")){
			return true;
		}
		if(interfaceName.startsWith("javax.")){
			return true;
		}
		if(interfaceName.startsWith("sun.")){
			return true;
		}
		if(interfaceName.startsWith("com.sun.")){
			return true;
		}
		return false;
	}
}
