package com.github.wangxuehui.rpc.snrpc.serializer;

import java.io.Serializable;

/**
 * @author skyim E-mail:wxh64788665@gmail.com
 * 类说明
 */
public class SnRpcRequest implements Serializable{
	private static final long serialVersionUID = -4700088399107076960L;

	private String requestID;

    //请求消息包含了要调用的类名,方法名,参数值,参数类型
	private String className;
	private String methodName;
	private String[] parameterTypes;
	private Object[] parameters;
	
	public SnRpcRequest(){
	}

	public SnRpcRequest(String className, String methodName, String[] parameterTypes, Object[] parameters) {
		super();
		this.className = className;
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		this.parameters = parameters;
	}

	public SnRpcRequest(String requestID, String className, String methodName,
                        String[] parameterTypes, Object[] parameters) {
		super();
		this.requestID = requestID;
		this.className = className;
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		this.parameters = parameters;
	}

	public String getRequestID() {
		return requestID;
	}

	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(String[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
}
