package com.github.atemerev.pms.listeners;

import java.util.List;

/**
 * Implement this interface in every object intending to broadcast
 * incoming messages to additional listeners. Provide the listeners
 * list. Handle the broadcasting.
 * <p/>
 * There is a helper class to which you can delegate this task---
 * a MessageListenerDelegate. You should delegate listeners() and
 * processMessage(...) methods to it, saving lines of code and brain
 * cells.
 *
 * @author Alexander Temerev
 * @version $Id$
 * @see MessageListenerDelegate
 */
public interface HasMessageListeners {

    /**
     * Get a list of attached message listeners (preferably a
     * LinkedList).
     *
     * @return List of message listeners.
     */
    public List<MessageListener> listeners();
}
