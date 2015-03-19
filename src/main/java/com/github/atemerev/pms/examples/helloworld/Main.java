package com.github.atemerev.pms.examples.helloworld;

import com.github.atemerev.pms.listeners.dispatch.DispatchListener;

/**
 * Here we create a DispatchListener—the main workhorse of the framework.
 It accepts any object acting as a message (in our case, Morning and Evening).
 The DispatchListener takes our message handler as argument and is now
 ready to process messages. The instances of Morning and Evening are passed
 via processMessage method and routed to the appropriate handler method.

 * @author Alexander Temerev
 * @version $Id$
 */
public class Main {
    public static void main(String[] args) {
        //Event Handler
        HelloWorldHandler handler = new HelloWorldHandler();
        //Let Dispatcher DO ALL INTERNAL
        DispatchListener listener = new DispatchListener(handler);

        //Event Comming
        listener.processMessage(new Morning());
        listener.processMessage(new Evening());
    }
}
