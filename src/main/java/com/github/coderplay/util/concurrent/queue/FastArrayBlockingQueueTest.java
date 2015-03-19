package com.github.coderplay.util.concurrent.queue;

import java.util.concurrent.BlockingQueue;

/**
 * Created by zqhxuyuan on 15-3-18.
 */
public class FastArrayBlockingQueueTest {

    public static void main(String[] args) {
        final int BUFFER_SIZE = 1024 * 8;
        final long ITERATIONS = 1000L * 1000L * 10L;

        long start = System.currentTimeMillis();
        //创建一个阻塞队列
        final BlockingQueue<Long> queue =
                new FastArrayBlockingQueue<Long>(
                        // producer strategy
                        new SingleThreadedClaimStrategy(BUFFER_SIZE),
                        // consumer strategy
                        new SingleThreadedWaitStrategy());

        //消费者
        Runnable consumer = new Runnable() {
            @Override
            public void run() {
                try {
                    for (long l = 0; l < ITERATIONS; l++)
                        queue.take().longValue();
                } catch (InterruptedException ie) {
                }
            }
        };

        //生产者
        Runnable producer = new Runnable() {
            @Override
            public void run() {
                try {
                    for (long l = 0; l < ITERATIONS; l++)
                        queue.put(Long.valueOf(l));
                } catch (InterruptedException ie) {
                }
            }
        };

        new Thread(consumer).start();
        new Thread(producer).start();

        System.out.println(System.currentTimeMillis() - start);
    }
}
