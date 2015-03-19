package com.github.atemerev.pms.examples.helloworld;

import com.github.atemerev.pms.listeners.dispatch.DispatchListener;

/**
 * @author Alexander Temerev
 * @version $Id$
 */
public class Main {
    public static void main(String[] args) {
        HelloWorldHandler handler = new HelloWorldHandler();
        DispatchListener listener = new DispatchListener(handler);
        listener.processMessage(new Morning());
        listener.processMessage(new Evening());
    }
}
