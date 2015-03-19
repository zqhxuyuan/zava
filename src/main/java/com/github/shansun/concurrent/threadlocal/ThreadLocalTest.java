package com.github.shansun.concurrent.threadlocal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-13
 */
public class ThreadLocalTest {

    // 单线程
    // static ExecutorService executor = Executors.newSingleThreadExecutor();

    // 固定大小线程池
    static ExecutorService		executor		= Executors.newFixedThreadPool(5);

    static ThreadLocal<String>	threadLocal1	= new ThreadLocal<String>();

    static ThreadLocal<String>	threadLocal2	= new ThreadLocal<String>();

    /**
     * @param args
     */
    public static void main(String[] args) {
        // wrongUsage();

        rightUsage();

        executor.shutdown();
    }

    static void wrongUsage() {
        Runnable wrongCMD = new Runnable() {

            @Override
            public void run() {
                if (threadLocal1 == null) {
                    System.err.println("ThreadLocal is null, init it!");
                    threadLocal1 = new ThreadLocal<String>();
                }

                if (threadLocal1.get() == null) {
                    long millis = System.currentTimeMillis();
                    System.err.println("ThreadLocal is empty, set it to " + millis + "!");
                    threadLocal1.set("Hello@" + millis);
                } else {
                    System.err.println("ThreadLocal is not null, get it!");
                    System.err.println(threadLocal1.get() + ", current=" + System.currentTimeMillis());
                }

                threadLocal1 = null;
            }
        };

        for (int i = 0; i < 10; i++) {
            executor.execute(wrongCMD);
        }
    }

    static void rightUsage() {
        Runnable rightCMD = new Runnable() {

            @Override
            public void run() {
                if (threadLocal2.get() == null) {
                    long millis = System.currentTimeMillis();
                    System.err.println("ThreadLocal is empty, set it to " + millis + "!");
                    threadLocal2.set("Hello@" + millis);
                } else {
                    System.err.println("ThreadLocal is not null, get it!");
                    System.err.println(threadLocal2.get() + ", current=" + System.currentTimeMillis());
                }

                threadLocal2.remove();
            }
        };

        for (int i = 0; i < 10; i++) {
            executor.execute(rightCMD);
        }
    }
}