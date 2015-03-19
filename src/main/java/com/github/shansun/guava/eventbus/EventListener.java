package com.github.shansun.guava.eventbus;

import com.google.common.eventbus.Subscribe;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-4
 */
public class EventListener {
    private int	lastMessage	= 0;

    @Subscribe
    public void listen(TestEvent event) {
        System.out.println("Received event.. [" + event.getMessage() + "]");

        lastMessage = event.getMessage();
    }

    public int getLastMessage() {
        return lastMessage;
    }
}