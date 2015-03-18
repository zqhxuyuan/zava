package com.shansun.sparrow.actor.api;

import com.shansun.sparrow.actor.internal.MessageWrapper;

/**
 * 消息处理线程获取到消息后，将处理逻辑交给callback去做
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-21
 */
public interface MessageRedcapCallback {

	/**
	 * 处理消息
	 * 
	 * @param message
	 */
	void execute(MessageWrapper message);
}
