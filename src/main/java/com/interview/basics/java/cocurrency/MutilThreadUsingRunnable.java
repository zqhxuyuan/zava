package com.interview.basics.java.cocurrency;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 11/3/14
 * Time: 11:22 AM
 */
public class MutilThreadUsingRunnable {
    static class LiftOff implements Runnable{
        protected int countDown = 10;
        private static int taskCount = 0;
        private final int id = taskCount++;

        public LiftOff() {
        }

        public LiftOff(int countDown) {
            this.countDown = countDown;
        }

        public String status(){
            return "#" + id + "(" +
               (countDown > 0? countDown : "Liftoff!") + "), ";
        }

        @Override
        public void run() {
            while(countDown-- > 0){
                System.out.print(status());
                //Thread.yield();
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args){
//        for(int i = 0; i < 5; i++){
//            new Thread(new LiftOff()).start();
//        }
        //ExecutorService exec = Executors.newCachedThreadPool();
        ExecutorService exec = Executors.newFixedThreadPool(3);
        for(int i = 0; i < 5; i++)
            exec.execute(new LiftOff());
        System.out.println("Waiting for Liftoff!");
        exec.shutdown();
    }
}
