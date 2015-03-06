package com.interview.basics.model.collection.queue;

import com.interview.basics.model.collection.list.Node;

/**
 * Created_By: stefanie
 * Date: 14-7-13
 * Time: 下午10:01
 */
public class LinkedQueue<T> implements Queue<T> {
    Node<T> head;
    Node<T> tail;
    int size;

    @Override
    public void push(T item) {
        Node<T> node = new Node<T>(item);
        if(head == null || tail == null){
            tail = node;
            head = tail;
        } else {
            tail.next = node;
            tail = node;
        }
        size++;
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
    public T peek() {
        return this.head == null ? null : this.head.item;
    }
}
