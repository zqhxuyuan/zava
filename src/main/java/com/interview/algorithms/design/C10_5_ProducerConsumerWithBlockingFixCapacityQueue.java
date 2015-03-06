package com.interview.algorithms.design;

import com.interview.basics.java.cocurrency.blocking.BlockingFixCapabilityQueue;

import java.util.Random;

/**
 * Created_By: stefanie
 * Date: 14-10-23
 * Time: 下午7:10
 */



public class C10_5_ProducerConsumerWithBlockingFixCapacityQueue {
    static class Producer implements Runnable {
        int counter = 0;
        BlockingFixCapabilityQueue<Message> queue;
        Random random = new Random();
        int id;

        public Producer(int id, BlockingFixCapabilityQueue<Message> queue) {
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
                queue.push(m);
                System.out.printf("PRODUCER-%d: pushed a message %s in queue, current size is %d\n ", this.id, m.message, queue.size());
                try {
                    Thread.currentThread().sleep(5 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void start(){
            Thread thread = new Thread(this);
            thread.start();
        }
    }

    static class Consumer implements Runnable {
        BlockingFixCapabilityQueue<Message> queue;
        int id;

        public Consumer(int id, BlockingFixCapabilityQueue<Message> queue) {
            this.id = id;
            this.queue = queue;
        }

        @Override
        public void run() {
            System.out.println("CONSUMER IS ON");
            while (true) {
                Message m = queue.pop();
                System.out.printf("CONSUMER-%d: handling %s\n", this.id, m.message);
                try {
                    Thread.currentThread().sleep(m.time * 3 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.printf("CONSUMER-%d: finish %s\n", this.id, m.message);
            }
        }

        public void start(){
            Thread thread = new Thread(this);
            thread.start();
        }
    }

    public static void main(String[] args) {
        BlockingFixCapabilityQueue<Message> queue = new BlockingFixCapabilityQueue<>(5);
        for(int i = 0; i < 3; i++){
            new Producer(i+1, queue).start();
            try {
                Thread.currentThread().sleep(3 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for(int i = 0; i < 5; i++){
            new Consumer(i+1, queue).start();
            try {
                Thread.currentThread().sleep(5 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

