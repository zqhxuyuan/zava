package com.github.atemerev.pms.listeners;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A delegate class for broadcasting message events to multiple listeners.
 * All implementations of MessageListenersAware interface should usually use
 * this delegate for actual implementation of events broadcasting.
 * This is a "poor-man JMS" publish/subscribe model, if you like it this way.
 * <p/>
 * This delegate implementation is (finally hoped to be) thread-safe.
 *
 * @author Alexander Temerev
 * @version $Id:$
 */

public class MessageListenerDelegate implements MessageListener, HasMessageListeners {

    private final List<MessageListener> listeners = new CopyOnWriteArrayList<MessageListener>();

    /**
     * Broadcast received message to registered listeners. If no listerers are
     * registered yet, do nothing.
     *
     * @param message Message to broadcast.
     */
    @Override
    public void processMessage(Object message) {
        synchronized (listeners) {
            if (listeners.size() > 0) {
                ListIterator<MessageListener> i = listeners.listIterator(listeners.size());
                while (i.hasPrevious()) {
                    i.previous().processMessage(message);
                }
            }
        }
    }

    /**
     * Get attached listeners list.
     *
     * @return Linked list of attached message listeners.
     */
    @Override
    public List<MessageListener> listeners() {
        return this.listeners;
    }

    /**
     * Set the list of listeners to already supplied argument. This is useful
     * for Spring integration.
     *
     * @param listeners List of message listeners.
     */
    public synchronized void setListeners(List<MessageListener> listeners) {
        List<MessageListener> reversed = new ArrayList<MessageListener>(listeners);
        Collections.reverse(reversed);
        this.listeners.addAll(reversed);
    }
}
