package com.github.atemerev.hollywood.future;

import com.github.atemerev.pms.listeners.MessageListener;

import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * @author Alexander Temerev, Alexander Kuklev
 * @version $Id$
 */
public abstract class MementoPromiseProxy<T> extends MementoAbstractPromise<T> {
    private Promise<T> target;

    MementoPromiseProxy(Promise<T> target) {
        this.target = target;
    }

    public T get() {
        return target.get();
    }

    public T get(long timeout) throws TimeoutException {
        return target.get(timeout);
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return target.cancel(mayInterruptIfRunning);
    }

    public boolean isCancelled() {
        return target.isCancelled();
    }

    public boolean isDone() {
        return target.isDone();
    }

    public List<MessageListener> listeners() {
        return target.listeners();
    }
}

