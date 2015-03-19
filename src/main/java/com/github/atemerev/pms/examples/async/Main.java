package com.github.atemerev.pms.examples.async;

import com.github.atemerev.pms.examples.helloworld.Morning;
import com.github.atemerev.pms.listeners.dispatch.DispatchListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 并发执行模式
 * @author Alexander Temerev
 * @version $Id$
 */
public class Main {
    public static void main(String[] args) throws Exception {
        SleepyMorningHandler sleepyMorning = new SleepyMorningHandler();

        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        DispatchListener listener = new DispatchListener(sleepyMorning, threadPool);

        listener.processMessage(new Morning());
        Thread.sleep(50);

        System.out.println("Meanwhile...");
        System.out.println("A work day started...");
        System.out.println("Where are my TPS reports?");
        threadPool.shutdown();
    }
}
