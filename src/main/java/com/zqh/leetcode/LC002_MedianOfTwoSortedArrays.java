package com.zqh.leetcode;

/**
 * Created by zqhxuyuan on 15-2-27.
 *
 * https://oj.leetcode.com/problems/median-of-two-sorted-arrays/
 *
 * There are two sorted arrays A and B of size m and n respectively.
 * Find the median of the two sorted arrays.
 * The overall run time complexity should be O(log (m+n)).
 */
public class LC002_MedianOfTwoSortedArrays {

    public static void main(String[] args) {
        // median of {3, 3, 5, 9, 11} is 5
        // the median of {3, 5, 7, 9} is (5 + 7) / 2 = 6 wikipedia
        LC002_MedianOfTwoSortedArrays s = new LC002_MedianOfTwoSortedArrays();
        double result = s.findMedianSortedArrays2(new int[]{3, 3, 5, 9, 11}, new int[]{3, 5, 7, 9});
        System.out.println(result);
    }

    //https://github.com/tg123
    int safe(int[] X, int i){
        if ( i < 0) return Integer.MIN_VALUE;
        if ( i >= X.length) return Integer.MAX_VALUE;
        return X[i];
    }

    int kth(int[] A, int[] B, int k){
        if (A.length == 0)
            return B[k];
        if (B.length == 0)
            return A[k];
        if (k == 0)
            return Math.min(A[0], B[0]);
        if (A.length == 1 && B.length == 1){
            // k must be 1
            return Math.max(A[0], B[0]);
        }
        int s = 0;
        int e = A.length;

        while ( s < e ){
            int m = (s + e) / 2;
            int n = k - m;
            if ( A[m] <= safe(B, n) ) {
                if (n == 0 || A[m] >= safe(B, n - 1)) {
                    return A[m];
                }
            }
            if ( safe(B, n) <= A[m] ){
                if (m == 0 || safe(B, n) >= A[m - 1]) {
                    return B[n];
                }
            }
            if ( A[m] < safe(B, n) ) {
                s = m + 1;
            } else {
                e = m;
            }
        }
        if (A[A.length - 1] < B[0]){
            return B[k - A.length];
        } else {
            return kth(B, A, k);
        }
    }

    public double findMedianSortedArrays(int A[], int B[]) {
        int s = A.length + B.length;
        final int k = s / 2;

        if(s % 2 == 1){
            return kth(A, B, k);
        }else{
            return (kth(A, B, k - 1) + kth(A, B, k)) / 2.0;
        }
    }

    //http://www.ninechapter.com/solutions/median-of-two-sorted-arrays/
    public double findMedianSortedArrays2(int A[], int B[]) {
        int len = A.length + B.length;
        if (len % 2 == 0) {
            return (findKth(A, 0, B, 0, len / 2) + findKth(A, 0, B, 0, len / 2 + 1)) / 2.0 ;
        } else {
            return findKth(A, 0, B, 0, len / 2 + 1);
        }
    }

    // find kth number of two sorted array
    public static int findKth(int[] A, int A_start, int[] B, int B_start, int k){
        if(A_start >= A.length)
            return B[B_start + k - 1];
        if(B_start >= B.length)
            return A[A_start + k - 1];

        if (k == 1)
            return Math.min(A[A_start], B[B_start]);

        int A_key = A_start + k / 2 - 1 < A.length
                ? A[A_start + k / 2 - 1]
                : Integer.MAX_VALUE;
        int B_key = B_start + k / 2 - 1 < B.length
                ? B[B_start + k / 2 - 1]
                : Integer.MAX_VALUE;

        if (A_key < B_key) {
            return findKth(A, A_start + k / 2, B, B_start, k - k / 2);
        } else {
            return findKth(A, A_start, B, B_start + k / 2, k - k / 2);
        }
    }
}
