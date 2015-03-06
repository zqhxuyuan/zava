package com.interview.basics.model.collection.queue;

/**
 * Created_By: stefanie
 * Date: 14-7-13
 * Time: 下午10:13
 */
public class FixCapabilityArrayQueue<T> implements Queue<T> {
    protected int N = 10;
    protected T[] array = (T[]) new Object[N];

    protected int head = 0;
    protected int tail = 0;
    protected int size = 0;

    public FixCapabilityArrayQueue(int n){
        this.N = n;
    }

    public FixCapabilityArrayQueue(){
    }

    @Override
    public void push(T item) {
        if(size < N){
            array[tail] = item;
            tail = (tail+1) % N;
            size++;
        } else {
            System.err.println("Stack is full");
        }
    }

    @Override
    public T pop() {
        if(size == 0){
            return null;
        } else {
            T element = array[head];
            head = (head + 1) % N;
            size--;
            return element;
        }
    }

    @Override
    public T peek() {
        if(size == 0)   return null;
        else            return array[head];
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }
}
