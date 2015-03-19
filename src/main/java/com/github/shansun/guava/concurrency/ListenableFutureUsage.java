package com.github.shansun.guava.concurrency;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * <p>
 * </p>
 *
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-4
 */
public class ListenableFutureUsage {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // 创建
        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

        // 执行
        ListenableFuture<String> future = service.submit(new Callable<String>() {

            @Override
            public String call() throws Exception {
                return "Hello future";
            }
        });

        // 设置回调
        Futures.addCallback(future, new FutureCallback<String>() {

            @Override
            public void onSuccess(String result) {
                System.out.println("I got the result: " + result);
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("Encounter an exception!");
                t.printStackTrace();
            }
        });
    }

}