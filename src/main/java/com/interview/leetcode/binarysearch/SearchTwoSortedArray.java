package com.interview.leetcode.binarysearch;

/**
 * Created_By: stefanie
 * Date: 14-11-14
 * Time: 下午9:23
 *
 *  Solution: like "find k-th element in sorted array".
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
 *
 */
public class SearchTwoSortedArray {
    public static int topK(int[] a, int[] b, int k){
        if(k > a.length + b.length - 1) return Integer.MAX_VALUE;
        return findKth(a, b, k);
    }

    /**
     *  using the mid of short array to do binary search in long array.
     *  1. make sure a is the shorter array
     *  2. do binary search on A, and compare a[mid] and b[K - 1 - mid]
     *      a. b[K - 1 - mid] is out of range or bigger, a[0]...a[mid] is in topK, continue the search in a[mid + 1]..a[m]
     *      b. else continue the search in a[0]...a[mid]
     *  3. after binary search, the K-th element is the previous element of a[left],
     *     it is the larger one in a[left - 1] and b[K - 1 - left]
     *
     */
    //Time: O(log(min(m, n)),
    public static int findKth(int[] a, int[] b, int K){
        int m = a.length;
        int n = b.length;
        if(m > n) return findKth(b, a, K);   //make sure a is the shorter array
        int low = 0, high = m;
        //binary binarysearch the first element in a but not in TopK
        while(low < high){
            int mid = low + (high - low) / 2;   //find the mid in a
            int bIdx = K - 1 - mid;   //a[0]…a[mid] in TopK, b should have K - 1 - mid element
            if(bIdx >= n || a[mid] < b[bIdx]) low = mid + 1;   //if j is out of range or b[j] is larger, so a[0]…a[mid] in TopK, continue to search in a[mid + 1]..a[m]
            else high = mid;   //a[mid +1]…a[high] not in TopK, so binarysearch in a[0]…a[mid]
        }
        //the K-th element is the larger one in a[left - 1] and b[K - 1 - left]
        int keyA = low - 1 >= 0? a[low - 1] : Integer.MIN_VALUE;
        int keyB = K - 1 - low >= 0? b[K - 1 - low] : Integer.MIN_VALUE;
        return Math.max(keyA, keyB);
    }

    public double findMedian(int A[], int B[]) {
        int median = (A.length + B.length) / 2 + 1;
        if ((A.length + B.length) % 2 == 1) return findKth(A, B, median);
        else return (findKth(A, B, median - 1) + findKth(A, B, median)) / 2.0;
    }


}
