package com.github.atemerev.hollywood.future;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * // todo rewrite comments
 * A support class for appending continuations to promises. After the promise is fulfilled (i.e. its underlying
 * future task is done), continuation execution is started by fire() method. The result of promise execution is
 * accessible within the caller's scope.
 * <p/>
 * Continuations can be chained. This will inevitably lead you to the Haskell hell with all its monads, so be
 * warned.
 *
 * @author Alexander Temerev, Alexander Kuklev
 * @version $Id$
 */
public class PromiseTask<T> extends AbstractPromise<T> implements Promise<T>, Runnable {

    private Callable<T> task;

    private CountDownLatch done = new CountDownLatch(1);
    private RuntimeExecutionException executionException;
    private T result;

    /**
     * Create the continuation with the supplied Callable.
     *
     * @param task A Callable implementation to use for the continuation.
     */
    public PromiseTask(Callable<T> task) {
        this.task = task;
    }

    /**
     * Start the continuation. This method should be called after the caller promise is fulfilled. If the continuation
     * throws exception, it will be stored and will be thereafter thrown by get() method.
     */
    public void run() {
        try {
            result = task.call();
        } catch (Throwable e) {
            executionException = new RuntimeExecutionException(e);
        }
        done.countDown();
        markDone();
    }

    /**
     * Join the continuation execution to the current thread and get its result. If exception had been thrown,
     * it's wrapped into the unchecked RuntimeExcecutionException and then rethrown.
     *
     * @return Continuation result.
     * @throws RuntimeExecutionException If exception has been thrown during the execution.
     */
    public T get() throws RuntimeExecutionException {
        try {
            done.await();
            if (executionException != null) {
                throw executionException;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Manual interruption is not supported");
        }
        return result;
    }

    /**
     * Attempt to join the continuation to the current thread for the specified time. If still running,
     * throws TimeoutException (and the continuation continues to run, so if you want to stop it, use cancel()).
     * Other than that, works the same way as the no-argument get().
     *
     * @param timeout Timeout to wait for execution completion while joining the thread.
     * @return Execution result (if completed before timeout).
     * @throws TimeoutException          If timeout happened before execution has been completed.
     * @throws RuntimeExecutionException If exception has been thrown during the execution.
     */
    public T get(long timeout) throws RuntimeExecutionException, TimeoutException {
        try {
            done.await(timeout, TimeUnit.MILLISECONDS);
            if (executionException != null) {
                throw executionException;
            }
            if (result == null) {
                throw new TimeoutException();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Manual interruption is not supported");
        }
        return result;
    }

    //TODO: Reimplement the whole class using the FutureTask source codes.
}
