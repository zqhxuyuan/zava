package com.interview.algorithms.array;

import com.interview.utils.ArrayUtil;

/**
 * Created_By: stefanie
 * Date: 14-10-24
 * Time: 下午2:38
 */
public class C4_71_FindKMaxNumber_Heap {
    public static int[] find(int[] array, int k){
        int[] minHeap = new int[k];
        for(int i = 0; i < k; i++) {
            minHeap[i] = array[i];
            swim(minHeap, i);
        }
        for(int i = k; i < array.length; i++){
            if(array[i] > minHeap[0]) {
                minHeap[0] = array[i];
                sink(minHeap, 0);
            }

        }
        return minHeap;
    }

    private static void swim(int[] heap, int i){
        int p = (i - 1) / 2;
        while(p >= 0 && heap[p] > heap[i]){
            ArrayUtil.swap(heap, p, i);
            i = p;
            p = (i - 1)/2;
        }
    }

    private static void sink(int[] heap, int i){
        int smaller = 2 * i + 1;
        while(smaller < heap.length){
            if(smaller + 1 < heap.length && heap[smaller + 1] < heap[smaller]) smaller++;
            if(heap[smaller] >= heap[i]) break;
            ArrayUtil.swap(heap, smaller, i);
            i = smaller;
            smaller = 2 * i + 1;
        }
    }
}
