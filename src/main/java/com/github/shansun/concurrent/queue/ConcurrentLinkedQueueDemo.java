package com.github.shansun.concurrent.queue;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 非阻塞队列
 * http://www.ibm.com/developerworks/cn/java/j-tiger06164/
 * http://hubingforever.blog.163.com/blog/static/17104057920106295029175/
 * http://www.ibm.com/developerworks/cn/java/j-jtp04186/
 * http://www.blogjava.net/xylz/archive/2010/07/23/326934.html
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-19
 */
public class ConcurrentLinkedQueueDemo {

    public static void main(String[] args) {
        final Queue<String> queue = new ConcurrentLinkedQueue<String>();

        for (int i = 0; i < 100000; i++) {
            queue.add(String.valueOf(i));
        }

        // 起10条线程task值
        for (int i = 0; i < 10; i++) {
            new Thread() {

                @Override
                public void run() {
                    long start = System.currentTimeMillis();

                    int count = 0;
                    while (queue.poll() != null) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        count++;
                    }

                    System.err.println("[" + Thread.currentThread().getName() + "] Polled " + count + ", Used " + (System.currentTimeMillis() - start) + "ms");
                }

            }.start();
        }
    }
}