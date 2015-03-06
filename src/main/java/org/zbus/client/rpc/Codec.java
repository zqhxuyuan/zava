package org.zbus.client.rpc;

import org.zbus.common.remoting.Message;

/**
 * RPC请求应答编解码
 * 并提供强类型转换
 * 典型实现: JsonCodec
 * 
 * @author 洪磊明(rushmore)
 *
 */
public interface Codec {
	Message  encodeRequest(Request request); 
	Message  encodeResponse(Response response); 
	Request  decodeRequest(Message msg); 
	Response decodeResponse(Message msg);
	/**
	 * 强制转换类型，比如JsonCodec中将JSON格式的对象转换为强类型
	 * 这个过程在方法本地调用之前组装参数（强类型匹配）的时候使用
	 * 
	 * @param param 弱类型（JSON/XML化的内存对象），简单类型也支持
	 * @param targetType 目标类型
	 * @return
	 * @throws ClassNotFoundException
	 */
	Object normalize(Object param, Class<?> targetType) throws ClassNotFoundException;
}
