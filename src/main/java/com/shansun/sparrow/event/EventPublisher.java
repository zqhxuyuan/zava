package com.shansun.sparrow.event;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-6-6
 */
public interface EventPublisher {
	/**
	 * 发布事件
	 * 
	 * @param event
	 */
	void publish(Event event);

	/**
	 * 注册监听者
	 * 
	 * @param listener
	 */
	void register(EventListener listener);

	/**
	 * 反注册监听者
	 * 
	 * @param listener
	 */
	void unregister(EventListener listener);

	/**
	 * 反注册所有监听者
	 */
	void unregisterAll();
}
