package edu.princeton.cs.excercise.ch2;

/*************************************************************************
 *  Compilation:  javac TraceShell.java
 *  Execution:    java  TraceShell input
 *
 *  Inserstion sort the sequence of strings specified on the command-line
 *  and show the detailed trace.
 *
 *  % java TraceShell SORTEXAMPLE
 *
 *************************************************************************/

import edu.princeton.cs.introcs.StdDraw;

import java.awt.Font;

public class TraceShell {
    private static int line = 0;

    public static void sort(String[] a) {
        int N = a.length;

        // 3x+1 increment sequence:  1, 4, 13, 40, 121, 364, 1093, ...
        int h = 1;
        while (h < N/3) h = 3*h + 1;

        while (h >= 1) {
            // h-sort the array
            for (int i = h; i < N; i++) {
                int j;
                for (j = i; j >= h && less(a[j], a[j-h]); j -= h) {
                    exch(a, j, j-h);
                }
                draw(a, h, i, j);
                line++;
            }
            h /= 3;
            footer(a);
            line++;
        }
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

    //
    private static void draw(String[] a, int h, int ith, int jth) {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(-3.75, line, h + "");
        StdDraw.text(-2.50, line, ith + "");
        StdDraw.text(-1.25, line, jth + "");
        for (int i = 0; i < a.length; i++) {
            if (i == jth)                StdDraw.setPenColor(StdDraw.BOOK_RED);
            else if (i > ith)            StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
            else if (i < jth)            StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
            else if ((i % h) == (jth % h)) StdDraw.setPenColor(StdDraw.BLACK);
            else                         StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
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
        StdDraw.text(-3.75, -2, "h");
        StdDraw.text(-2.50, -2, "i");
        StdDraw.text(-1.25, -2, "j");
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

        // number of rows needed
        int rows = 0;
        int h = 1;
        while (h < N/3) {
            rows += (N - h + 1);
            h = 3*h + 1;
        }
        rows += (N - h + 1);

        // set canvas size
        StdDraw.setCanvasSize(30*(N+3), 30*(rows+3));
        StdDraw.setXscale(-4, N + 1);
        // StdDraw.setYscale(N+1, -4);
        StdDraw.setYscale(rows, -4);
        StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 13));

        // draw the header
        header(a);

        // sort the array
        sort(a);
    }

}