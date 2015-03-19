package com.github.shansun.concurrent.forkjoin;

import EDU.oswego.cs.dl.util.concurrent.FJTask;
import EDU.oswego.cs.dl.util.concurrent.FJTaskRunnerGroup;

/**
 * 使用fork-join方式计算PI值
 *
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-4
 */
public class PI {

    static int		numSteps	= 100;
    static double	step;
    static double	sum			= 0.0;
    static int		partStep;

    static class PITask extends FJTask {
        int		i	= 0;
        double	sum	= 0.0;

        public PITask(int i) {
            this.i = i;
        }

        @Override
        public void run() {
            System.out.println("Current Thread: " + Thread.currentThread().getName());

            double x = (i + 0.5) * step;
            sum += 4.0 / (1.0 + x * x);
        }
    }

    public static void main(String[] args) {
        double pi;
        step = 1.0 / (double) numSteps;

        try {
            int processors = Runtime.getRuntime().availableProcessors();

            System.err.println("I have " + processors + " processors here!");

            FJTaskRunnerGroup g = new FJTaskRunnerGroup(processors);

            long start = System.nanoTime();

            PITask[] tasks = new PITask[numSteps];

            for(int i = 0; i < numSteps; i++) {
                tasks[i] = new PITask(i);
            }

            g.invoke(new FJTask.Par(tasks));

            for(int i = 0; i < numSteps; i++) {
                sum += tasks[i].sum;
            }

            pi = step * sum;

            System.out.println(pi);

            System.out.println(Math.PI);

            System.err.println("Used " + (System.nanoTime() - start));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}