package com.interview.algorithms.stackqueue;

import com.interview.basics.model.collection.stack.LinkedStack;
import com.interview.basics.model.collection.stack.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/14/14
 * Time: 11:47 AM
 *
 * Design a stack. We want to push, pop, and also, retrieve the minimum element in constant time.
 */
public class C7_1_MinStack_2Stack<T extends Comparable> implements Stack<T> {

    private Stack<T> stack = new LinkedStack<T>();
    private Stack<T> minStack = new LinkedStack<>();

    @Override
    public void push(T item) {
        stack.push(item);
        if(min() == null || item.compareTo(min()) <= 0) minStack.push(item);
    }

    @Override
    public T pop() {
        T element = stack.pop();
        if(element!= null && min() != null && element.compareTo(min()) == 0) minStack.pop();
        return element;
    }

    @Override
    public T peek() {
        return stack.peek();
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public int size() {
        return stack.size();
    }

    @Override
    public T get(int i) {
        return null;
    }

    public T min(){
        return minStack.peek();
    }


}
