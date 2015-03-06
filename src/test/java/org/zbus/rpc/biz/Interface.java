package org.zbus.rpc.biz;

import java.util.List;
import java.util.Map;

/**
 * 测试ZBUS RPC的透明度
 * 1) 接口类无任何ZBUS相关侵入
 * 2）覆盖各种复杂的入参与出参，异常, 目标是跟本地方法一样无任何限制
 * 
 * @author 洪磊明
 *
 */
public interface Interface{
  
	String getString(String name);
	
	String[] stringArray();
	
	int plus(int a, int b);
	
	User getUser(String name);
	
	Order getOrder();
	
	User[] getUsers();
	
	Object[] objectArray();

	int saveObjectArray(Object[] array);

	Map<String, Object> map(int value1);
	
	List<Map<String, Object>> listMap();
	
	Class<?> classTest(Class<?> inClass);
	
	void noReturn();
	
	void throwException();
	
	void throwUnkownException();
}