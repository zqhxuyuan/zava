package com.shansun.sparrow.actor.internal;

import org.slf4j.LoggerFactory;

import com.shansun.sparrow.actor.api.Message;
import com.shansun.sparrow.actor.spi.AbstractActor;




/**
 * 默认的无效的消息处理
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-20
 */
public class NoneMessageActor extends AbstractActor {

	@Override
	public String getName() {
		return "none-message";
	}

	@Override
	public String getCategory() {
		return "default";
	}

	@Override
	public boolean process(Message message) {
		LoggerFactory.getLogger(getClass()).trace("Ignored a message: " + message.getSubject());
		return true;
	}
}
