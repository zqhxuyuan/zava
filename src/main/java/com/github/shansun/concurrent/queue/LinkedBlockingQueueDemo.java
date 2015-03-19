package com.github.shansun.concurrent.queue;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 阻塞队列 http://www.blogjava.net/fidodido/archive/2005/10/11/15269.html
 * @author:     lanbo <br>
 * @version:    1.0  <br>
 * @date:   	2012-7-19
 */
public class LinkedBlockingQueueDemo {

    /**
     * @param args
     */
    public static void main(String[] args) {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<LinkedBlockingQueueDemo.Message>();
        Producer p = new Producer(queue);
        Consumer c1 = new Consumer(queue);
        Consumer c2 = new Consumer(queue);
        new Thread(p).start();
        new Thread(c1).start();
        new Thread(c2).start();
    }

    static class Producer implements Runnable {
        private final BlockingQueue<Message> queue;

        public Producer(BlockingQueue<Message> queue) {
            super();
            this.queue = queue;
        }

        @Override
        public void run() {
            while(true) {
                try {
                    queue.put(new Message());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Consumer implements Runnable {
        private final BlockingQueue<Message> queue;

        public Consumer(BlockingQueue<Message> queue) {
            super();
            this.queue = queue;
        }

        @Override
        public void run() {
            while(true) {
                try {
                    Message message = queue.take();
                    System.err.println(message.getId());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    static class Message {
        private String id;

        public Message() {
            this(UUID.randomUUID().toString());
        }

        public Message(String id) {
            super();
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}