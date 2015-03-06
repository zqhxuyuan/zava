package com.interview.algorithms.array;

import com.interview.algorithms.general.C1_42_UglyNumber;
import com.interview.basics.model.collection.heap.BinaryArrayHeap;

/**
 * Created_By: stefanie
 * Date: 14-8-21
 * Time: 下午4:38
 */
public class C4_48_ABArray {

    public static int[] generate(int a, int b, int N){
        BinaryArrayHeap<Integer> minHeap = new C1_42_UglyNumber.UnduplicateBinaryArrayHeap<>(BinaryArrayHeap.MIN_HEAD);
        minHeap.add(a);
        minHeap.add(b);

        int[] array = new int[N];
        int i = 0;
        while(i < N){
            array[i] = minHeap.pollHead().intValue();
            if(array[i] % a == 0)   minHeap.add(array[i] + a);
            if(array[i] % b == 0)   minHeap.add(array[i] + b);
            i++;
        }
        return array;
    }
}
