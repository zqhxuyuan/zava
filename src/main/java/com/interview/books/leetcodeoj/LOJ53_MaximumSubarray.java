package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-22
 * Time: 下午5:33
 */
public class LOJ53_MaximumSubarray {
    //scan and tracking sum and max, if sum < 0, reset to 0.
    //if max == 0, return the largest elements in A.
    public int maxSubArray(int[] A) {
        int max = 0;
        int sum = 0;
        int largest = A[0];
        for(int i = 0; i < A.length; i++){
            largest = Math.max(largest, A[i]);
            sum = Math.max(sum + A[i], 0);
            max = Math.max(max, sum);
        }
        return max > 0? max : largest;
    }
}
