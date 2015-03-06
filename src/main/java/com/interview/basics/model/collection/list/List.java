package com.interview.basics.model.collection.list;

import java.util.Iterator;

/**
 * Created_By: stefanie
 * Date: 14-7-1
 * Time: 下午10:05
 */
public interface List<T> {
    public void add(T element);
    public void add(int index, T element);
    public T get(int index);
    public void set(int index, T element);
    public int indexOf(T element);
    public boolean contains(T element);
    public T remove(int index);
    public T remove(T element);
    public int size();
    public boolean isEmpty();
    public T[] toArray();
    public Iterator<T> iterator();
    public void addAll(List<T> list);
    public void clear();
}
