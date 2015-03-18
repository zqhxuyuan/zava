package com.shansun.sparrow.retryer;

import java.util.concurrent.TimeUnit;

/**
 * 重试执行任务，真正的重试处理逻辑
 * 
 * @author lanbo <br>
 * @version 1.0 <br>
 * @date 2012-8-28
 */
public class RetryableTask<T> implements Retryable<T> {
	private final Retryable<T>	retryable;
	private long				left;
	private Retryer				retryer;

	public RetryableTask(Retryable<T> retryable, Retryer retryer) {
		this.retryable = retryable;
		this.retryer = retryer;
		this.left = retryer.getTimes();
	}

	@Override
	public T execute() throws Throwable {
		try {
			T returnObject = retryable.execute();
			if (!exhausted() && retryer.getReturnCondition().apply(returnObject)) {
				return retry();
			}
			return returnObject;
		} catch (Throwable e) {
			if (!exhausted() && retryer.getThrowCondition().apply(e)) {
				return retry();
			}
			throw e;
		}
	}

	private T retry() throws Throwable {
		// 只有超过10ms的才真正去睡眠等待
		if (retryer.getInterval() > 10) {
			TimeUnit.MILLISECONDS.sleep(retryer.getInterval());
		}
		left--;
		return execute();
	}

	private boolean exhausted() {
		return left == 0;
	}
}
