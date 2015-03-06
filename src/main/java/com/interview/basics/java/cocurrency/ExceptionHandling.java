package com.interview.basics.java.cocurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 11/4/14
 * Time: 11:29 AM
 */
public class ExceptionHandling {
    static class ExceptionRunnable implements Runnable{
        @Override
        public void run() {
            throw new RuntimeException();
        }
    }

    static class ExceptionHandler implements Thread.UncaughtExceptionHandler{
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.out.println("catch an exception " + e.toString() + " from thread " + t.getName());
        }
    }

    static class HandlerThreadFactory implements ThreadFactory{
        public Thread newThread(Runnable r){
            Thread t = new Thread(r);
            t.setUncaughtExceptionHandler(new ExceptionHandler());
            return t;
        }
    }

    public static void main(String[] args){
//        try{
//            ExecutorService exec = Executors.newCachedThreadPool();
//            exec.execute(new ExceptionRunnable());
//        } catch(Exception e){
//            System.out.println("catch an exception " + e.getMessage());
//        }
        ExecutorService exec = Executors.newCachedThreadPool(new HandlerThreadFactory());
        exec.execute(new ExceptionRunnable());
    }
}
