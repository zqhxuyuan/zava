package com.interview.basics.search.array;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 10/17/14
 * Time: 4:39 PM
 */
public abstract class ArraySearcher<T extends Comparable<T>> {
    protected T[] input;

    protected ArraySearcher(T[] input) {
        this.input = input;
    }

    public void setInput(T[] input) {
        this.input = input;
    }

    public abstract T find(T element);
}
