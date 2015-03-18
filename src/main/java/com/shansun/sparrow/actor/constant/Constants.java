package com.shansun.sparrow.actor.constant;

/**
 * 常量
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-21
 */
public interface Constants {

	/**
	 * 线程初始化条数，对应的值必须是Integer类型
	 */
	public static final String	OPTION_KEY_THREAD_COUNT		= "threadCount";

	/**
	 * 默认每个ActorManager启动的线程数
	 */
	public static final int		DEFAULT_ACTOR_THREAD_COUNT	= 5;
}
