package com.interview.algorithms.array;


import com.interview.basics.model.collection.heap.BinaryArrayHeap;

/**
 * Created_By: stefanie
 * Date: 14-9-8
 * Time: 上午9:45
 *
 * Given an int array A and a sliding window width w, scan the array with sliding window and keep the max value in the window to array B.
 * Write code to find B. B[i] should be the max value among A[i] ~ A[i + w - 1];
 *
 * 1. The straight forward solution: for each element move, find the max. it should be O(nw).
 * 2. Better way is to using a MAX-HEAD heap to tracking the max value in the sliding window.
 *    The problem is how to delete element not in the window when it is not the max.
 *    Define a data structure as IndexedNode to tracking the offset of the value in A,
 *    every time when pop head remove element not in the window by checking the offset is out of the window. O(nlgn)
 * 3. Using double-ended queue.     <TO BE ADDED>
 *    http://leetcode.com/2011/01/sliding-window-maximum.html
 */

public class C4_53_SlidingWindowMaximum {
    static class IndexedNode implements Comparable<IndexedNode>{
        int value;
        int index;

        public IndexedNode(int value, int index) {
            this.value = value;
            this.index = index;
        }

        @Override
        public int compareTo(IndexedNode o) {
            if(this.value > o.value) return 1;
            else if(this.value < o.value) return -1;
            else return 0;
        }
    }

    public static int[] find(int[] a, int w){
        BinaryArrayHeap<IndexedNode> heap = new BinaryArrayHeap<IndexedNode>();
        int[] b = new int[a.length - w + 1];
        for(int i = 0; i < w; i++) heap.add(new IndexedNode(a[i], i));
        for(int i = w; i < a.length; i++){
            IndexedNode max = heap.getHead();
            b[i - w] = max.value;
            while(max.index <= i - w){
                heap.pollHead();
                max = heap.getHead();
            }
            heap.add(new IndexedNode(a[i], i));
        }
        b[a.length - w] = heap.getHead().value;
        return b;
    }
}
