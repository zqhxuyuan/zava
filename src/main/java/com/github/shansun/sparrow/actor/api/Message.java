package com.github.shansun.sparrow.actor.api;


/**
 * 消息体
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-20
 */
public interface Message {

	/** 已经重做的次数 */
	public int	redoCount	= 0;

	/**
	 * 获取消息来源
	 */
	Actor getSource();

	/**
	 * 获取消息主题
	 */
	String getSubject();

	/**
	 * 消息内容
	 */
	Object getData();
}
