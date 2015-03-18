package com.shansun.sparrow.actor.spi;

import java.util.Collection;
import java.util.Map;

import com.shansun.sparrow.actor.api.Actor;
import com.shansun.sparrow.actor.api.Message;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-20
 */
public interface ActorManager {

	Actor createActor(Class<? extends Actor> clazz);

	Actor createActor(Class<? extends Actor> clazz, Map<String, Object> options);

	boolean startActor(Actor actor);

	boolean detachActor(String name);

	boolean detachActor(Actor actor);

	Actor createAndStartActor(Class<? extends Actor> clazz);

	Actor createAndStartActor(Class<? extends Actor> clazz, Map<String, Object> options);

	/**
	 * 发送消息给指定Actor
	 * 
	 * @param message
	 * @param sourceActor
	 *            消息来源Actor，如果为null，则会自动设置为UnknownActor.
	 * @param targetName
	 * @return
	 */
	int send(Message message, Actor sourceActor, String targetName);

	/**
	 * 发送消息给指定Actor
	 * 
	 * @param message
	 * @param sourceActor
	 *            消息来源Actor，如果为null，则会自动设置为UnknownActor.
	 * @param targetNames
	 * @return
	 */
	int send(Message message, Actor sourceActor, String[] targetNames);

	/**
	 * 发送消息给指定Actor
	 * 
	 * @param message
	 * @param sourceActor
	 *            消息来源Actor，如果为null，则会自动设置为UnknownActor.
	 * @param targetNames
	 * @return
	 */
	int send(Message message, Actor sourceActor, Collection<String> targetNames);

	/**
	 * 广播消息给所有Actor
	 * 
	 * @param message
	 * @param sourceActor
	 *            消息来源Actor，如果为null，则会自动设置为UnknownActor.
	 * @return
	 */
	int broadcast(Message message, Actor sourceActor);

	/**
	 * 初始化
	 */
	void initialize();

	/**
	 * 待参数的初始化
	 * @param options
	 */
	void initialize(Map<String, Object> options);

	/**
	 * 停止Actor引擎
	 */
	void terminateAndWait();

	/**
	 * 停止Actor引擎
	 */
	void terminate();
}
