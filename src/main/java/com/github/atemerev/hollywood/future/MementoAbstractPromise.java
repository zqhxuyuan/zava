package com.github.atemerev.hollywood.future;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author Alexander Temerev, Alexander Kuklev
 * @version $Id$
 */
public abstract class MementoAbstractPromise<T> extends AbstractPromise<T> implements MementoPromise<T> {
    public <W> MementoPromise<W> append(Callable<W> continuation) {
        final PromiseTask<W> continuationPromise = (PromiseTask<W>) super.append(continuation);
        listeners().add(continuationPromise.delegate);
        return new MementoPromiseProxy<W>(continuationPromise) {
            public List getMemories() {
                return MementoAbstractPromise.this.getMemories();
            }
        };
    }

    public MementoPromise<Void> append(final Runnable continuation) {
        final PromiseTask<Void> continuationPromise = (PromiseTask<Void>) super.append(continuation);
        listeners().add(continuationPromise.delegate);
        return new MementoPromiseProxy<Void>(continuationPromise) {
            public List getMemories() {
                return MementoAbstractPromise.this.getMemories();
            }
        };
    }
}
