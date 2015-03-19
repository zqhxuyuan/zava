package com.atemerev.pms.examples.async;

import com.atemerev.pms.examples.helloworld.Morning;
import com.atemerev.pms.listeners.dispatch.DispatchListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Alexander Temerev
 * @version $Id$
 */
public class Main {
    public static void main(String[] args) throws Exception {
        SleepyMorningHandler sleepyMorning
                = new SleepyMorningHandler();
        ExecutorService threadPool
                = Executors.newFixedThreadPool(2);
        DispatchListener listener
                = new DispatchListener(
                sleepyMorning, threadPool);
        listener.processMessage(new Morning());
        Thread.sleep(50);
        System.out.println("Meanwhile...");
        System.out.println("A work day started...");
        System.out.println("Where are my TPS reports?");
        threadPool.shutdown();
    }
}
