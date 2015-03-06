package com.interview.algorithms.stackqueue;

import com.interview.basics.model.collection.list.LinkedList;
import com.interview.basics.model.collection.list.List;
import com.interview.basics.model.collection.stack.FixCapabilityArrayStack;
import com.interview.basics.model.collection.stack.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/15/14
 * Time: 10:14 AM
 */
public class C7_5_SetOfStack<T> implements Stack<T> {
    int N;
    int size;

    List<Stack<T>> stacks = new LinkedList<>();

    public C7_5_SetOfStack(int n){
        this.N = n;
    }

    public C7_5_SetOfStack(){
        this.N = 10;
    }

    private Stack<T> getCurrentStack(){
        return stacks.get(stacks.size() - 1);
    }

    @Override
    public void push(T item) {
        if(stacks.isEmpty() || getCurrentStack().size() == N)
            stacks.add(new FixCapabilityArrayStack<T>(N));
        stacks.get(stacks.size() - 1).push(item);
        size++;
    }

    @Override
    public T pop() {
        Stack<T> current = getCurrentStack();
        T element = current.pop();
        while(current.isEmpty()) {
            stacks.remove(stacks.size() - 1);
            current = getCurrentStack();
        }
        size--;
        return element;
    }

    @Override
    public T peek() {
        return getCurrentStack().peek();
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    public T popAt(int i){
        if(i < stacks.size())   return stacks.get(i).pop();
        return null;
    }

    @Override
    public T get(int i) {
        return null;
    }
}
