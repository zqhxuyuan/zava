package edu.princeton.cs.excercise.ch2;

/*************************************************************************
 *  Compilation:  javac TraceQuick3way.java
 *  Execution:    java  TraceQuick3way string
 *
 *  Quicksort the sequence of strings specified as the command-line
 *  arguments and show the detailed trace.
 *
 *  % java TraceQuick3way QUICKSORTEXAMPLE
 *
 *************************************************************************/

import edu.princeton.cs.introcs.StdDraw;
import edu.princeton.cs.introcs.StdRandom;

import java.awt.Font;

public class TraceQuick3way {
    private static int line = 0;


    public static void sort(String[] a) {
        sort(a, 0, a.length - 1);
    }

    private static void sort(String[] a, int lo, int hi) {
        if (hi < lo) return;
        if (hi == lo) {
            draw(a, lo);
            line++;
            return;
        }

        int lt = lo, gt = hi;
        String v = a[lo];
        int i = lo;
        while (i <= gt) {
            int cmp = a[i].compareTo(v);
            if      (cmp < 0) exch(a, lt++, i++);
            else if (cmp > 0) exch(a, i, gt--);
            else              i++;
        }

        // a[lo..lt-1] < v = a[lt..gt] < a[gt+1..hi].
        draw(a, lo, lt, gt, hi);
        line++;

        sort(a, lo, lt-1);
        sort(a, gt+1, hi);
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

    // draw the contents of the array, with a[lo] in red
    private static void draw(String[] a, int lo) {
        StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
        StdDraw.text(-5.00, line, lo + "");
        StdDraw.text(-1.25, line, lo + "");
        for (int i = 0; i < a.length; i++) {
            if (i == lo)  StdDraw.setPenColor(StdDraw.BOOK_RED);
            else          StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
            StdDraw.text(i, line, a[i]);
        }
    }

    // draw the contents of the array, with a[lo] to a[hi] in black
    private static void draw(String[] a, int lo, int lt, int gt, int hi) {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(-5.00, line, lo + "");
        StdDraw.text(-1.25, line, hi + "");
        StdDraw.setPenColor(StdDraw.BOOK_RED);
        StdDraw.text(-3.75, line, lt + "");
        StdDraw.text(-2.50, line, gt + "");
        for (int i = 0; i < a.length; i++) {
            if (i >= lt && i <= gt)      StdDraw.setPenColor(StdDraw.BOOK_RED);
            else if (i >= lo && i <= hi) StdDraw.setPenColor(StdDraw.BLACK);
            else                         StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
            StdDraw.text(i, line, a[i]);
        }
    }

    // display header
    private static void header(String[] a) {
        int N = a.length;
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(N/2.0, -4, "a[ ]");
        for (int i = 0; i < N; i++)
            StdDraw.text(i, -3, i + "");
        StdDraw.text(-5.00, -3, "lo");
        StdDraw.text(-3.75, -3, "lt");
        StdDraw.text(-2.50, -3, "gt");
        StdDraw.text(-1.25, -3, "hi");
        StdDraw.setPenColor(StdDraw.BOOK_RED);
        StdDraw.line(-5.25, -2.65, N-.5, -2.65);
        StdDraw.setPenColor(StdDraw.BLACK);
        for (int i = 0; i < a.length; i++)
            StdDraw.text(i, -2, a[i]);
        StdRandom.shuffle(a);
        for (int i = 0; i < a.length; i++)
            StdDraw.text(i, -1, a[i]);
    }

    // display footer
    private static void footer(String[] a) {
        StdDraw.setPenColor(StdDraw.BLACK);
        for (int i = 0; i < a.length; i++)
            StdDraw.text(i, line, a[i]);
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
        StdDraw.setCanvasSize(30*(N+4), 30*(N+4));
        StdDraw.setXscale(-5, N+1);
        StdDraw.setYscale(N+1, -5);
        StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 13));

        // display the header
        header(a);

        // sort the array and display trace
        sort(a);

        // display the footer
        footer(a);
    }

}