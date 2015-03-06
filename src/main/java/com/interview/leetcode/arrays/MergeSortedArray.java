package com.interview.leetcode.arrays;

import com.interview.leetcode.utils.IndexedValue;

import java.util.PriorityQueue;

/**
 * Created_By: stefanie
 * Date: 14-11-13
 * Time: 下午1:17
 *
 * Merge sorted arrays into one sorted array
 *
 * 1. given 2 sorted array, merge them into one    {@link #merge2(int[], int[])}
 * 2. given k sorted array, merge them into one    {@link #mergeK(int[][])}
 * 3. given sorted array A and B, merge B into A as one sorted list  {@link #merge2A(int[], int, int[], int)}
 *
 * Basic Tricks:
 *   1. Get the total length of array, using a offset in the final array,
 *   2. Select min of all these sorted arrays. (O(lg) to use heap)
 *
 */
public class MergeSortedArray {
    public static int[] merge2(int[] A, int[] B){
        int length = A.length + B.length;
        int[] merged = new int[length];
        int i = 0; //index of A
        int j = 0; //index of B
        for(int k = 0; k < length; k++){
            if(i >= A.length)       merged[k] = B[j++];
            else if(j >= B.length)  merged[k] = A[i++];
            else if(A[i] < B[j])    merged[k] = A[i++];
            else                    merged[k] = B[j++];
        }
        return merged;
    }

    public static void merge2A(int[] A, int m, int[] B, int n){
        int length = n + m;
        m--; n--;
        for(int k = length - 1; k >= 0; k--){
            if(n < 0)               break;
            else if(m < 0)          A[k] = B[n--];
            else if(A[m] > B[n])    A[k] = A[m--];
            else                    A[k] = B[n--];
        }
    }


    public static int[] mergeK(int[][] num){
        int length = 0;
        PriorityQueue<IndexedValue> heap = new PriorityQueue<>();
        int[] offsets = new int[num.length];
        for(int i = 0; i < num.length; i++){
            if(num[i].length > 0) heap.add(new IndexedValue(num[i][0], i));
            length += num[i].length;
            offsets[i] = 0;
        }
        int[] merged = new int[length];
        int offset = 0;
        while(!heap.isEmpty()){
            IndexedValue min = heap.poll();
            merged[offset++] = min.value;
            if(offsets[min.offset] < num[min.offset].length - 1){
                heap.add(new IndexedValue(num[min.offset][++offsets[min.offset]], min.offset));
            }

        }
        return merged;
    }

}
