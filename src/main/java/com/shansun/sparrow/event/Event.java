package com.shansun.sparrow.event;

import java.util.EventObject;
import java.util.UUID;

/**
 * 事件体
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-6-6
 */
public abstract class Event extends EventObject {
	private static final long	serialVersionUID	= 3787024242851558433L;

	/**
	 * 事件ID，能唯一标识一次事件请求
	 */
	private final String		eventId;

	public Event(Object source) {
		super(source);
		this.eventId = UUID.randomUUID().toString();
	}

	public String getEventId() {
		return eventId;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Event)) {
			return false;
		}

		final Event event = (Event) o;

		if (source != null ? !source.equals(event.source) : event.source != null) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return (source != null ? source.hashCode() : 0);
	}

	@Override
	public String toString() {
		return getClass().getName() + "[eventId=" + eventId + ", source=" + source + "]";
	}
}
