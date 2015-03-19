package com.github.shansun.concurrent.future;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Lists;

/**
 * <p>
 * </p>
 *
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-16
 */
public class MultiThreadParrallelSum extends MultiThreadSum {

    static ExecutorService	service	= new ThreadPoolExecutor(10, 600, 30, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadPoolExecutor.AbortPolicy());

    static long				value	= 0;

    /**
     * @param args
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        futureSum();

        service.shutdown();
    }

    static void futureSum() throws InterruptedException, ExecutionException {
        long start = System.currentTimeMillis();

        List<Callable<Long>> tasks = Lists.newArrayList();

        for (int i = 0; i < 100; i++) {
            Callable<Long> task = new Callable<Long>() {

                @Override
                public Long call() throws Exception {
                    long sum = 0;

                    for (int j = 1; j <= CONST; j++) {
                        sum += j;
                    }

                    // System.out.println(Thread.currentThread().getName() +
                    // " is Down! [" + k + "]");

                    return sum;
                }
            };
            tasks.add(task);
        }

        List<Future<Long>> futures = service.invokeAll(tasks, 300, TimeUnit.MILLISECONDS);

        int succ = 0;
        int fail = 0;
        int total = 0;

        for (Future<Long> s : futures) {
            try {
                if (s.isDone()) {
                    if (!s.isCancelled()) {
                        // System.err.println(s.toString() + "is done!");
                        value = s.get();
                    } else {
                        // System.err.println(s.toString() + " is cancelled" + ", cancelled=" + s.isCancelled() + ", done=" + s.isDone());
                    }
                } else {
                    s.cancel(true);
                    System.err.println("Cancelling " + s);
                }

                succ++;
            } catch (Exception e) {
                e.printStackTrace();
                fail++;
            } finally {
                total++;
            }
        }

        System.err.println("Future [succ=" + succ + ", fail=" + fail + ", total=" + total + "] Used: " + (System.currentTimeMillis() - start));
    }
}