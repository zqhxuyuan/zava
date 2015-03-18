package com.github.rfqu.juc;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/** 
 * http://habrahabr.ru/company/luxoft/blog/157273/#comment_6265995
 * @author Alexei Kaigorodov
 *
 */

interface ClassWithGetter {
    abstract int get();
}

class ClassWithGetterLock implements ClassWithGetter {
    Lock lock = new ReentrantLock();
    int value = 100;

    @Override
    public int get() {
        lock.lock();
        try {
            return value;
        } finally {
            lock.unlock();
        }
    }

}

class ClassWithGetterSync implements ClassWithGetter {
    int value = 100;

    @Override
    public synchronized int get() {
        return value;
    }

}

class ClassWithGetterNoSync implements ClassWithGetter {
    int value = 100;

    @Override
    public int get() {
        return value;
    }

}

class ClassWithGetterAt implements ClassWithGetter {
    AtomicInteger value = new AtomicInteger(1);

    @Override
    public int get() {
        return value.get();
    }
}

class ClassWithGetterAtSub extends AtomicInteger implements ClassWithGetter {
    
    ClassWithGetterAtSub() {
        super(100);
    }
}

class ClassWithGetterVol implements ClassWithGetter {
    volatile int value = 100;

    @Override
    public  int get() {
        return value;
    }

}

public class HabrGetterTest implements Runnable {
    static final int N = 100;
    final static int size = 100_000;
    ClassWithGetter obj;
    CountDownLatch latch = new CountDownLatch(N);
    CyclicBarrier barrier;
    volatile long start, end;
    int sum1;

    public HabrGetterTest(ClassWithGetter obj) {
        this.obj = obj;
    }

    public void run() {
        start = System.nanoTime();
    }

    public void test() throws Exception {
        final String simpleName = obj.getClass().getSimpleName();
        System.out.println("======================== " + simpleName);
        for (int j = 0; j < 4; j++) {
            start = System.nanoTime();
            for (int i = 0; i < size; i++) {
                sum1 = obj.get();
            }
            end = System.nanoTime();
            System.out.println("  ms: " + ((end - start) / 1_000_000));
        }

        System.out.println("\nStart measuring " + simpleName);
        barrier = new CyclicBarrier(N, this);
        for (int i = 0; i < N; ++i)
            new Thread(new Worker()).start();
        latch.await();
        end = System.nanoTime();
        final long el = (end - start);
        float ops=el*1.0f/(size*N);
        System.out.println("N=" + N + "; elapsed ms=" + (el / 1_000_000)+"; op (ns)="+ops);
    }

    class Worker implements Runnable {
        int sum;

        @Override
        public void run() {
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            }
            for (int i = 0; i < size; i++) {
                sum += obj.get();
            }
            latch.countDown();
        }

    }

    public static void main(String[] args) throws Exception {
        new HabrGetterTest(new ClassWithGetterNoSync()).test();
        new HabrGetterTest(new ClassWithGetterVol()).test();
        new HabrGetterTest(new ClassWithGetterAtSub()).test();
        new HabrGetterTest(new ClassWithGetterAt()).test();
        new HabrGetterTest(new ClassWithGetterLock()).test();
        new HabrGetterTest(new ClassWithGetterSync()).test();
        System.out.println(">>>> end");
    }
}