package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-10-18
 * Time: 下午5:04
 */
public class C4_66_InPlaceMergeSortedArray {
    public static void merge(int[] a, int[] b, int n, int m){
        for(int k = m + n - 1; k >= 0 && n > 0; k--){
            if(n <= 0)                  a[k] = b[--m];
            else if(b[m-1] > a[n-1])    a[k] = b[--m];
            else                        a[k] = a[--n];
        }
    }
}
