package com.github.shansun.concurrent.queue;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * http://blog.csdn.net/hudashi/article/details/7076798
 * http://www.cnblogs.com/jobs/archive/2007/04/27/730255.html
 *
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-30
 */
public class DelayQueueDemo {

    /**
     * @param args
     */
    public static void main(String[] args) {
        BlockingQueue<Task> queue = new DelayQueue<Task>();

        for (int i = 0; i < 10; i++) {
            new Thread(new ThreadProducer(queue)).start();
            new Thread(new ThreadConsumer(queue)).start();
        }
    }

    static class Task implements Delayed {

        String	name;
        long	submitTime;

        public Task(String name, long delayTime) {
            super();
            this.name = name;
            this.submitTime = TimeUnit.NANOSECONDS.convert(delayTime, TimeUnit.MILLISECONDS) + System.nanoTime();
        }

        public void doTask() {
            System.out.println("do task: " + name);
        }

        @Override
        public int compareTo(Delayed o) {
//			System.out.println("compare to...");
            Task that = (Task) o;
            return submitTime > that.submitTime ? 1 : (submitTime < that.submitTime ? -1 : 0);
        }

        @Override
        public long getDelay(TimeUnit unit) {
            System.out.println("get delay...");
            return unit.convert(submitTime - System.nanoTime(), TimeUnit.MILLISECONDS);
        }
    }

    static class ThreadProducer implements Runnable {

        BlockingQueue<Task>	queue;

        public ThreadProducer(BlockingQueue<Task> queue) {
            super();
            this.queue = queue;
        }

        static int		count	= 0;

        static Random	random	= new Random(System.currentTimeMillis());

        @Override
        public void run() {
            while (true) {
                count = (count + 1) & 0xffffffff;

                Task task = new Task("" + count, random.nextInt() & 0xFFFF);

                try {
                    queue.put(task);
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class ThreadConsumer implements Runnable {

        BlockingQueue<Task>	queue;

        public ThreadConsumer(BlockingQueue<Task> queue) {
            super();
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Task task = queue.take();
                    task.doTask();
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}