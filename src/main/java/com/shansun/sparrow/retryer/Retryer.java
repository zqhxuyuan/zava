package com.shansun.sparrow.retryer;

import com.google.common.base.Predicate;

/**
 * 重试支持
 * 
 * @author lanbo <br>
 * @version 1.0 <br>
 * @date 2012-8-28
 */
public interface Retryer {

	/**
	 * 重做任务
	 * 
	 * @param <T>
	 * @param retryableTask
	 * @return
	 * @throws Throwable
	 */
	<T> T executeWithRetry(Retryable<T> retryableTask) throws Throwable;

	/**
	 * 获取重试次数
	 * 
	 * @return
	 */
	long getTimes();

	/**
	 * 获取重试间隔，单位毫秒
	 * 
	 * @return
	 */
	long getInterval();

	/**
	 * 获取异常捕获条件
	 * 
	 * @return
	 */
	Predicate<Throwable> getThrowCondition();

	/**
	 * 获取返回值鉴别条件
	 * 
	 * @return
	 */
	Predicate<Object> getReturnCondition();
}
