/************************************************************************
 *  Copyright (c) 2011-2012 HONG LEIMING.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 ***************************************************************************/
package org.zbus.client.rpc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.zbus.client.service.ServiceHandler;
import org.zbus.common.logging.Logger;
import org.zbus.common.logging.LoggerFactory;
import org.zbus.common.remoting.Message;

class MethodInstance{
	public Method method;
	public Object instance;
	
	public MethodInstance(Method method, Object instance){
		this.method = method;
		this.instance = instance;
	}

	@Override
	public String toString() {
		return "MethodInstance [method=" + method + ", instance=" + instance + "]";
	} 
}

public class RpcServiceHandler implements ServiceHandler {
	private static final Logger log = LoggerFactory.getLogger(RpcServiceHandler.class); 
	private static final Codec codec = new JsonCodec();//TODO configurable
	
	private Map<String,MethodInstance> methods = new HashMap<String, MethodInstance>();
	
	public RpcServiceHandler(Object... services){
		addModule(services);
	}
	public void addModule(Object... services){
		for(Object obj : services){
			for(Class<?> intf : obj.getClass().getInterfaces()){
				addModule(intf.getSimpleName(), obj);
				addModule(intf.getCanonicalName(), obj);
			}
			addModule(obj.getClass().getSimpleName(), obj);
			addModule(obj.getClass().getCanonicalName(), obj);
		}
	}
	
	public void addModule(String module, Object... services){
		for(Object service: services){
			this.initCommandTable(module, service);
		}
	}
	
	private void initCommandTable(String module, Object service){
		try {  
			Method [] methods = service.getClass().getMethods(); 
			for (Method m : methods) { 
				String method = m.getName();
				Remote cmd = m.getAnnotation(Remote.class);
				if(cmd != null){ 
					method = cmd.id();
					if(cmd.exclude()) continue;
					if("".equals(method)){
						method = m.getName();
					}  
				}
				
				String paramMD5 = ""; 
				Class<?>[] paramTypes = m.getParameterTypes();
				StringBuilder sb = new StringBuilder();
				for(int i=0;i<paramTypes.length;i++){ 
					sb.append(paramTypes[i].getCanonicalName());
				}
				paramMD5 = sb.toString();
				String key = module + ":" + method+":"+paramMD5; 
				String key2 = module + ":" + method;
				if(this.methods.containsKey(key)){
					log.error(key + " duplicated"); 
				} else {
					log.debug("register "+service.getClass().getSimpleName()+"\t" + key);
					log.debug("register "+service.getClass().getSimpleName()+"\t"  + key2);
				}
				m.setAccessible(true);
				MethodInstance mi = new MethodInstance(m, service);
				this.methods.put(key, mi);  
				this.methods.put(key2, mi);  
			}  
		} catch (SecurityException e) {
			log.error(e.getMessage(), e);
		}   
	}
	
	private static boolean isBlank(String value){
		return (value == null || "".equals(value.trim()));
	}
	
	private Request decodeRequest(Message msg){
		Request req = codec.decodeRequest(msg);
		Request.normalize(req); //json未设置好的module或者params标准化
		if(isBlank(req.getMethod())){
			throw new IllegalArgumentException("missing method name");
		}
		return req;
	}
	
	private MethodInstance findMethod(Request req){ 
		String paramTypesMD5 = "";
		if(req.getParamTypes() != null){
			for(String type : req.getParamTypes()){
				paramTypesMD5 += type;
			}
		}
		String module = req.getModule(); 
		String method = req.getMethod();
		String key = module+":"+method+":"+paramTypesMD5;//支持语言多态
		String key2 = module+":"+method;
		if(this.methods.containsKey(key)){
			return this.methods.get(key); 
		} else {  
			if(this.methods.containsKey(key2)){
				return this.methods.get(key2); 
			} else {
				String errorMsg = String.format("%s:%s not found, module may not set, or wrong", module, method);
				throw new IllegalArgumentException(errorMsg);
			}
		}
	}
	
	private void checkParamTypes(MethodInstance target, Request req){
		Class<?>[] targetParamTypes = target.method.getParameterTypes();
		
		if(targetParamTypes.length !=  req.getParams().length){
			String requiredParamTypeString = "";
			for(int i=0;i<targetParamTypes.length;i++){
				Class<?> paramType = targetParamTypes[i]; 
				requiredParamTypeString += paramType.getName();
				if(i<targetParamTypes.length-1){
					requiredParamTypeString += ", ";
				}
			}
			Object[] params = req.getParams();
			String gotParamsString = "";
			for(int i=0;i<params.length;i++){ 
				gotParamsString += params[i];
				if(i<params.length-1){
					gotParamsString += ", ";
				}
			}
			String errorMsg = String.format("Method:%s(%s), called with %s(%s)", 
					target.method.getName(), requiredParamTypeString, target.method.getName(), gotParamsString);
			throw new IllegalArgumentException(errorMsg);
		}
	}
	
	@Override
	public Message handleRequest(Message msg){  
		Response resp = new Response();
		try {
			Request req = decodeRequest(msg);
			MethodInstance target = findMethod(req);
			checkParamTypes(target, req);
			
			Class<?>[] targetParamTypes = target.method.getParameterTypes();
			Object[] invokeParams = new Object[targetParamTypes.length];  
			Object[] reqParams = req.getParams();
			for(int i=0; i<targetParamTypes.length; i++){ 
				invokeParams[i] = codec.normalize(reqParams[i], targetParamTypes[i]);
			} 
			Object result = target.method.invoke(target.instance, invokeParams);
			resp.setResult(result); 
			resp.setEncoding(msg.getEncoding());
		} catch (InvocationTargetException e) { 
			resp.setError(e.getTargetException());
		} catch (Throwable e) { 
			resp.setError(e);
		} 
		try{
			return codec.encodeResponse(resp);
		} catch (Throwable e) {
			log.error(e.getMessage(), e.getCause());
		} 
		return null; //should not here
	}
}
