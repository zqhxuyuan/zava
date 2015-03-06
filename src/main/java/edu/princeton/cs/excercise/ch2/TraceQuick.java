package edu.princeton.cs.excercise.ch2;

/*************************************************************************
 *  Compilation:  javac TraceQuick.java
 *  Execution:    java  TraceQuick input
 *
 *  Quicksort the sequence of strings specified as the command-line
 *  arguments and show the detailed trace.
 *
 *  % java TraceQuick QUICKSORTEXAMPLE
 *
 *************************************************************************/

import edu.princeton.cs.introcs.StdDraw;
import edu.princeton.cs.introcs.StdRandom;

import java.awt.Font;

public class TraceQuick {
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
        int j = partition(a, lo, hi);
        draw(a, lo, j, hi);
        line++;
        sort(a, lo, j-1);
        sort(a, j+1, hi);
    }

    private static int partition(String[] a, int lo, int hi) {
        int i = lo;
        int j = hi + 1;
        String v = a[lo];
        while (true) {

            // find item on lo to swap
            while (less(a[++i], v))
                if (i == hi) break;

            // find item on hi to swap
            while (less(v, a[--j]))
                if (j == lo) break;      // redundant since a[lo] acts as sentinel

            // check if pointers cross
            if (i >= j) break;

            exch(a, i, j);
        }

        // put v = a[j] into position
        exch(a, lo, j);

        // with a[lo .. j-1] <= a[j] <= a[j+1 .. hi]
        return j;
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
        StdDraw.text(-3.75, line, lo + "");
        StdDraw.text(-1.25, line, lo + "");
        for (int i = 0; i < a.length; i++) {
            if (i == lo)  StdDraw.setPenColor(StdDraw.BOOK_RED);
            else          StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
            StdDraw.text(i, line, a[i]);
        }
    }


    // draw the contents of the array, with a[lo] to a[hi] in black
    private static void draw(String[] a, int lo, int j, int hi) {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(-3.75, line, lo + "");
        StdDraw.text(-1.25, line, hi + "");
        StdDraw.setPenColor(StdDraw.BOOK_RED);
        StdDraw.text(-2.50, line, j  + "");
        for (int i = 0; i < a.length; i++) {
            if (i == j)                  StdDraw.setPenColor(StdDraw.BOOK_RED);
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
        StdDraw.text(-3.75, -3, "lo");
        StdDraw.text(-2.50, -3, "j");
        StdDraw.text(-1.25, -3, "hi");
        StdDraw.setPenColor(StdDraw.BOOK_RED);
        StdDraw.line(-4, -2.65, N-.5, -2.65);
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
        StdDraw.setCanvasSize(30*(N+3), 30*(N+3));
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