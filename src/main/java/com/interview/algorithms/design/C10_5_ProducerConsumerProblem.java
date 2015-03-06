package com.interview.algorithms.design;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/25/14
 * Time: 1:36 PM
 */
class Message {
    int time;
    String message;

    Message(int time, String message) {
        this.time = time;
        this.message = message;
    }
}

class MessageQueue<T> {
    Queue<T> queue = new ArrayDeque<T>();

    public void add(T element) {
        queue.add(element);
        synchronized (this){
            System.out.println("Add element to the queue, queue size is " + queue.size());
            this.notifyAll();
        }
    }

    public T get() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

class Producer implements Runnable {
    MessageQueue<Message> queue;
    Random random = new Random();
    int id;

    public Producer(int id, MessageQueue<Message> queue) {
        this.id = id;
        this.queue = queue;
    }

    @Override
    public void run() {
        System.out.printf("PRODUCER-%d IS ON\n", this.id);
        while (true) {
            int time = random.nextInt(5);
            String message = "A task need " + time + " seconds";
            Message m = new Message(time, message);
            System.out.printf("PRODUCER-%d: generate a message %s\n ", this.id, m.message);
            queue.add(m);
            try {
                Thread.currentThread().sleep(2 * 1000);
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

class Consumer implements Runnable {
    MessageQueue<Message> queue;
    int id;

    public Consumer(int id, MessageQueue<Message> queue) {
        this.id = id;
        this.queue = queue;
    }

    @Override
    public void run() {
        System.out.println("CONSUMER IS ON");
        while (true) {
            Message m = null;
            while(m == null){
                synchronized (queue) {
                    if (queue.isEmpty()) {
                        try {
                            //System.out.printf("CONSUMER-%d: waiting for message\n", this.id);
                            queue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        m = queue.get();
                    }
                }
            }

            System.out.printf("CONSUMER-%d: handling %s\n", this.id, m.message);
            try {
                Thread.currentThread().sleep(m.time * 1000);
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

public class C10_5_ProducerConsumerProblem {

    public static void main(String[] args) {
        MessageQueue<Message> queue = new MessageQueue<>();
        for(int i = 0; i < 10; i++){
            if(i % 2 == 0)
                new Producer(i/2+1, queue).start();
            new Consumer(i+1, queue).start();
        }
    }
}
