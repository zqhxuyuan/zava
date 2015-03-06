package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-11-10
 * Time: 上午2:22
 *
 * Solution: like "find k-th element in sorted array".
 *
 *  1. have lowA and lowB pointing to the begin of binarysearch area.
 *  2. if k == 1 return the min of A[lowA] and B[lowB]
 *  3. when k > 1, find the element offset is the k / 2 - 1 in A and B.
 *       int keyA = lowA + half - 1 < A.length? A[lowA + half - 1] : Integer.MAX_VALUE;
 *       int keyB = lowB + half - 1 < B.length? B[lowB + half - 1] : Integer.MAX_VALUE;
 *  4. if keyA > keyB, element smaller than k / 2 - 1 in A should smaller than the k-th element, should count in
 *       so search (k - k / 2)th element start from lowA + k / 2
 *     otherwise sink B from lowB + k / 2;
 *  5. checking up bound (array.length) to avoid overstep the boundary
 *
 *  Time: O(log(M + N))
 */
public class C4_79_MedianTwoSortedArray {
    public static int median(int[] A, int[] B){
        int length = A.length + B.length;
        if(length % 2 != 0) return findKth(A, 0, B, 0, length / 2 + 1);
        return (int)((findKth(A, 0, B, 0, length / 2) + findKth(A, 0, B, 0, length / 2 + 1)) / 2.0);
    }

    private static int findKth(int[] A, int lowA, int[] B, int lowB, int k){
        if(lowA >= A.length) return B[lowB + k - 1];
        if(lowB >= B.length) return A[lowA + k - 1];
        if(k == 1) return Math.min(A[lowA], B[lowB]);
        int half = k / 2;
        int keyA = lowA + half - 1 < A.length? A[lowA + half - 1] : Integer.MAX_VALUE;
        int keyB = lowB + half - 1 < B.length? B[lowB + half - 1] : Integer.MAX_VALUE;
        if(keyA < keyB) return findKth(A, lowA + half, B, lowB, k - half);
        else return findKth(A, lowA, B, lowB + half, k - half);
    }
}
