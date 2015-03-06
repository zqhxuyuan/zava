package com.interview.basics.java.cocurrency.blocking;

import com.interview.basics.model.collection.queue.FixCapabilityArrayQueue;

/**
 * Created_By: stefanie
 * Date: 14-10-23
 * Time: 下午7:11
 */
public class BlockingFixCapabilityQueue<T> extends FixCapabilityArrayQueue<T> {
    Boolean pushFlag = true;
    Boolean popFlag = true;

    public BlockingFixCapabilityQueue(int n) {
        super(n);
    }

    @Override
    public void push(T item) {
        try {
            synchronized (pushFlag) {
                while(size >= N){
                    System.out.println("Queue is full");
                    pushFlag.wait();
                }
                super.push(item);
                popFlag.notifyAll();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public T pop() {
        try {
            synchronized (popFlag){
                while(size <= 0) {
                    System.out.println("Queue is empty");
                    popFlag.wait();
                }
                pushFlag.notifyAll();
                return super.pop();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
