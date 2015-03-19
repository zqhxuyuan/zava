package com.github.shansun.concurrent.queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * http://blog.csdn.net/hudashi/article/details/7076745
 *
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-30
 */
public class ArrayBlockingQueueDemo {

    /**
     * @param args
     */
    @SuppressWarnings("rawtypes")
    public static void main(String[] args) {
        BlockingQueue<?> queue = new ArrayBlockingQueue(100);

        for (int i = 0; i < 10; i++) {
            new Thread(new ThreadProducer(queue)).start();
            new Thread(new ThreadConsumer(queue)).start();
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    static class ThreadProducer implements Runnable {

        BlockingQueue	queue;

        public ThreadProducer(BlockingQueue queue) {
            super();
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    queue.put(System.currentTimeMillis());
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    static class ThreadConsumer implements Runnable {
        BlockingQueue	queue;

        public ThreadConsumer(BlockingQueue queue) {
            super();
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    System.out.println(queue.take());
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}