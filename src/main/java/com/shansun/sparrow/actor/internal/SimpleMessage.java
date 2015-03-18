package com.shansun.sparrow.actor.internal;

import com.shansun.sparrow.actor.api.Actor;
import com.shansun.sparrow.actor.api.Message;



/**
 * 简单的消息
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-23
 */
public class SimpleMessage implements Message {
	static final Actor	unknownSource	= new UnknownActor();

	Actor				source			= unknownSource;

	String				subject			= "default-subject";

	Object				data			= null;

	public SimpleMessage(Object data) {
		super();
		this.data = data;
	}

	public SimpleMessage(String subject, Object data) {
		super();
		this.subject = subject;
		this.data = data;
	}

	public SimpleMessage(Actor source, String subject, Object data) {
		super();
		this.source = source;
		this.subject = subject;
		this.data = data;
	}

	@Override
	public Actor getSource() {
		return source;
	}

	@Override
	public String getSubject() {
		return subject;
	}

	@Override
	public Object getData() {
		return data;
	}

	public void setSource(Actor source) {
		this.source = source;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
