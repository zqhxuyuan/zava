package com.interview.basics.model.collection.stack;

public interface Stack<T> {

	public void push(T item);
	
	public T pop();
	
	public T peek();
	
	public boolean isEmpty();
	
	public int size();

    public T get(int i);

}
