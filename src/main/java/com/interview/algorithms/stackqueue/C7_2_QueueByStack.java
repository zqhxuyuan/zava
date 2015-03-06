package com.interview.algorithms.stackqueue;

import com.interview.basics.model.collection.queue.Queue;
import com.interview.basics.model.collection.stack.LinkedStack;
import com.interview.basics.model.collection.stack.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/14/14
 * Time: 4:35 PM
 */
public class C7_2_QueueByStack<T> implements Queue<T> {
    Stack<T> base = new LinkedStack<T>();
    Stack<T> backup = new LinkedStack<T>();

    @Override
    public void push(T item) {
        base.push(item);
    }

    @Override
    public T pop() {
        copy();
        return backup.pop();
    }

    @Override
    public T peek() {
        copy();
        return backup.peek();
    }

    @Override
    public boolean isEmpty() {
        return base.isEmpty() && backup.isEmpty();
    }

    @Override
    public int size() {
        return base.size() + backup.size();
    }

    private void copy(){
        if(backup.size() == 0)
            while(!base.isEmpty()) backup.push(base.pop());
    }
}
