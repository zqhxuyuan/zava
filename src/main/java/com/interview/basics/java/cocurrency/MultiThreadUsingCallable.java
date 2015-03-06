package com.interview.basics.java.cocurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 11/3/14
 * Time: 1:15 PM
 */
public class MultiThreadUsingCallable {
    static class LiftOff implements Callable<String>{
        protected int countDown = 10;
        private static int taskCount = 0;
        private final int id = taskCount++;

        public LiftOff() {
        }

        public LiftOff(int countDown) {
            this.countDown = countDown;
        }

        @Override
        public String call() {
            return "#" + id + "(" +
                    (countDown > 0? countDown : "Liftoff!") + "), ";
        }
    }

    public static void main(String[] args){
//        for(int i = 0; i < 5; i++){
//            new Thread(new LiftOff()).start();
//        }
        //ExecutorService exec = Executors.newCachedThreadPool();
        ExecutorService exec = Executors.newFixedThreadPool(3);
        List<Future<String>> results = new ArrayList<Future<String>>();
        for(int i = 0; i < 5; i++){
            results.add(exec.submit(new LiftOff()));
        }
        System.out.println("Waiting for Liftoff!");
        for(Future<String> fs : results){
            try {
                System.out.println(fs.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } finally{
                exec.shutdown();
            }
        }
        exec.shutdown();
    }
}
