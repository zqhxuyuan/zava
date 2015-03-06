package com.interview.basics.java.cocurrency.blocking;


import com.interview.basics.model.collection.queue.Queue;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 10/24/14
 * Time: 11:22 AM
 */
public interface BlockingQueue<T> extends Queue<T> {
    public void add(T item) throws InterruptedException;
    public T take() throws InterruptedException;
}
