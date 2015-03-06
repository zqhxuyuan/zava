package com.interview.books.ccinterview;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/15/14
 * Time: 10:14 AM
 */
public class CC6_SetOfStack<T> extends Stack<T> {
    int N;
    int size;

    List<Stack<T>> stacks = new LinkedList<>();

    public CC6_SetOfStack(int n){
        this.N = n;
    }

    public CC6_SetOfStack(){
        this.N = 10;
    }

    private Stack<T> getCurrentStack(){
        return stacks.get(stacks.size() - 1);
    }

    @Override
    public T push(T item) {
        if(stacks.isEmpty() || getCurrentStack().size() == N)
            stacks.add(new Stack<T>());
        stacks.get(stacks.size() - 1).push(item);
        size++;
        return item;
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
}
