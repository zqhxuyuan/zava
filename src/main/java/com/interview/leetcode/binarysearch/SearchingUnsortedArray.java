package com.interview.leetcode.binarysearch;

import com.interview.utils.ArrayUtil;

import java.util.Random;

/**
 * Created_By: stefanie
 * Date: 14-11-14
 * Time: 下午8:11
 */
public class SearchingUnsortedArray {
    static Random RAND = new Random();

    public static boolean find(int[] array, int target){
        return find(array, target, 0, array.length - 1);
    }

    private static boolean find(int[] array, int target, int low, int high){
        if(low > high) return false;
        int pivot = low;
        //if(high - low > 0)  pivot = low + RAND.nextInt(high - low); //random select one between low ~ high.
        if(array[pivot] == target) return true;
        pivot = partition(array, low, pivot, high);
        if(target < array[pivot]) return find(array, target, low, pivot - 1);
        else return find(array, target, pivot + 1, high);
    }

    private static int partition(int[] array, int low, int pivot, int high){
        ArrayUtil.swap(array, low, pivot);
        int i = low;
        for(int j = low + 1; j <= high; j++){
            if(array[j] < array[low]) ArrayUtil.swap(array, j, ++i);
        }
        ArrayUtil.swap(array, low, i);
        return i;
    }

    public static int topK(int[] array, int k){
        return topK(array, k, 0, array.length - 1);
    }

    private static int topK(int[] array, int k, int low, int high){
        if(low > high) return -1;
        int pivot = low;
        //if(high - low > 0)  pivot = low + RAND.nextInt(high - low); //random select one between low ~ high.
        pivot = partition(array, low, pivot, high);
        if(pivot == k) return array[pivot];
        else if(pivot > k) return topK(array, k, low, pivot - 1);
        else return topK(array, k, pivot + 1, high);
    }

}
