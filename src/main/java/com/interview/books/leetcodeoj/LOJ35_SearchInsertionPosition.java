package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-19
 * Time: 下午9:38
 */
public class LOJ35_SearchInsertionPosition {
    //low < high
    //if(A[mid] >= target) high = mid; else low = mid + 1;
    //return (A[low] >= target)? low : low + 1;
    public int searchInsert(int[] A, int target) {
        int low = 0;
        int high = A.length - 1;
        while(low < high){
            int mid = low + (high - low)/2;
            if(A[mid] >= target) high = mid;
            else low = mid + 1;
        }
        return (A[low] >= target)? low : low + 1;
    }

    public static void main(String[] args){
        int[] num = new int[]{1,3};
        LOJ35_SearchInsertionPosition searcher = new LOJ35_SearchInsertionPosition();
        System.out.println(searcher.searchInsert(num, 0));
    }
}
