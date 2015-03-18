package com.shansun.sparrow.actor.util;

import java.util.Collection;
import java.util.Map;

import com.shansun.sparrow.actor.api.Actor;
import com.shansun.sparrow.actor.api.Message;
import com.shansun.sparrow.actor.builder.ActorManagerBuilder;
import com.shansun.sparrow.actor.spi.ActorManager;



/**
 * Actor¹¤¾ß
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-23
 */
public final class Actors {
	final static ActorManager	actorManager	= ActorManagerBuilder.newBuilder().withThreadCount(10).build();

	static {
		actorManager.initialize();
	}

	public static Actor createActor(Class<? extends Actor> clazz) {
		return actorManager.createActor(clazz);
	}

	public static Actor createActor(Class<? extends Actor> clazz, Map<String, Object> options) {
		return actorManager.createActor(clazz, options);
	}

	public static boolean startActor(Actor actor) {
		return actorManager.startActor(actor);
	}

	public static boolean detachActor(String name) {
		return actorManager.detachActor(name);
	}

	public static boolean detachActor(Actor actor) {
		return actorManager.detachActor(actor);
	}

	public static Actor createAndStartActor(Class<? extends Actor> clazz) {
		return actorManager.createAndStartActor(clazz);
	}

	public static Actor createAndStartActor(Class<? extends Actor> clazz, Map<String, Object> options) {
		return actorManager.createAndStartActor(clazz, options);
	}

	public static int send(Message message, Actor sourceActor, String targetName) {
		return actorManager.send(message, sourceActor, targetName);
	}

	public static int send(Message message, Actor sourceActor, String[] targetNames) {
		return actorManager.send(message, sourceActor, targetNames);
	}

	public static int send(Message message, Actor sourceActor, Collection<String> targetNames) {
		return actorManager.send(message, sourceActor, targetNames);
	}

	public static int broadcast(Message message, Actor sourceActor) {
		return actorManager.broadcast(message, sourceActor);
	}

	public static void terminateAndWait() {
		actorManager.terminateAndWait();
	}

	public static void terminate() {
		actorManager.terminate();
	}
}
