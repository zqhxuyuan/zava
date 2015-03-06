package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-28
 * Time: 下午4:23
 */
public class LOJ152_MaximumProductSubarray {
    //scan backward and forward tracking products. if product == 0, reset to 1.
    //tracking max which is bigger one of backward and forward
    public int maxProduct(int[] A) {
        int max = Integer.MIN_VALUE;
        int forward = 1;
        int backward = 1;
        for(int i = 0; i < A.length; i++){
            forward *= A[i];
            backward *= A[A.length - 1 - i];
            int bigger = Math.max(forward, backward);
            max = Math.max(max, bigger);
            if(forward == 0)  forward = 1;
            if(backward == 0) backward = 1;
        }
        return max;
    }
}
