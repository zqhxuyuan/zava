package com.github.atemerev.hollywood.future;

import com.github.atemerev.pms.listeners.MessageListener;
import com.github.atemerev.pms.listeners.MessageListenerDelegate;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * <code>AbstractPromise</code> provides a convenient implementation of <code>Promise</code> methods for appending
 * continuations.
 *
 * @author Alexander Temerev, Alexander Kuklev
 * @version $Id$
 */
public abstract class AbstractPromise<T> implements Promise<T> {

    boolean isDone = false;
    boolean isCancelled = false;
    List<Runnable> continuations = new LinkedList<Runnable>();

    protected MessageListenerDelegate delegate = new MessageListenerDelegate();

    /**
     * Get list of attached listeners. Here you can attach listeners for the following events:
     * <ul>
     * <li><code>CompletedEvent</code>: if the task has been successfully completed.</li>
     * <li><code>CancelledEvent</code>: if the task has been cancelled.</li>
     * </ul>
     *
     * @return List of attached listeners.
     */
    public List<MessageListener> listeners() {
        return delegate.listeners();
    }

    // Status getters

    /**
     * Returns <tt>true</tt> if this task completed.
     *
     * Completion may be due to normal termination, an exception, or
     * cancellation -- in all of these cases, this method will return
     * <tt>true</tt>.
     *
     * @return <tt>true</tt> if this task completed
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns <tt>true</tt> if this task was cancelled before it completed
     * normally.
     *
     * @return <tt>true</tt> if this task was cancelled before it completed
     */
    public boolean isCancelled() {
        return isCancelled;
    }

    // Status setters

    /**
     * This method must be called <strong>once</strong> when the task is completed. This will mark the
     * promise as done, fire <code>CompletedEvent</code> to attached listeners and process all continuations.
     */
    public void markDone() {
        isDone = true;
        delegate.processMessage(new CompletedEvent(this));
        fireContinuations();
    }

    /**
     * This method must be called if the task was cancelled. This will mark the promise as cancelled and fire
     * <code>CancelledEvent</code> to attached listeners.
     */
    public void markCancelled() {
        isDone = true;
        isCancelled = true;
        delegate.processMessage(new CancelledEvent(this));
    }

    /**
     * Append a callable continuation -- i.e. a continuation which returns a value.
     *
     * @param continuation A continuation to append -- something implementing <code>Callable</code> interface.
     *                     Normally it would be called a <em>closure</em>.
     * @return A promise for this continuation (which can be checked for execution, or appended with other
     * continuations).
     */
    public <W> Promise<W> append(Callable<W> continuation) {
        PromiseTask<W> promiseContinuation = new PromiseTask<W>(continuation);
        if (isDone) {
            promiseContinuation.run();
        } else {
            continuations.add(promiseContinuation);
        }
        return promiseContinuation;
    }

    /**
     * Append a runnable continuation -- i.e. a continuation which doesn't return a value.
     *
     * @param continuation A continuation to append -- something implementing <code>Runnable</code> interface.
     * @return A promise for this continuation.
     */
    public Promise<Void> append(final Runnable continuation) {
        PromiseTask<Void> promiseContinuation = new PromiseTask<Void>(new Callable<Void>() {
            public Void call() {
                continuation.run();
                return null;
            }
        });
        if (isDone) {
            promiseContinuation.run();
        } else {
            continuations.add(promiseContinuation);
        }
        return promiseContinuation;
    }

    /**
     * Append a callable continuation -- i.e. a continuation which returns a value -- using a specified
     * executor to run it.
     *
     * @param continuation A continuation to append -- something implementing <code>Callable</code> interface.
     *                     Normally it would be called a <em>closure</em>.
     * @param executor An executor to submit continuation to.
     * @return A promise for this continuation (which can be checked for execution, or appended with other
     * continuations).
     */
    public <W> Promise<W> append(Callable<W> continuation, final Executor executor) {
        final PromiseTask<W> promiseContinuation = new PromiseTask<W>(continuation);
        if (isDone) {
            executor.execute(promiseContinuation);
        } else {
            continuations.add(new Runnable() {
                public void run() {
                    executor.execute(promiseContinuation);
                }
            });
        }
        return promiseContinuation;
    }

    /**
     * Append a runnable continuation -- i.e. a continuation which doesn't return a value -- using a specified
     * executor to run it.
     *
     * @param continuation A continuation to append -- something implementing <code>Runnable</code> interface.
     * @param executor An executor to submit continuation to.
     * @return A promise for this continuation (which can be checked for execution, or appended with other
     * continuations).
     */
    public Promise<Void> append(final Runnable continuation, final Executor executor) {
        final PromiseTask<Void> promiseContinuation = new PromiseTask<Void>(new Callable<Void>() {
            public Void call() {
                continuation.run();
                return null;
            }
        });
        if (isDone) {
            executor.execute(promiseContinuation);
        } else {
            continuations.add(new Runnable() {
                public void run() {
                    executor.execute(promiseContinuation);
                }
            });
        }
        return promiseContinuation;
    }

    public final T get(long timeout, TimeUnit unit) throws TimeoutException {
        return get(unit.toMillis(timeout));
    }

    public final void join() {
        get();
    }

    public final void join(long timeout) throws TimeoutException {
        get(timeout);
    }

    // Stuff to override
    public abstract T get();

    public abstract T get(long timeout) throws TimeoutException;

    // Can be overridden, though need not be.
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    // Private methods

    private void fireContinuations() {
        for (Runnable continuation : continuations) {
            continuation.run();
        }
    }
}
