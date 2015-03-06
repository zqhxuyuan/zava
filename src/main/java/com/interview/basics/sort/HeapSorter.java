package com.interview.basics.sort;

import com.interview.basics.model.collection.heap.BinaryArrayHeap;

public class HeapSorter<T extends Comparable<T>> extends Sorter<T>{
	//use min head or max head, up=true is max head.
	public boolean up = true;

	public T[] sort(T[] input) {
        BinaryArrayHeap<T> heap;
        if (up) heap = new BinaryArrayHeap<>(BinaryArrayHeap.MAX_HEAD);
        else heap = new BinaryArrayHeap<>(BinaryArrayHeap.MIN_HEAD);

        int N = input.length;
        for (int i = 0; i < N; i++) heap.add(input[i]);
        for (int i = 0; i < N; i++)
            input[i] = heap.pollHead();
        return input;
    }
}
