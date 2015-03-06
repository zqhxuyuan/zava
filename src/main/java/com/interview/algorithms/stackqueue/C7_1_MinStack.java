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
public class C7_1_MinStack<T extends Comparable> implements Stack<T> {
    class NodeWithMin<T extends Comparable>{
        T element;
        T min;

        public NodeWithMin(T element, T currentMin){
            this.element = element;
            this.min = currentMin != null && element.compareTo(currentMin) > 0 ? currentMin : element;
        }
    }

    private Stack<NodeWithMin<T>> stack = new LinkedStack<NodeWithMin<T>>();

    @Override
    public void push(T item) {
        stack.push(new NodeWithMin<T>(item, min()));
    }

    @Override
    public T pop() {
        return stack.pop().element;
    }

    @Override
    public T peek() {
        return stack.peek().element;
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
        return stack.peek() == null? null : stack.peek().min;
    }


}
