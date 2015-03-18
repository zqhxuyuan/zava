package com.shansun.sparrow.actor.internal;

import com.shansun.sparrow.actor.api.Message;

/**
 * 消息队列
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-8-15
 */
public interface MessageQueue<T extends Message> {

	/**
	 * 移除并返回队列头部的消息。如果队列为空，则返回null。【无阻塞】
	 */
	public T poll();

	/**
	 * 添加一个消息到队列尾部，并返回true。如果队列已满，则返回false。【无阻塞】
	 * 
	 * @param msg
	 * @return
	 */
	public boolean add(T msg);

	/**
	 * 获取队列长度
	 * 
	 * @return
	 */
	public int size();
}
