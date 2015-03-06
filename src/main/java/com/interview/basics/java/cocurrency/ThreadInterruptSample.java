package com.interview.basics.java.cocurrency;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 12/24/14
 * Time: 2:55 PM
 * <p/>
 * IOBlockedTask and SynchronizedBlockedTask can't be interrupted to shutdown.
 * For IOBlockedTask, we could close IO stream to shutdown the thread.
 * And in NIO, the blocked IO can handle interruption.
 * For SynchronizedBlockedTask, it can't be interrupted, but ReentrantLock can using lock.lockInterruptibly().
 */
public class ThreadInterruptSample {
    static class SleepBlockedTask implements Runnable {
        @Override
        public void run() {
            try {
                System.out.println("Sleep 100 seconds");
                TimeUnit.SECONDS.sleep(100);
            } catch (InterruptedException e) {
                System.err.println("SleepBlockedTask is interrupted");
            }
            System.out.println("Exit from SleepBlockedTask");
        }
    }

    static class IOBlockedTask implements Runnable {
        private InputStream in;

        public IOBlockedTask(InputStream is) {
            in = is;
        }

        @Override
        public void run() {
            try {
                System.out.println("Waiting for read()");
                in.read();
            } catch (IOException e) {
                if (Thread.currentThread().isInterrupted()) {
                    System.err.println("IOBlockedTask is interrupted");
                } else {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("Exit from IOBlockedTask");
        }
    }

    static class SynchronizedBlockedTask implements Runnable {
        public synchronized void f() {
            while (true) Thread.yield();
        }

        public SynchronizedBlockedTask() {
            new Thread() {
                public void run() {
                    f();
                }
            }.start();
        }

        @Override
        public void run() {
            System.out.println("SynchronizedBlockedTask Trying to call f()");
            f();
            System.out.println("Exit from SynchronizedBlockedTask");
        }
    }

    static class ReentrantLockBlockTask implements Runnable {
        private Lock lock = new ReentrantLock();

        public ReentrantLockBlockTask() {
            lock.lock();
        }

        public void f() {
            try {
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                System.err.println("ReentrantLockBlockTask is interrupted");
            }
            System.out.println("Exit from ReentrantLockBlockTask");
        }

        @Override
        public void run() {
            System.out.println("ReentrantLockBlockTask Trying to call f()");
            f();
        }
    }

    static class InterruptedTask implements Runnable {

        @Override
        public void run() {
            double d = 1.0;
            while (!Thread.interrupted()) { //
                for (int i = 0; i < 25000000; i++) d = d + (Math.PI + Math.E) / d;
                System.out.println(d);
            }
            System.err.println("InterruptedTask is interrupted");
            System.out.println("Exit from InterruptedTask");
        }
    }

    static class InterruptThread {
        public static void testThreadInterrupt(Runnable runnable) throws InterruptedException {
            Thread thread = new Thread(runnable);
            thread.start();
            TimeUnit.SECONDS.sleep(1);
            thread.interrupt();
        }

        public static void main(String[] args) throws InterruptedException {
            testThreadInterrupt(new SleepBlockedTask());
            testThreadInterrupt(new IOBlockedTask(System.in));
            testThreadInterrupt(new SynchronizedBlockedTask());
            TimeUnit.SECONDS.sleep(3);
            System.exit(0);
        }
    }

    static class InterruptExecutorService {
        public static void testInterruptExecutorService(Runnable runnable) throws InterruptedException {
            ExecutorService exec = Executors.newCachedThreadPool();
            Future<?> f = exec.submit(runnable);
            TimeUnit.SECONDS.sleep(1);
            f.cancel(true);
        }

        public static void main(String[] args) throws InterruptedException {
            testInterruptExecutorService(new SleepBlockedTask());
            testInterruptExecutorService(new IOBlockedTask(System.in));
            testInterruptExecutorService(new SynchronizedBlockedTask());
            testInterruptExecutorService(new ReentrantLockBlockTask());
            testInterruptExecutorService(new InterruptedTask());
            TimeUnit.SECONDS.sleep(3);
            System.exit(0);
        }
    }

    static class ShutdownIOBlockedTask {
        public static void main(String[] args) throws IOException, InterruptedException {
            ExecutorService exec = Executors.newCachedThreadPool();
            ServerSocket server = new ServerSocket(8080);
            InputStream socketInput = new Socket("localhost", 8080).getInputStream();

            Future<?> f = exec.submit(new IOBlockedTask(socketInput));
            TimeUnit.SECONDS.sleep(1);
            f.cancel(true);
            TimeUnit.SECONDS.sleep(1);
            System.out.println("Try closing input stream");
            socketInput.close();
            TimeUnit.SECONDS.sleep(5);
            System.exit(0);
        }
    }

}
