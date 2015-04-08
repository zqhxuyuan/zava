package com.zqh.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

/**
 * http://blog.csdn.net/u014783753/article/details/44805703
 */
public class TestExchanger {

    public static void main(String[] args) {
        List<String> buffer1 = new ArrayList<String>();
        List<String> buffer2 = new ArrayList<String>();
        Exchanger<List<String>> exchanger = new Exchanger<List<String>>();

        Producer producer = new Producer(buffer1,exchanger);
        Consumer consumer = new Consumer(buffer2,exchanger);

        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);

        producerThread.start();
        consumerThread.start();
    }
}

class Producer implements Runnable {

    private List<String> buffer;
    private final Exchanger<List<String>> exchanger;

    public Producer(List<String> buffer, Exchanger<List<String>> exchanger){
        this.buffer = buffer;
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        for(int i = 0; i < 10; i ++){
            System.out.println("Producer cycle:" + (i+1));
            for(int j = 0; j < 10; j ++){
                String mess = "message:" + (i * 10) + j;
                System.out.println("produce:" + mess);
                buffer.add(mess);
            }
            try {
                buffer = exchanger.exchange(buffer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Consumer implements Runnable {

    private List<String> buffer;
    private final Exchanger<List<String>> exchanger;

    public Consumer(List<String> buffer, Exchanger<List<String>> exchanger){
        this.buffer = buffer;
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        for(int i = 0; i < 10; i ++){
            System.out.println("Consumer cycle:" + (i+1));
            try {
                buffer = exchanger.exchange(buffer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(int j = 0; j < 10; j ++){
                String mess = buffer.get(0);
                System.out.println("consume:" + mess);
                buffer.remove(0);
            }
        }
    }
}
