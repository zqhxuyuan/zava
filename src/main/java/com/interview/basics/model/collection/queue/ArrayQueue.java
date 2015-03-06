package com.interview.basics.model.collection.queue;

import com.interview.basics.model.collection.list.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/14/14
 * Time: 10:43 AM
 */
public class ArrayQueue<T> extends FixCapabilityArrayQueue<T> {

    public ArrayQueue(int n){
        super(n);
    }

    public ArrayQueue(){
        super();
    }

    @Override
    public void push(T item) {
        if(size >= N) {
            expand();
        }
        super.push(item);
    }

    @Override
    public T pop() {
        T element = super.pop();
        if(size < N/4) {
            shrink();
        }
        return element;
    }

    private void expand(){
        N = 2 * N;
        copy();
    }

    private void shrink(){
        N = N / 2;
        copy();
    }

    private void copy(){
        T[] newArray = (T[]) new Object[N];
        for(int i = 0; i < size; i++){
            newArray[i] = this.array[(head+i)%N];
        }
        this.array = newArray;
        head = 0;
        tail = size -1;
    }
}
