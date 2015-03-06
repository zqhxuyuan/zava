package com.interview.basics.sort;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 10/17/14
 * Time: 5:41 PM
 */
public class QuickSorterThreeMedian<T extends Comparable<T>> extends QuickSorterSimplest<T> {
    @Override
    protected void findPivot(T[] input, int low, int high, int key) {
        int median = (low + high) / 2;
        if(input[low].compareTo(input[median]) > 0)     swap(input, low, median);
        if(input[high].compareTo(input[low]) < 0)       swap(input, low, high);
        if(input[high].compareTo(input[median]) < 0)    swap(input, high, median);

        swap(input, key, median);
    }
}
