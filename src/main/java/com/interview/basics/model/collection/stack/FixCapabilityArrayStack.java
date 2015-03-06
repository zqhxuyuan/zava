package com.interview.basics.model.collection.stack;

import com.interview.basics.model.collection.stack.Stack;

public class FixCapabilityArrayStack<T> implements Stack<T> {
	
	private T[] array;
	private int N;
	private int current = 0;
	
	@SuppressWarnings("unchecked")
	public FixCapabilityArrayStack(int N){
		this.N = N;
		this.array = (T[]) new Object[N];
	}

	@Override
	public void push(T item) {
		if(this.size() < N){
			this.array[current++] = item;
		} else {
			System.err.println("Stack is full");
		}
		
	}

	@Override
	public T pop() {
		if(!this.isEmpty()) return this.array[--current];
		else                return null;
	}

	@Override
	public boolean isEmpty() {
		return current == 0;
	}

	@Override
	public int size() {
		return current;
	}



    @Override
	public T peek() {
		if(!this.isEmpty()) return this.array[current-1];
        else                return null;
	}

    @Override
    public T get(int i) {
        return i < current? this.array[i] : null;
    }

}
