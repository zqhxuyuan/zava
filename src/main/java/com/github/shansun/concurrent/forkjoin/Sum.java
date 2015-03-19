package com.github.shansun.concurrent.forkjoin;

import EDU.oswego.cs.dl.util.concurrent.FJTask;
import EDU.oswego.cs.dl.util.concurrent.FJTaskRunnerGroup;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-4
 */
public class Sum {

    private static final int	TASK_COUNT	= 300000;

    /**
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        parallelSum();

        commonSum();
    }

    private static void parallelSum() throws InterruptedException {
        FJTaskRunnerGroup g = new FJTaskRunnerGroup(Runtime.getRuntime().availableProcessors());

        long start = System.currentTimeMillis();

        SumTask[] tasks = new SumTask[TASK_COUNT];

        for (int i = 0; i < TASK_COUNT; i++) {
            tasks[i] = new SumTask();
        }

        g.invoke(new FJTask.Par(tasks));

        long sum = 0;

        for (int i = 0; i < TASK_COUNT; i++) {
            sum += tasks[i].sum;
        }

        System.out.println(sum);

        System.err.println("Parallel Used " + (System.currentTimeMillis() - start) + " ms");
    }

    private static class SumTask extends FJTask {
        long	sum	= 0;

        @Override
        public void run() {
            for (int i = 0; i < Short.MAX_VALUE * 4; i++) {
                sum += i;
            }
        }
    }

    private static void commonSum() {
        long sum = 0;

        Runtime.getRuntime().availableProcessors();

        long start = System.currentTimeMillis();

        for (int j = 0; j < TASK_COUNT; j++) {
            for (int i = 0; i < Short.MAX_VALUE * 4; i++) {
                sum += i;
            }
        }

        System.out.println(sum);

        System.err.println("Common Used " + (System.currentTimeMillis() - start) + " ms");
    }

}