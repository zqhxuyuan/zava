package com.interview.books.ninechapter;

import com.interview.utils.ConsoleWriter;

import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * Created_By: stefanie
 * Date: 14-12-11
 * Time: 下午11:05
 */
public class NC1_TopKPairSum {
    class Item implements Comparable<Item>{
        int offsetA;
        int offsetB;
        int value;

        Item(int offsetA, int offsetB, int value) {
            this.offsetA = offsetA;
            this.offsetB = offsetB;
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            Item item = (Item) obj;
            if(item.offsetA == this.offsetA && item.offsetB == this.offsetB) return true;
            else return false;
        }

        @Override
        public int compareTo(Item o) {
            return o.value - value;
        }
    }
    public int[] topK(int[] A, int[] B){
        int[] pairSum = new int[A.length];
        Arrays.sort(A);
        Arrays.sort(B);

        PriorityQueue<Item> heap = new PriorityQueue<>(A.length);
        heap.add(new Item(A.length - 1, B.length - 1, A[A.length - 1] + B[B.length - 1]));
        int offset = 0;
        while(offset < A.length){
            Item item = heap.poll();
            pairSum[offset++] = item.value;
            if(item.offsetA - 1 >= 0){
                Item next = new Item(item.offsetA - 1, item.offsetB, A[item.offsetA - 1] + B[item.offsetB]);
                if(!heap.contains(next)) heap.add(next);
            }
            if(item.offsetB - 1 >= 0){
                Item next = new Item(item.offsetA, item.offsetB - 1, A[item.offsetA] + B[item.offsetB - 1]);
                if(!heap.contains(next)) heap.add(next);
            }

        }
        return pairSum;
    }

    public static void main(String[] args){
        int[] A = new int[]{1,3,4,5,6};
        int[] B = new int[]{2,3,4,6,7};
        NC1_TopKPairSum finder = new NC1_TopKPairSum();
        int[] pair = finder.topK(A, B);
        ConsoleWriter.printIntArray(pair);
    }
}
