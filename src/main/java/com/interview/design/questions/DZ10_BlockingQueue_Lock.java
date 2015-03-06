package com.interview.design.questions;

import com.interview.basics.java.cocurrency.blocking.BlockingQueue;
import com.interview.basics.model.collection.queue.FixCapabilityArrayQueue;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 10/24/14
 * Time: 11:17 AM
 */
public class DZ10_BlockingQueue_Lock<T> extends FixCapabilityArrayQueue<T> implements BlockingQueue<T> {
    public ReentrantLock lock;
    public Condition full;
    public Condition empty;

    public DZ10_BlockingQueue_Lock(int N){
        super(N);
        lock = new ReentrantLock();
        full = lock.newCondition();
        empty = lock.newCondition();
    }

    @Override
    public void add(T item) throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while(size >= N){
                System.out.println("Queue is full");
                full.await();
            }
            super.push(item);
            empty.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while(size == 0){
                empty.await();
            }
            T element = super.pop();
            full.signal();
            return element;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } finally {
            lock.unlock();
        }
    }
}
