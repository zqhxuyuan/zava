package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-19
 * Time: 下午9:06
 */
public class LOJ33_SearchInRotatedArray {
    //low, mid, high is offset, not elements
    //if(A[low] <= A[mid] && target < A[low])
    //if(A[high] >= A[mid] && target > A[high])
    //de-dup by while(low < high && A[low] == A[high]) high--;
    public int search(int[] A, int target) {
        int low = 0;
        int high = A.length - 1;
        while(low <= high){
            while(low < high && A[low] == A[high]) high--;
            int mid = low + (high - low)/2;
            if(A[mid] == target) return mid;
            else if(A[mid] > target){
                if(A[low] <= A[mid] && target < A[low]) low = mid + 1;
                else high = mid - 1;
            } else {
                if(A[high] >= A[mid] && target > A[high]) high = mid - 1;
                else low = mid + 1;
            }
        }
        return -1;
    }
}
