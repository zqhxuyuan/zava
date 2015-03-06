package com.interview.leetcode.binarysearch;

/**
 * Created_By: stefanie
 * Date: 14-11-14
 * Time: 下午4:33
 */
public class SearchingSortedArrayWithDuplication {

    public static int searchLow(int[] A, int target){
        int low = 0;
        int high = A.length - 1;
        while(low < high){
            int mid = low + (high - low)/2;
            if(target <= A[mid]) high = mid;
            else low = mid + 1;
        }
        return A[low] == target? low : -1;    //if low != target, low is the first element larger than target
    }

    public static int searchHigh(int[] A, int target){
        int low = 0;
        int high = A.length - 1;
        while(low < high){
            int mid = low + (high - low)/2;
            if(target >= A[mid]) low = mid + 1;
            else high = mid - 1;
        }
        return A[high] == target? high : high - 1;   //if high != target, high is the first element larger than target
    }


    public static int[] searchRange(int[] A, int target) {
        int low = searchLow(A, target);
        if(low == -1) return new int[]{-1,-1};
        int high = searchHigh(A, target);
        return new int[]{low, high};
    }
}
