package com.github.shansun.concurrent.queue;

import java.util.Random;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * http://blog.csdn.net/hudashi/article/details/7076814
 *
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-30
 */
public class SynchronousQueueDemo {

    /**
     * @param args
     */
    public static void main(String[] args) {
        SynchronousQueue<String> queue = new SynchronousQueue<String>();

        for (int i = 0; i < 10; i++) {
            new Thread(new ThreadProducer(queue)).start();
            new Thread(new ThreadConsumer(queue)).start();
        }
    }

    static class ThreadProducer implements Runnable {

        SynchronousQueue<String>	queue;

        public ThreadProducer(SynchronousQueue<String> queue) {
            super();
            this.queue = queue;
        }

        static int		count	= 0;

        static Random	random	= new Random(System.currentTimeMillis());

        @Override
        public void run() {
            String name = "";
            int val = 0;

            while (true) {
                count = (count + 1) & 0xffffffff;

                val = random.nextInt() % 15;

                try {
                    if (val < 5) {
                        name = "offer name: " + count;
                        queue.offer(name);
                    } else if (val < 10) {
                        name = "put name: " + count;
                        queue.put(name);
                    } else {
                        name = "offer wait time and name: " + count;
                        queue.offer(name, 1000, TimeUnit.MILLISECONDS);
                    }

                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class ThreadConsumer implements Runnable {

        SynchronousQueue<String>	queue;

        public ThreadConsumer(SynchronousQueue<String> queue) {
            super();
            this.queue = queue;
        }

        @Override
        public void run() {
            String name;

            while (true) {
                try {
                    name = queue.take();
                    System.out.println("task " + name);
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}