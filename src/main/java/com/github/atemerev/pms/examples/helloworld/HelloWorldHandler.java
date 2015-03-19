package com.github.atemerev.pms.examples.helloworld;

import com.github.atemerev.pms.Listener;

/**
 * @author Alexander Temerev
 * @version $Id$
 */
public class HelloWorldHandler {
    // EventListener: when Event:Morning come, get up
    @Listener
    void $(Morning morning) {
        System.out.println(morning);
        System.out.println("Hello, world!");
    }

    // EventListener: when Event:Evening come, sleep
    @Listener void $(Evening evening) {
        System.out.println(evening);
        System.out.println("Goodbye, world!");
    }
}
