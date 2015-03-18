package com.shansun.sparrow.actor.internal;

import org.slf4j.LoggerFactory;

import com.shansun.sparrow.actor.api.MessageRedcapCallback;
import com.shansun.sparrow.statistic.CountStatistic;
import com.shansun.sparrow.statistic.Statistics;

/**
 * 消息的搬运工，负责把消息取出来，丢给指定的Actor去处理
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-20
 */
public class MessageRedcapRunnable implements Runnable {

	private boolean				running;

	final MemMessageQueue		queue;

	final MessageRedcapCallback	callback;

	long						maximumSize	= 0;

	Thread						thisThread	= Thread.currentThread();

	volatile Object				lock		= new Object();

	CountStatistic				stat;

	public MessageRedcapRunnable(MessageRedcapCallback callback) {
		this(0, callback);
	}

	public MessageRedcapRunnable(long maximumSize, MessageRedcapCallback callback) {
		super();

		// 初始化后即允许执行线程逻辑
		setRunning(true);

		// 初始化本线程私有的消息队列
		queue = new MemMessageQueue();

		// 真正的业务逻辑，由外部去实现，一般放到Manager里
		this.callback = callback;
	}

	/**
	 * 添加消息
	 * 
	 * @param wrapper
	 */
	public boolean addMessage(MessageWrapper wrapper) {
		if (maximumSize > 0 && maximumSize <= queue.size()) {
			return false;
		}

		queue.add(wrapper);

		synchronized (lock) {
			lock.notify();
		}

		return true;
	}

	@Override
	public void run() {
		stat = Statistics.getCountStat(Thread.currentThread().getName() + "-Processed");

		thisThread = Thread.currentThread();

		synchronized (lock) {
			while (isRunning()) {
				MessageWrapper message = queue.poll();

				if (message == null) {
					try {
						LoggerFactory.getLogger(getClass()).info("[" + Thread.currentThread().getName() + "] is idle, there is no message.");

						lock.wait();
					} catch (InterruptedException e) {
						LoggerFactory.getLogger(getClass()).warn("Thread sleep was interrupted", e);
					}

					continue;
				}

				else {
					try {
						// 处理消息
						callback.execute(message);

						LoggerFactory.getLogger(getClass()).info("processing message [" + message + "]");
					} catch (Throwable e) {
						LoggerFactory.getLogger(getClass()).warn("processing message encounter error", e);
					} finally {
						stat.incr();
					}
				}
			}
		}

		processLeftMessage();
	}

	/**
	 * 处理掉剩余的消息
	 */
	private void processLeftMessage() {
		MessageWrapper msg = null;
		while ((msg = queue.poll()) != null) {
			callback.execute(msg);
		}
	}

	public boolean isRunning() {
		return running;
	}

	public MemMessageQueue getQueue() {
		return queue;
	}

	public MessageRedcapCallback getCallback() {
		return callback;
	}

	public long getMaximumSize() {
		return maximumSize;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
}
