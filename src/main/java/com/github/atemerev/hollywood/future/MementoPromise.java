package com.github.atemerev.hollywood.future;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * <code>MementoPromise</code> is a useful extension of a <code>Promise</code> allowing for storage of
 * intermediate computation results called <em>memories</em>. It's an abstraction for one of the most common cases
 * where intermediate results are delivered as separate entities in some specific order.
 *
 * <code>MementoPromise</code> can be used in design of stateful communication protocols, handling of
 * partial updates, and other clever things.
 *
 * @author Alexander Temerev, Alexander Kuklev
 * @version $Id$
 */
public interface MementoPromise<T> extends Promise<T> {

    /**
     * Get the list of stored intermediate computation results, <em>memories</em>. This method can be implemented
     * as generic-typed equivalent.
     * @return The list of memories. Generally it's <em>not</em> thread-safe, so manual synchronization will be
     * required!
     */
    public List getMemories();

    /**
     * Append a callable continuation to this <code>MementoPromise</code>, which will be executed as soon as the
     * promise execution is completed. The <code>MementoPromise</code> for this continuation is returned, so it
     * can be checked for it's own execution or appended with other continuations if necessary.
     *
     * @param continuation A continuation to append -- something implementing <code>Callable</code> interface.
     *                     Normally it would be called a <em>closure</em>.
     * @return A <code>MementoPromise</code> for the continuation appended.
     */
    public <W> MementoPromise<W> append(Callable<W> continuation);

    /**
     * Append a runnable continuation to this <code>MementoPromise</code>, which will be executed as soon as the
     * promise execution is completed. The <code>MementoPromise&lt;Void&gt;</code> is returned, which
     * can be checked for it's completion or appended with other continuations.
     *
     * @param continuation A continuation to append -- something implementing <code>Runnable</code> interface.
     * @return A <code>MementoPromise</code> for the continuation appended.
     */
    public MementoPromise<Void> append(Runnable continuation);
}
