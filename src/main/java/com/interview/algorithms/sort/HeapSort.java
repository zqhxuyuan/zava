package com.interview.algorithms.sort;

import com.interview.algorithms.heap.Heap;
import com.interview.algorithms.heap.MaxHeapComparator;
import com.interview.algorithms.heap.MinHeapComparator;

import java.util.Comparator;

/**
 * Created_By: zouzhile
 * Date: 10/26/14
 * Time: 3:09 PM
 */
public class HeapSort {

    public void sort(int[] array, boolean maxHeap) {
        Comparator comparator = new MinHeapComparator();
        if(maxHeap)
            comparator = new MaxHeapComparator();
        Heap heap = new Heap(array, comparator);
        while(heap.size() > 0) {
            System.out.print(heap.remove() + " ");
        }
    }

    public static void main(String[] args) {
        int[] array = new int[] {4, 3, 9, 8, 5};
        HeapSort sorter = new HeapSort();
        sorter.sort(array, false);
    }
}
