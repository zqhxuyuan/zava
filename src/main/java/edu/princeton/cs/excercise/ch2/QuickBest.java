package edu.princeton.cs.excercise.ch2;

import edu.princeton.cs.introcs.StdOut;

/*************************************************************************
 *  Compilation:  javac QuickBest.java
 *  Execution:    java QuickBest N
 *
 *  Generate a best-case input of size N for standard quicksort.
 *
 *  % java QuickBest 3
 *  BAC
 *
 *  % java QuickBest 7
 *  DACBFEG
 *
 *  % java QuickBest 15
 *  HACBFEGDLIKJNMO
 *
 *************************************************************************/

public class QuickBest {

    // postcondition: a[lo..hi] is best-case input for quicksorting that subarray
    private static void best(int[] a, int lo, int hi) {

        // precondition:  a[lo..hi] contains keys lo to hi, in order
        for (int i = lo; i <= hi; i++)
            assert a[i] == i;

        if (hi <= lo) return;
        int mid = lo + (hi - lo) / 2;
        best(a, lo, mid-1);
        best(a, mid+1, hi);
        exch(a, lo, mid);
    }

    public static int[] best(int N) {
        int[] a = new int[N];
        for (int i = 0; i < N; i++)
            a[i] = i;
        best(a, 0, N-1);
        return a;
    }

    // exchange a[i] and a[j]
    private static void exch(int[] a, int i, int j) {
        int swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }


    public static void main(String[] args) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        int N = 20;
        int[] a = best(N);
        for (int i = 0; i < N; i++)
            // StdOut.println(a[i]);
            StdOut.print(alphabet.charAt(a[i]));
        StdOut.println();
    }
}