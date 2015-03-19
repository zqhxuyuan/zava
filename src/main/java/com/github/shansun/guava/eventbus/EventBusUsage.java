package com.github.shansun.guava.eventbus;

import com.google.common.eventbus.EventBus;

/**
 * 事件总线用法
 *
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-4
 */
public class EventBusUsage {

    /**
     * @param args
     */
    public static void main(String[] args) {
        simplyUsage();

        multiListener();

        deadEvent();
    }

    static void simplyUsage() {
        // given
        EventBus bus = new EventBus("test");
        EventListener listener = new EventListener();
        bus.register(listener);

        // when
        System.out.println("Posting event...");
        bus.post(new TestEvent(200));
        System.out.println("Posted event.");

        // then
        System.out.println(listener.getLastMessage());
    }

    static void multiListener() {
        EventBus bus = new EventBus("test");
        MultiListener listener = new MultiListener();
        bus.register(listener);

        System.out.println("Posting event [100]...");
        bus.post(100);
        System.out.println("Posted event [100].");
        System.out.println("Posting event [900]...");
        bus.post(900L);
        System.out.println("Posted event [900].");

        System.out.println(listener.getLastInteger());
        System.out.println(listener.getLastLong());
    }

    static void deadEvent() {
        EventBus bus = new EventBus("test");
        DeadEventListener listener = new DeadEventListener();
        bus.register(listener);

        bus.post(new TestEvent(300));

        System.out.println(listener.notReceived);
    }
}