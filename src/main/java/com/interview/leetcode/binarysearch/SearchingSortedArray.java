package com.interview.leetcode.binarysearch;

/**
 * Created_By: stefanie
 * Date: 14-11-14
 * Time: 下午4:26
 */
public class SearchingSortedArray {

    public static int find(int[] array, int target){
        int lower = 0;
        int higher = array.length - 1;
        while(lower < higher) {
            int mid = (lower + higher) / 2;
            if(array[mid] == target) return mid;  //return when find one
            if (array[mid] < target) lower = mid + 1;
            else higher = mid;
        }
        return array[lower] == target ? lower : -1;
    }
}
