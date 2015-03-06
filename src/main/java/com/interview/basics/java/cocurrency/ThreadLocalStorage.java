package com.interview.basics.java.cocurrency;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 12/24/14
 * Time: 2:20 PM
 */
public class ThreadLocalStorage{
    static class Accessor implements Runnable{
        private final int id;

        public Accessor(int idn){
            this.id = idn;
        }
        @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                ThreadLocalVariableHolder.increment();
                System.out.println(this);
                Thread.yield();
            }
        }

        public String toString(){
            return "#" + id + ": " + ThreadLocalVariableHolder.get();
        }
    }

    static class ThreadLocalVariableHolder {
        private static ThreadLocal<Integer> value = new ThreadLocal<Integer>(){
            private Random rand = new Random(47);
            protected synchronized Integer initialValue(){
                return rand.nextInt(10000);
            }
        };
        public static void increment(){
            value.set(value.get() + 1);
        }

        public static int get(){
            return value.get();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();
        for(int i = 0; i < 5; i++){
            exec.execute(new Accessor(i));
        }
        TimeUnit.SECONDS.sleep(3);
        exec.shutdownNow();
    }
}
