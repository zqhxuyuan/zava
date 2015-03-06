package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-19
 * Time: 下午8:39
 */
public class LOJ34_SearchForARange {
    //search first and last, high = mid and low = mid + 1;
    //first:  return A[low] == target? low : -1;
    //second: return A[high] == target? high : high - 1;
    public int[] searchRange(int[] A, int target) {
        if(A.length == 0) return new int[]{-1,-1};
        int first = searchFirst(A, target);
        if(first == -1) return new int[]{-1,-1};
        int last = searchLast(A, target);
        return new int[]{ first, last };
    }

    public int searchFirst(int[] A, int target){
        int low = 0;
        int high = A.length - 1;
        while(low < high){
            int mid = low + (high - low)/2;
            if(A[mid] >= target) high = mid;
            else low = mid + 1;
        }
        return A[low] == target? low : -1;
    }

    public int searchLast(int[] A, int target){
        int low = 0;
        int high = A.length - 1;
        while(low < high){
            int mid = low + (high - low)/2;
            if(A[mid] <= target) low = mid + 1;
            else high = mid;
        }
        return A[high] == target? high : high - 1;
    }
}
