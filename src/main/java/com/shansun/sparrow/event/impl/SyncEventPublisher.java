package com.shansun.sparrow.event.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.MapMaker;
import com.shansun.sparrow.event.Event;
import com.shansun.sparrow.event.EventListener;
import com.shansun.sparrow.event.EventPublisher;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-6-6
 */
public final class SyncEventPublisher implements EventPublisher {

	private final Listeners	listeners	= new Listeners();

	private static SyncEventPublisher instance = new SyncEventPublisher();
	
	private SyncEventPublisher() {
		// Singleton
	}
	
	public static SyncEventPublisher getInstance() {
		return instance;
	}
	
	@Override
	public void publish(final Event event) {
		checkNotNull(event);
		Iterator<EventListener> iterator = listeners.get(event.getClass());
		while (iterator.hasNext()) {
			iterator.next().onEvent(event);
		}
	}

	@Override
	public void register(final EventListener listener) {
		checkNotNull(listener);
		listeners.register(listener);
	}

	@Override
	public void unregister(final EventListener listener) {
		checkNotNull(listener);
		listeners.remove(listener);
	}

	public void unregisterAll() {
		listeners.clear();
	}

	static final class Listeners {
		private final Map<Class<?>, List<EventListener>>	invokers	= new MapMaker().makeMap();

		private void register(final EventListener listener) {
			@SuppressWarnings("rawtypes")
			Class[] eventClasses = listener.getHandledEventClasses();
			
			checkNotNull(eventClasses, "监听者必须指定感兴趣的事件类型!");

			for (Class<? extends Event> event : eventClasses) {
				List<EventListener> list = invokers.get(eventClasses);
				if (list == null) {
					list = new ArrayList<EventListener>();
				}

				list.add(listener);
				invokers.put(event, list);
			}
		}

		void remove(final EventListener listener) {
			for (Entry<Class<?>, List<EventListener>> entry : invokers.entrySet()) {
				entry.getValue().remove(listener);
			}
		}

		void clear() {
			invokers.clear();
		}

		public Iterator<EventListener> get(final Class<? extends Event> eventClass) {
			return invokers.get(eventClass).iterator();
		}
	}
}