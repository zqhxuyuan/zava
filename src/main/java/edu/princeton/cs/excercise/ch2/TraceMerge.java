package edu.princeton.cs.excercise.ch2;

/*************************************************************************
 *  Compilation:  javac TraceMerge.java
 *  Execution:    java  TraceMerge input
 *
 *  Mergesort the sequence of strings specified as the command-line
 *  arguments and show the detailed trace.
 *
 *  % java TraceMerge MERGESORTEXAMPLE
 *
 *************************************************************************/

import edu.princeton.cs.introcs.StdDraw;

import java.awt.Font;

public class TraceMerge {
    private static int line = 0;

    private static void merge(String[] a, String[] aux, int lo, int m, int hi) {

        // copy to aux[]
        for (int k = lo; k <= hi; k++) {
            aux[k] = a[k];
        }

        // merge back to a[]
        int i = lo, j = m+1;
        for (int k = lo; k <= hi; k++) {
            if      (i > m)                a[k] = aux[j++];
            else if (j > hi)               a[k] = aux[i++];
            else if (less(aux[j], aux[i])) a[k] = aux[j++];
            else                           a[k] = aux[i++];
        }

    }

    // mergesort a[lo..hi] using auxiliary array aux[lo..hi]
    private static void sort(String[] a, String[] aux, int lo, int hi) {
        if (hi <= lo) return;
        int m = lo + (hi - lo) / 2;
        sort(a, aux, lo, m);
        sort(a, aux, m + 1, hi);
        merge(a, aux, lo, m, hi);
        draw(a, lo, m, hi);
        line++;
    }

    public static void sort(String[] a) {
        String[] aux = new String[a.length];
        sort(a, aux, 0, a.length-1);
    }

    // exchange a[i] and a[j]
    private static void exch(String[] a, int i, int j) {
        String swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

    // is v < w ?
    private static boolean less(String v, String w) {
        return (v.compareTo(w) < 0);
    }


    // draw the contents of the array, with a[lo] to a[hi] in black
    private static void draw(String[] a, int lo, int m, int hi) {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(-2.50, line, m  + "");
        StdDraw.setPenColor(StdDraw.BOOK_RED);
        StdDraw.text(-3.75, line, lo + "");
        StdDraw.text(-1.25, line, hi + "");
        for (int i = 0; i < a.length; i++) {
            if (i >= lo && i <= hi) StdDraw.setPenColor(StdDraw.BLACK);
            else                    StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
            StdDraw.text(i, line, a[i]);
        }
    }

    // display header
    private static void header(String[] a) {
        int N = a.length;
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(N/2.0, -3, "a[ ]");
        for (int i = 0; i < N; i++)
            StdDraw.text(i, -2, i + "");
        StdDraw.text(-3.75, -2, "lo");
        StdDraw.text(-2.50, -2, "m");
        StdDraw.text(-1.25, -2, "hi");
        StdDraw.setPenColor(StdDraw.BOOK_RED);
        StdDraw.line(-4, -1.65, N-.5, -1.65);
        StdDraw.setPenColor(StdDraw.BLACK);
        for (int i = 0; i < a.length; i++)
            StdDraw.text(i, -1, a[i]);
    }

    // display footer
    private static void footer(String[] a) {
        int N = a.length;
        StdDraw.setPenColor(StdDraw.BLACK);
        for (int i = 0; i < a.length; i++)
            StdDraw.text(i, N-1, a[i]);
    }


    // test client
    public static void main(String[] args) {

        // parse command-line argument as an array of 1-character strings
        String s = args[0];
        int N = s.length();
        String[] a = new String[N];
        for (int i = 0; i < N; i++)
            a[i] = s.substring(i, i+1);

        // set canvas size
        StdDraw.setCanvasSize(30 * (N + 3), 30 * (N + 3));
        StdDraw.setXscale(-4, N+1);
        StdDraw.setYscale(N+1, -4);
        StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 13));

        // display the header
        header(a);

        // sort the array and display trace
        sort(a);

        // display the footer
        footer(a);
    }

}