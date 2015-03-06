package com.interview.algorithms.design;

import com.interview.basics.java.cocurrency.blocking.BlockingQueue;
import com.interview.basics.java.cocurrency.blocking.BlockingQueueUsingLock;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 10/24/14
 * Time: 10:57 AM
 */
public class C10_5_ProducerConsumerWithBlockingQueue {
    static class Producer implements Runnable {
        int counter = 0;
        BlockingQueue<Message> queue;
        Random random = new Random();
        int id;

        public Producer(int id, BlockingQueue<Message> queue) {
            this.id = id;
            this.queue = queue;
        }

        @Override
        public void run() {
            System.out.printf("PRODUCER-%d IS ON\n", this.id);
            while (true) {
                int time = random.nextInt(5) + 1;
                String message = "task " + id + "-" + counter++ + " need " + time + " seconds";
                Message m = new Message(time, message);
                System.out.printf("PRODUCER-%d: generate a message %s\n ", this.id, m.message);
                try {
                    queue.add(m);
                    System.out.printf("PRODUCER-%d: pushed a message %s in queue, current size is %d\n ", this.id, m.message, queue.size());
                    Thread.currentThread().sleep(5 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void start() {
            Thread thread = new Thread(this);
            thread.start();
        }
    }

    static class Consumer implements Runnable {
        BlockingQueue<Message> queue;
        int id;

        public Consumer(int id, BlockingQueue<Message> queue) {
            this.id = id;
            this.queue = queue;
        }

        @Override
        public void run() {
            System.out.println("CONSUMER IS ON");
            while (true) {
                try {
                    Message m = queue.take();
                    System.out.printf("CONSUMER-%d: handling %s\n", this.id, m.message);
                    Thread.currentThread().sleep(m.time * 3 * 1000);
                    System.out.printf("CONSUMER-%d: finish %s\n", this.id, m.message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void start() {
            Thread thread = new Thread(this);
            thread.start();
        }
    }

    public static void main(String[] args) {
        BlockingQueue<Message> queue = new BlockingQueueUsingLock<>(5);
        for (int i = 0; i < 3; i++) {
            new Producer(i + 1, queue).start();
            try {
                Thread.currentThread().sleep(3 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 5; i++) {
            new Consumer(i + 1, queue).start();
            try {
                Thread.currentThread().sleep(5 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}