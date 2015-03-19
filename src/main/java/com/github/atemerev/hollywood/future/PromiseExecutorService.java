package com.github.atemerev.hollywood.future;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

public class PromiseExecutorService {
    private final Executor executor;

    public PromiseExecutorService(Executor executor) {
        this.executor = executor;
    }

    public <W> Promise<W> submit(Callable<W> task) {
        PromiseTask<W> promiseTask = new PromiseTask<W>(task);
        executor.execute(promiseTask);
        return promiseTask;
    }

    public Promise<Void> submit(final Runnable task) {
        PromiseTask<Void> promiseTask = new PromiseTask<Void>(new Callable<Void>() {
            public Void call() {
                task.run();
                return null;
            }
        });
        executor.execute(promiseTask);
        return promiseTask;
    }

    public <R> Promise<R> submit(final Runnable task, final R result) {
        PromiseTask<R> promiseTask = new PromiseTask<R>(new Callable<R>() {
            public R call() {
                task.run();
                return result;
            }
        });
        executor.execute(promiseTask);
        return promiseTask;
    }
}
