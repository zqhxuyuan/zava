package com.github.atemerev.pms.examples.routine;

import com.github.atemerev.pms.listeners.HasMessageListeners;
import com.github.atemerev.pms.listeners.MessageListener;
import com.github.atemerev.pms.listeners.MessageListenerDelegate;

import java.util.List;

/**
 * @author Alexander Temerev
 * @version $Id$
 */
public class Life implements MessageListener, HasMessageListeners {

    private MessageListenerDelegate delegate = new MessageListenerDelegate();

    // Delegated methods...

    @Override
    public void processMessage(Object message) {
        delegate.processMessage(message);
    }

    @Override
    public List<MessageListener> listeners() {
        return delegate.listeners();
    }

    // Some other methods which may be useful in Life...
    // ...
}
