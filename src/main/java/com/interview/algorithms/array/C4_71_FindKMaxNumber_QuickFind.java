package com.interview.algorithms.array;

import com.interview.utils.ArrayUtil;

/**
 * Created_By: stefanie
 * Date: 14-10-24
 * Time: 下午2:43
 */
public class C4_71_FindKMaxNumber_QuickFind {
    public static int[] find(int[] array, int k){
        find(array, k, 0, array.length - 1);
        return array;
    }

    private static void find(int[] array, int k, int low, int high){
        if(low >= high) return;
        int p = partition(array, low, high);
        if(p == k - 1 || p == k) return;
        else if(p > k) find(array, k, low, p - 1);
        else find(array, k, p + 1, high);
    }

    public static int partition(int[] array, int low, int high){
        if(low == high) return low;
        int i = low;
        for(int j = low + 1; j <= high; j++){
            if(array[j] > array[low]) ArrayUtil.swap(array, ++i, j);
        }
        ArrayUtil.swap(array, low, i);
        return i;
    }
}
