package com.interview.algorithms.stackqueue;

import com.interview.basics.model.collection.queue.Queue;

import java.util.Stack;

/**
 * Created_By: stefanie
 * Date: 14-9-26
 * Time: 上午10:12
 */
public class C7_9_MaxQueueWithStack<T extends Comparable<T>> implements Queue<T> {
    class MaxStack<T extends Comparable<T>> extends Stack<T>{
        private Stack<T> maxStack = new Stack<>();
        T max;
        @Override
        public T push(T item) {
            super.push(item);
            if(max == null || item.compareTo(max) > 0) {
                maxStack.push(item);
                max = item;
            }
            else maxStack.push(max);
            return item;
        }

        @Override
        public synchronized T pop() {
            T element =  super.pop();
            maxStack.pop();
            return element;
        }

        public T max(){
            if(maxStack.empty()) return null;
            else return maxStack.peek();
        }
    }

    private MaxStack<T> aStack = new MaxStack<>();
    private MaxStack<T> bStack = new MaxStack<>();

    @Override
    public void push(T item) {
        aStack.push(item);
    }

    private void move(){
        while(!aStack.empty()){
            bStack.push(aStack.pop());
        }
    }

    @Override
    public T pop() {
        if(bStack.empty()) move();
        return bStack.pop();
    }

    @Override
    public T peek() {
        if(bStack.empty()) move();
        return bStack.peek();
    }

    @Override
    public boolean isEmpty() {
        return aStack.isEmpty() && bStack.isEmpty();
    }

    @Override
    public int size() {
        return aStack.size() + bStack.size();
    }

    public T max(){
        T aMax = aStack.max();
        T bMax = bStack.max();
        if(aMax == null && bMax == null) return null;
        else if(aMax != null && (bMax == null || aMax.compareTo(bMax) > 0)) return aMax;
        else return bMax;
    }
}
