package com.interview.algorithms.stackqueue;

import com.interview.basics.model.collection.heap.BinaryArrayHeap;
import com.interview.basics.model.collection.queue.Queue;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/25/14
 * Time: 4:53 PM
 *
 * Solution:
 *  Use a Linked List to save the Queue, and Max Head Heap to track the max element
 *      when push in, insert to tail and add to heap.
 *      when pop out, delete from head, and set the value to null in the heap.
 *          since delete element in heap is O(N).
 *      when get max, getHead in the heap until the head value is not null. (head.value == null means the element is deleted)
 *
 */
public class C7_9_MaxQueueWithHeap<T extends Comparable<T>> implements Queue<T>{
    private QueueNode<T> head;
    private QueueNode<T> tail;
    private int size;

    private BinaryArrayHeap<QueueNode<T>> heap = new BinaryArrayHeap<>();

    class QueueNode<T extends Comparable<T>> implements Comparable<QueueNode<T>>{
        T value;
        QueueNode<T> next;
        public QueueNode(T value){
            this.value = value;
        }

        @Override
        public int compareTo(QueueNode<T> o) {
            return this.value.compareTo(o.value);
        }
    }

    @Override
    public void push(T item) {
        QueueNode<T> node = new QueueNode<>(item);
        heap.add(node);
        if(head == null){
            head = tail = node;
            return;
        } else {
            tail.next = node;
            tail = node;
        }
        size++;
    }

    @Override
    public T pop() {
        if(head == null) return null;
        QueueNode<T> node = head;
        head = head.next;
        if(tail == node) tail = null;
        size--;
        //delete in the queue: just reset the node value to null
        T element = node.value;
        node.value = null;
        return element;
    }

    @Override
    public T peek() {
        if(head == null) return null;
        else return head.value;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    public T max(){
        if(heap.getHead() == null) return null;
        //if node value == null, means this node is already deleted
        while(heap.getHead().value == null) heap.pollHead();
        return heap.getHead().value;
    }
}
