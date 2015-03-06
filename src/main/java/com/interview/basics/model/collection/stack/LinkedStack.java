package com.interview.basics.model.collection.stack;

import com.interview.basics.model.collection.list.Node;
import com.interview.basics.model.collection.stack.Stack;

public class LinkedStack<T> implements Stack<T> {
	
	private Node<T> head = null;
	private int size = 0;

	@Override
	public void push(T item) {
		Node<T> node = this.head;
		head = new Node<T>(item);
		head.next = node;
		this.size ++;
	}

	@Override
	public T pop() {
		if(this.head != null){
			T node = this.head.item;
			this.head = this.head.next;
			this.size --;
			return node;
		} else {
			return null;
		}
	}

	@Override
	public boolean isEmpty() {
		return this.head == null;
	}

	@Override
	public int size() {
		return this.size;
	}

    @Override
    public T get(int i) {
        if (i >= size) return null;
        Node<T> node = getNode(i);
        if (node != null) return node.item;
        else return null;
    }

    protected Node<T> getNode(int index) {
        int i = 0;
        Node<T> current = head;
        while (i++ < index) current = current.next;
        return current;
    }

    @Override
	public T peek() {
        return this.head == null ? null : this.head.item;
	}

}
