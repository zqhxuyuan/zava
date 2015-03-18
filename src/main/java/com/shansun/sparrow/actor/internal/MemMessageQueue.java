package com.shansun.sparrow.actor.internal;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * <b>基于内存的消息队列。</b> <br>
 * 每条处理线程会对应一个私有的消息队列。 <br>
 * 在发送消息的时候，会随机进入某一个私有队列中。
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-20
 */
public class MemMessageQueue implements MessageQueue<MessageWrapper> {

	/** 使用非阻塞队列保存消息 */
	private Queue<MessageWrapper>	msgQueue	= new ConcurrentLinkedQueue<MessageWrapper>();

	public static MemMessageQueue create() {
		return new MemMessageQueue();
	}

	@Override
	public MessageWrapper poll() {
		return msgQueue.poll();
	}

	@Override
	public boolean add(MessageWrapper msg) {
		return msgQueue.add(msg);
	}

	@Override
	public int size() {
		return msgQueue.size();
	}
}
