package com.interview.basics.model.collection.hash;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/15/14
 * Time: 3:14 PM
 */
public interface Set<T>{
    public void add(T element);
    public boolean contains(T element);
    public T remove(T element);
    public int size();
    public boolean isEmpty();
    public Iterator<T> iterator();
}
