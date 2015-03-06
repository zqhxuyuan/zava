package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-6-30
 * Time: 下午9:37
 *
 * Given a rotated sorted array, the element might appears in the order 3,4,5,6,7,1,2.
 * How would you find the minimum element
 */
public class C4_27_MinFinderInRotateSortedList {
    public static int findMin(int[] array){
        return findMin(array, 0, array.length - 1);
    }

    private static int findMin(int a[], int s, int t) {
        if (s == t || a[s] < a[t]) return a[s];
        int m = s + (t-s)/2;
        if (a[s]>a[m]) return findMin(a, s, m);
        else return findMin(a, m+1, t);
    }
}
