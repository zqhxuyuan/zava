package com.interview.basics.model.collection.heap;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 10/15/14
 * Time: 1:44 PM
 */
public interface Heap<T extends Comparable<T>> {
    public static int MAX_HEAD = 0;
    public static int MIN_HEAD = 1;
    public void add(T element);
    public T getHead();
    public T pollHead();
    public int size();
    public boolean contains(T k);
}
