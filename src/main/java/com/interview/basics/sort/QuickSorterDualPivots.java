package com.interview.basics.sort;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 10/17/14
 * Time: 10:43 AM
 */
public class QuickSorterDualPivots<T extends Comparable<T>> extends QuickSorterSimplest<T> {

    @Override
    protected void sort(T[] input, int low, int high){
        if (low >= high) return;
        findPivot(input, low, high, low);
        findPivot(input, low, high, high);
        if(input[low].compareTo(input[high]) > 0) swap(input, low, high);

        int i = low;
        int m = -1;
        for (int j = low + 1; j < high; j++) {
            if (input[j].compareTo(input[low]) < 0 && ++i != j) {
                swap(input, i, j);
                if (m > -1 && ++m < j) swap(input, m, j);
            } else if (input[j].compareTo(input[low]) >= 0 && input[j].compareTo(input[high]) <= 0) {
                if (m == -1) m = i;
                if (++m != j) swap(input, m, j);
            }
        }
        if(low != i) swap(input, low, i);
        sort(input, low, i - 1);
        if(m != -1) {
            if(++m != high) swap(input, high, m);
            sort(input, i + 1, m - 1);
            sort(input, m + 1, high);
        } else {
            sort(input, i + 1, high);
        }

    }

}
