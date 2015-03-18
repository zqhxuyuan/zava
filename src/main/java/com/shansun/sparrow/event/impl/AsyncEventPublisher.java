package com.shansun.sparrow.event.impl;

import com.shansun.sparrow.event.Event;
import com.shansun.sparrow.event.EventListener;
import com.shansun.sparrow.event.EventPublisher;

/**
 * 暂时为实现，可以选择使用Actor方式
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-6-6
 */
public class AsyncEventPublisher implements EventPublisher {

	@Override
	public void publish(Event event) {
		// TODO Auto-generated method stub
	}

	@Override
	public void register(EventListener listener) {
		// TODO Auto-generated method stub
	}

	@Override
	public void unregister(EventListener listener) {
		// TODO Auto-generated method stub
	}

	@Override
	public void unregisterAll() {
		// TODO Auto-generated method stub
	}

}
