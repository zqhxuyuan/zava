package com.shansun.sparrow.event;

/**
 * 事件监听器
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-6-6
 */
public interface EventListener extends java.util.EventListener {

	/**
	 * 处理事件
	 * 
	 * @param e
	 * @return
	 */
	void onEvent(Event e);

	/**
	 * 该事件监听者对哪些事件感兴趣
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes") Class[] getHandledEventClasses();
}
