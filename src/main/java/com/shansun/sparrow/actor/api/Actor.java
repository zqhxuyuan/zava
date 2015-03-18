package com.shansun.sparrow.actor.api;

/**
 * <b>一个Actor是可重复执行的调度单元. <b><br>
 * Actor负责接收消息，并且执行他们请求的操作。<br>
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-20
 */
public interface Actor {

	/**
	 * 获取当前Actor的名称，该字段不能为空，否则无法找到对应的Manager去处理消息。
	 * 
	 * @return
	 */
	String getName();

	/**
	 * 获取当前Actor所在目录
	 * 
	 * @return
	 */
	String getCategory();

	/**
	 * 设置当前Actor为有效：该状态下，Actor可以正常接收消息，但是执行active()之前的消息将被丢弃。
	 */
	void activate();

	/**
	 * 设置当前Actor为无效：该状态下，Actor将丢弃所有接收到的消息。
	 */
	void deactivate();
}
