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

import java.util.Arrays;


/**
 * RPC请求格式，模块+方法+参数列表，附加请求二进制的编码格式
 * 
 * @author 洪磊明(rushmore)
 *
 */
public class Request{ 
	private String module = ""; //模块标识
	private String method;      //远程方法
	private Object[] params;    //参数列表
	private String[] paramTypes;
	private String encoding = "UTF-8";
	
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public Object[] getParams() {
		return params;
	}
	public void setParams(Object[] params) {
		this.params = params;
	}
	public String[] getParamTypes() {
		return paramTypes;
	}
	public void setParamTypes(String[] paramTypes) {
		this.paramTypes = paramTypes;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public void assignParamTypes(Class<?>... types){
		this.paramTypes = new String[types.length];
		for(int i=0; i<types.length; i++){
			this.paramTypes[i]= types[i].getCanonicalName(); 
		}
	}
	
	public static void normalize(Request req){
		if(req.module == null){
			req.module = "";
		}
		if(req.params == null){
			req.params = new Object[0];
		}
	}
	
	@Override
	public String toString() {
		return "Request [module=" + module + ", method=" + method + ", params="
				+ Arrays.toString(params) + ", paramTypes="
				+ Arrays.toString(paramTypes) + ", encoding=" + encoding + "]";
	}
	
}
