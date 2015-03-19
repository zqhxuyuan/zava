package com.github.shansun.guava.eventbus;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-4
 */
public class DeadEventListener {
    boolean	notReceived	= false;

    @Subscribe
    public void listen(DeadEvent event) {
        System.out.println("Received dead event... [" + event + "]");

        notReceived = true;
    }

    public boolean isNotReceived() {
        return notReceived;
    }
}