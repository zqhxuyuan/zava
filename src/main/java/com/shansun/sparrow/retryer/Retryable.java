package com.shansun.sparrow.retryer;

/**
 * 可重试的执行体
 * 
 * @author lanbo <br>
 * @version 1.0 <br>
 * @date 2012-8-28
 */
public interface Retryable<T> {

	T execute() throws Throwable;
}
