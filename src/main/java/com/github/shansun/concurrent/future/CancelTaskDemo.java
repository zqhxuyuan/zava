package com.github.shansun.concurrent.future;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Maps;

/**
 * @author     lanbo <br>
 * @version    1.0  <br>
 * @date       2012-11-2
 */
public class CancelTaskDemo {
    public static void main(String[] args) {
        final ThreadGroup		threadGroup			= new ThreadGroup("Multi-Withhold");

        ExecutorService exec =  new ThreadPoolExecutor(0, 5, 300, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadFactory() {
            Integer threadPoint = 0;

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(threadGroup, r, "Multi-Withhold-Thread-" + (threadPoint++));
            }

        }, new ThreadPoolExecutor.CallerRunsPolicy());

        Map<Integer, Future<Long>> futures = Maps.newHashMap();
        for(int i = 0; i < 5; i++) {
            final int tmpI = i;
            Future<Long> future = exec.submit(new Callable<Long>() {

                @Override
                public Long call() throws Exception {
                    long j = 0;
                    for(;j < Long.MAX_VALUE; j++) {
                        System.out.println("线程-" + tmpI + "正在运行");
                        Thread.sleep(5000);
                    }
                    return j;
                }
            });

            futures.put(i, future);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String str = null;
            try {
                System.out.println("需要停止的线程序号");
                str = br.readLine();

                int threadId = Integer.parseInt(str);

                Future<Long> future = futures.get(threadId);

                if(future != null) {
                    future.cancel(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}