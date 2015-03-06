package com.interview.algorithms.heap;

import java.util.Comparator;

/**
 * Created_By: zouzhile
 * Date: 10/26/14
 * Time: 9:24 AM
 */
public class Heap {

    private int capacity = 0;
    private int[] buffer;
    private int size;
    private Comparator<Integer> comparator;

    public Heap(int capacity, Comparator<Integer> comparator) {
        this.comparator = comparator;
        this.capacity = capacity;
        this.buffer = new int[this.capacity + 1]; // spare buffer[0]
    }

    public Heap(int[] buffer, Comparator<Integer> comparator) {
        this.comparator = comparator;
        this.capacity = buffer.length * 2; // spare buffer[0]
        this.size = buffer.length;
        this.buffer = new int[capacity + 1];
        for(int i = 0; i < buffer.length; i ++)
            this.buffer[i+1] = buffer[i]; // buffer[0] is not used

        int nonLeafNodeIndex = this.size / 2; // the last
        for(int i = nonLeafNodeIndex; i >= 1; i --)
            this.swim(i);
    }

    public void insert(int value) {
        if(this.size >= this.capacity) {
            this.capacity *= 2;
            int[] buffer = new int[this.capacity + 1];
            for(int i = 1; i <= this.size; i ++) {
                buffer[i] = this.buffer[i];
            }
            this.buffer = buffer;
        }
        this.buffer[++size] = value;
        this.swim(size/2);
    }

    public int remove() {
        int top = this.buffer[1];
        this.buffer[1] = this.buffer[this.size--];
        this.sink(1);
        return top;
    }

    public void sink(int index) {
        while(index <= this.size/2) { // loop until last non leaf child
            // Compare the left and right child
            // to see which one should be exchanged with current node
            int exchangeChildIndex = 2 * index;
            int exchangeChild = this.buffer[2 * index];
            if(2 * index + 1 <= this.size) {
                if(this.comparator.compare(exchangeChild, this.buffer[2 * index + 1]) < 0) {
                    exchangeChildIndex = 2 * index + 1;
                    exchangeChild = this.buffer[exchangeChildIndex];
                }
            }

            if(this.comparator.compare(this.buffer[index], exchangeChild) >= 0)  // heap condition met
                break;
            else {
                this.swap(this.buffer, index, exchangeChildIndex);
                index = exchangeChildIndex;
            }
        }
    }

    public void swim(int index) {
        while(index >= 1 && 2 * index <= this.size) { // loop until first node
            // Compare the left and right child
            // to see which one should be exchanged with current node
            int exchangeChildIndex = 2 * index;
            int exchangeChild = this.buffer[2 * index];
            if(2 * index + 1 <= this.size) {
                if(this.comparator.compare(exchangeChild, this.buffer[2 * index + 1]) < 0) {
                    exchangeChildIndex = 2 * index + 1;
                    exchangeChild = this.buffer[exchangeChildIndex];
                }
            }

            if(this.comparator.compare(this.buffer[index], exchangeChild) >= 0)  // heap condition met
                break;
            else {
                this.swap(this.buffer, index, exchangeChildIndex);
                index /= 2;
            }
        }
    }

    private void swap(int[] array, int i, int j) {
        int tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }

    public int size() {
        return this.size;
    }
}
