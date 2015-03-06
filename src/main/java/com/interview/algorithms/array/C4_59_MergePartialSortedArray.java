package com.interview.algorithms.array;

import com.interview.utils.ArrayUtil;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/17/14
 * Time: 3:49 PM
 *
 * Solution:
 * 1. mergeByInsert:
 *    i as the A start, j as the B start
 *    find j's right place in A, and insert by move A right-wise by one offset.
 * 2. mergeByRotation:
 *    a as the A start, b as the B start
 *    find the first element A[i] in A larger than B[0]
 *    find the first element B[j] in B larger than A[i]
 *    then put B[0]..B[j-1] before A[i] using rotation:
 *      rotate A[i]...B[j-1]
 *      rotate B[0]...B[j-1]
 *      rotate A[i]...A[N]
 *    update A start A[i] and B start B[j]
 */
public class C4_59_MergePartialSortedArray {

    public static void merge(int[] array, int split){
        mergeByRotation(array, 0, split);
    }

    public static void mergeByInsert(int[] array, int split){
        int i = 0;
        int j = split;
        while(j < array.length){
            while(i < j && array[i] <= array[j]) i++;
            if(i >= j) break;
            ArrayUtil.insertBefore(array, i, j++);
        }
    }

    public static void mergeByRotation(int[] array, int a, int b){
        while(a < b && b < array.length){
            int i = a;
            int j = b;
            while(i < j && array[i] <= array[j]) i++;
            while(j < array.length && array[i] > array[j]) j++;

            ArrayUtil.reverse(array, i, j - 1);
            int mid = i + (j - b);
            ArrayUtil.reverse(array, i, mid - 1);
            ArrayUtil.reverse(array, mid, j - 1);
            a = mid;
            b = j;
        }
    }

}
