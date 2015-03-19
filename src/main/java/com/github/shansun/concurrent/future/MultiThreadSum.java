package com.github.shansun.concurrent.future;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AtomicDouble;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-5
 */
public class MultiThreadSum {
    protected static final int	CALC_TIMES	= 8;

    protected static final int	CONST		= 10000000;

    // static ListeningExecutorService service =
    // MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(20));

    static ExecutorService		service		= Executors.newFixedThreadPool(20);

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        for (int i = 0; i < 10; i++) {
            commonSum();
            futureSum();
            countdownSum();
        }

        service.shutdown();
    }

    static void commonSum() {
        long start = System.currentTimeMillis();

        double sum = 0;

        for (int j = 0; j < CALC_TIMES; j++) {
            for (int i = 1; i <= CONST; i++) {
                sum += i;
            }
        }

        System.err.println("Common [" + sum + "] Used: " + (System.currentTimeMillis() - start));
    }

    static void futureSum() throws InterruptedException, ExecutionException {
        long start = System.currentTimeMillis();

        List<Future<Long>> resultList = Lists.newArrayList();

        for (int i = 0; i < CALC_TIMES; i++) {
            // final int k = i;

            Future<Long> future = service.submit(new Callable<Long>() {

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
            });

            resultList.add(future);
        }

        double sum = 0;

        for (Future<Long> s : resultList) {
            sum += s.get();
        }

        System.err.println("Future [" + sum + "] Used: " + (System.currentTimeMillis() - start));
    }

    static void countdownSum() throws InterruptedException {
        long start = System.currentTimeMillis();

        final CountDownLatch finish = new CountDownLatch(CALC_TIMES);

        final AtomicDouble totalSum = new AtomicDouble(0);

        for (int i = 0; i < CALC_TIMES; i++) {
            service.execute(new Runnable() {

                @Override
                public void run() {
                    long sum = 0;

                    for (int i = 1; i <= CONST; i++) {
                        sum += i;
                    }

                    totalSum.addAndGet(sum);

                    finish.countDown();
                }
            });
        }

        finish.await();

        System.err.println("CountDown [" + totalSum.doubleValue() + "] Used: " + (System.currentTimeMillis() - start));
    }
}