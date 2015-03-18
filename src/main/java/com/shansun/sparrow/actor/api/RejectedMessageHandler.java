package com.shansun.sparrow.actor.api;

import com.shansun.sparrow.actor.internal.MessageWrapper;
import com.shansun.sparrow.actor.internal.ThreadWrapper;


/**
 * 拒绝消息的策略
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-24
 */
public interface RejectedMessageHandler {

	/**
	 * 拒绝接受消息，可能是消息队列已满，或者Actor出现异常等
	 * 
	 * @param message
	 *            被拒绝的消息
	 * @param thread
	 *            提出拒绝的线程
	 */
	public void reject(MessageWrapper message, ThreadWrapper thread);
}
