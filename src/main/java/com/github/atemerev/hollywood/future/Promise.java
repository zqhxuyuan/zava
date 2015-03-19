package com.github.atemerev.hollywood.future;

import com.github.atemerev.pms.listeners.HasMessageListeners;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * A <code>Promise</code> is generally a <code>Future</code> with support for appending continuations.
 * A continuation is an action which will be executed at the moment the <code>Future</code> completes. This action
 * can itself be a <code>Promise</code>, so they can be chained.
 * <p/>
 * <code>Promise&lt;Void&gt;</code> form is also common -- it provides a convenient way to check for completion of
 * some action (presumably running in a different thread) without caring for it's returned result.
 * <p/>
 * As an additional bonus, a <code>Promise</code> can accept message listeners. It will fire
 * <code>CancelledEvent</code> when task is cancelled and <code>CompletedEvent</code> when task is done. During
 * the execution progress, other messages can be fired and delivered to listeners.
 *
 * @author Alexander Temerev, Alexander Kuklev
 * @version $Id$
 */
public interface Promise<T> extends Future<T>, HasMessageListeners {

    /**
     * Append a callable continuation to this <code>Promise</code>, which will be executed (in the promise's thread)
     * as soon as the <code>Promise</code>'s execution is completed. The <code>Promise</code> for this continuation is
     * returned, so it can be checked for it's own execution or appended with other continuations if necessary.
     *
     * @param continuation A continuation to append -- something implementing <code>Callable</code> interface.
     *                     Normally it would be called a <em>closure</em>.
     * @return A <code>Promise</code> for the continuation appended.
     */
    public <W> Promise<W> append(Callable<W> continuation);

    /**
     * Append a runnable continuation to this <code>Promise</code>, which will be executed (in the promise's thread)
     * as soon as the <code>Promise</code>'s execution is completed. The <code>Promise&lt;Void&gt;</code> is
     * returned, which can be checked for it's completion or appended with other continuations.
     *
     * @param continuation A continuation to append -- something implementing <code>Runnable</code> interface.
     * @return A <code>Promise</code> for the continuation appended.
     */
    public Promise<Void> append(Runnable continuation);

    /**
     * Append a callable continuation to this <code>Promise</code>, which will be executed by specified executor
     * as soon as the <code>Promise</code>'s execution is completed. The <code>Promise</code> for this continuation is
     * returned, so it can be checked for it's own execution or appended with other continuations if necessary.
     *
     * @param continuation A continuation to append -- something implementing <code>Callable</code> interface.
     *                     Normally it would be called a <em>closure</em>.
     * @param executor     An executor to submit continuation to.
     * @return A <code>Promise</code> for the continuation appended.
     */
    public <W> Promise<W> append(Callable<W> continuation, Executor executor);

    /**
     * Append a runnable continuation to this <code>Promise</code>, which will be executed by specified executor
     * as soon as the <code>Promise</code>'s execution is completed. The <code>Promise&lt;Void&gt;</code> is
     * returned, which can be checked for it's completion or appended with other continuations.
     *
     * @param continuation A continuation to append -- something implementing <code>Runnable</code> interface.
     * @param executor     An executor to submit continuation to.
     * @return A <code>Promise</code> for the continuation appended.
     */
    public Promise<Void> append(Runnable continuation, Executor executor);

    /**
     * Waits if necessary (blocking the current thread) for the computation to complete, and then
     * retrieves its result.
     *
     * @return the computed result
     * @throws java.util.concurrent.CancellationException
     *                                   if the computation was cancelled
     * @throws RuntimeExecutionException if the computation threw an
     *                                   exception
     */
    public T get();

    /**
     * Waits if necessary (blocking the current thread) for the timeout specified, and then, if the
     * computation is complete, retrieves its result. Otherwise, throws TimeoutException.
     *
     * @param timeout Time to wait for task execution, in milliseconds.
     * @return the computed result
     * @throws java.util.concurrent.CancellationException
     *                                   if the computation was cancelled
     * @throws RuntimeExecutionException if the computation threw an
     *                                   exception
     * @throws java.util.concurrent.TimeoutException
     *                                   If timeout passed and calculation hasn't been completed yet.
     */
    public T get(long timeout) throws TimeoutException;

    /**
     * Wait for this promise to complete and ignore the result (which can still be retrieved later). This method
     * is just an alias to get(), but it's more convenient to use this form for <code>Promise&lt;Void&gt;</code>
     * tasks.
     */
    public void join();

    /**
     * Wait for this promise to complete for the timeout specified and ignore the result. This method
     * is just an alias to get(), but it's more convenient to use this form for <code>Promise&lt;Void&gt;</code>
     * tasks.
     *
     * @param timeout Time to wait for task execution, in milliseconds.
     * @throws java.util.concurrent.TimeoutException
     *          If timeout passed and calculation hasn't been completed yet.
     */
    public void join(long timeout) throws TimeoutException;
}
