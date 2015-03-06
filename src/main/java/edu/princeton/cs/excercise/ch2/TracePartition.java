package edu.princeton.cs.excercise.ch2;

/*************************************************************************
 *  Compilation:  javac TracePartition.java
 *  Execution:    java TracePartition string
 *
 *  Partitions the array of characters specified as the command-line
 *  argument and shows the detailed trace.
 *
 *  % java TracePartition KRATELEPUIMQCXOS
 *
 *  % java TracePartition AAAAAAAAAAAAAAAA
 *
 *************************************************************************/

import edu.princeton.cs.introcs.StdDraw;

import java.awt.Font;

public class TracePartition {
    private static int line = 0;

    private static int partition(String[] a) {
        int lo = 0, hi = a.length - 1;
        int i = lo;
        int j = hi + 1;
        String v = a[lo];
        while (true) {

            // find item on lo to swap
            int iold = i + 1;
            while (less(a[++i], v))
                if (i == hi) break;

            // find item on hi to swap
            int jold = j - 1;
            while (less(v, a[--j]))
                if (j == lo) break;      // redundant since a[lo] acts as sentinel

            // check if pointers cross
            if (i >= j) break;

            draw(a, iold, i, jold, j);
            line++;

            exch(a, i, j);
            draw(a, i, j);
            line++;
        }

        // put v = a[j] into position
        exch(a, lo, j);
        draw(a, lo, j);
        line++;

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

    private static void draw(String[] a, int iold, int ith, int jold, int jth) {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(-2.50, line, ith  + "");
        StdDraw.text(-1.25, line, jth  + "");
        for (int i = 0; i < a.length; i++) {
            if      (i >= iold && i <= ith) StdDraw.setPenColor(StdDraw.BLACK);
            else if (i <= jold && i >= jth) StdDraw.setPenColor(StdDraw.BLACK);
            else                            StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
            StdDraw.text(i, line, a[i]);
        }
    }

    private static void draw(String[] a, int ith, int jth) {
        StdDraw.setPenColor(StdDraw.BOOK_RED);
        if (ith != 0) StdDraw.text(-2.50, line, ith  + "");
        StdDraw.text(-1.25, line, jth  + "");
        for (int i = 0; i < a.length; i++) {
            if      (i == ith || i == jth) StdDraw.setPenColor(StdDraw.BOOK_RED);
            else                           StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
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
        StdDraw.text(-2.50, -2, "i");
        StdDraw.text(-1.25, -2, "j");
        StdDraw.setPenColor(StdDraw.BOOK_RED);
        StdDraw.line(-3, -1.65, N-.5, -1.65);
        StdDraw.setPenColor(StdDraw.BLACK);
        for (int i = 0; i < a.length; i++)
            StdDraw.text(i, -1, a[i]);
    }

    // display footer
    private static void footer(String[] a, int j) {
        StdDraw.setPenColor(StdDraw.BOOK_RED);
        StdDraw.text(-1.25, line, j + "");
        for (int i = 0; i < a.length; i++) {
            if  (i == j) StdDraw.setPenColor(StdDraw.BOOK_RED);
            else         StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(i, line, a[i]);
        }
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
        int j = partition(a);

        // display the footer
        footer(a, j);
    }

}