package com.interview.basics.model.collection.queue;

/**
 * Created_By: stefanie
 * Date: 14-7-12
 * Time: 下午11:15
 */
public interface Queue<T> {

    public void push(T item);

    public T pop();

    public T peek();

    public boolean isEmpty();

    public int size();
}
