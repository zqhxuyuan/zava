package com.interview.basics.sort;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 10/16/14
 * Time: 5:09 PM
 */
public class QuickSorterThreeWay<T extends Comparable<T>> extends QuickSorterSimplest<T> {

    @Override
    protected void sort(T[] input, int low, int high) {
        if (low >= high) return;
        findPivot(input, low, high, low);

        int i = low;
        int m = -1;
        for (int j = low + 1; j <= high; j++) {
            if (input[j].compareTo(input[low]) == 0) {
                if (m == -1) m = i;
                if (++m != j) swap(input, m, j);
            } else if (input[j].compareTo(input[low]) < 0 && ++i != j) {
                swap(input, i, j);
                if (m > -1 && ++m< j) swap(input, m, j);
            }
        }
        if(low != i) swap(input, low, i);
        sort(input, low, i - 1);
        sort(input, m > -1 ? m + 1 : i + 1, high);
    }
}
