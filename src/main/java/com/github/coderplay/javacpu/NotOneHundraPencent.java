package com.github.coderplay.javacpu;

/**
 * Created by zqhxuyuan on 15-3-28.
 */
public class NotOneHundraPencent implements Runnable {

    public static int NUM_THREADS = 4;

    public static void main(String[] args) throws InterruptedException{
        Thread[] threads = new Thread[NUM_THREADS];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new NotOneHundraPencent());
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.println("EOF");
    }

    @Override
    public void run() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
