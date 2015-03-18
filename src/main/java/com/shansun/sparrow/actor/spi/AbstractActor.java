package com.shansun.sparrow.actor.spi;

import java.util.concurrent.atomic.AtomicBoolean;

import com.shansun.sparrow.actor.api.Actor;
import com.shansun.sparrow.actor.api.Message;


/**
 * Actor的抽象实现
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-20
 */
public abstract class AbstractActor implements Actor {

	/** 当前Actor是否有效 */
	volatile AtomicBoolean	active	= new AtomicBoolean(false);

	protected ActorManager	manager;

	public ActorManager getManager() {
		return manager;
	}

	public void setManager(ActorManager manager) {
		if (this.manager != null && manager != null) {
			throw new IllegalStateException("Cannot change manager of attached actor");
		}
		this.manager = manager;
	}

	/**
	 * 处理消息
	 * 
	 * @param message
	 * @return
	 */
	public abstract boolean process(Message message);

	@Override
	public void activate() {
		active.set(true);
	}

	@Override
	public void deactivate() {
		active.set(false);
	}

	public boolean isActive() {
		return active.get();
	}

}
