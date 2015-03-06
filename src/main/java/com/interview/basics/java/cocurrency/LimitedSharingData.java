package com.interview.basics.java.cocurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 11/4/14
 * Time: 1:31 PM
 */
public class LimitedSharingData {
    static abstract class IntGenerator{
        private volatile boolean cancelled = false;
        public abstract int next();
        public void cancel() {
            this.cancelled = true;
        }
        public boolean isCancelled(){
            return cancelled;
        }
    }

    static class UnsafeIntGenerator extends IntGenerator{
        private int base = 0;
        @Override
        public int next() {
            base ++;
            Thread.yield();
            base ++;
            return base;
        }
    }

    static class SynchronizedIntGenerator extends IntGenerator{
        int base = 0;

        @Override
        public synchronized int next() {
            while(base > 10000) return -1;
            base ++;
            Thread.yield();
            base ++;
            if(base == 652) throw new RuntimeException();
            return base;
        }
    }

    static class MutexIntGenerator extends IntGenerator{
        private int base = 0;
        private Lock lock = new ReentrantLock();
        @Override
        public int next() {
            while(base > 10000) return -1;
            lock.lock();
            try{
                base ++;
                Thread.yield();
                base ++;
                if(base == 652) throw new RuntimeException();
                return base;
            } finally {
                lock.unlock();
            }

        }
    }

    static class EverChecker implements Runnable{
        private IntGenerator generator;
        private final int id;

        public EverChecker(int id, IntGenerator generator) {
            this.id = id;
            this.generator = generator;
        }

        @Override
        public void run() {
            while(!generator.isCancelled()){
                int val = generator.next();
                if((val & 1) == 1){
                    System.out.println(val + " not even!");
                    generator.cancel();
                }
            }
        }
    }

    public static void test(IntGenerator generator, int count){
        ExecutorService exec = Executors.newCachedThreadPool();
        for(int i = 0; i < count; i++){
            exec.execute(new EverChecker(i, generator));
        }
        exec.shutdown();
    }

    public static void main(String[] args){
//        IntGenerator unsafe = new UnsafeIntGenerator();
//        test(unsafe, 10);

//        IntGenerator synchronizedIntGenerator = new SynchronizedIntGenerator();
//        test(synchronizedIntGenerator, 10);

        IntGenerator mutexIntGenerator = new MutexIntGenerator();
        test(mutexIntGenerator, 10);
    }
}
