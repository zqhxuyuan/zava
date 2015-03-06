package com.interview.basics.model.collection.stack;

import com.interview.basics.model.collection.list.ArrayList;

/**
 * Created_By: stefanie
 * Date: 14-7-12
 * Time: 下午10:57
 */
public class ArrayStack<T> implements Stack<T> {
    ArrayList<T> array = new ArrayList<T>();

    @Override
    public void push(T item) {
        array.add(item);
    }

    @Override
    public T pop() {
        return array.remove(array.size() - 1);
    }

    @Override
    public T peek() {
        return array.get(array.size() - 1);
    }

    @Override
    public boolean isEmpty() {
        return array.isEmpty();
    }

    @Override
    public int size() {
        return array.size();
    }

    @Override
    public T get(int i) {
        return array.get(i);
    }


}
