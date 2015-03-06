package com.interview.basics.search.array.unsorted;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 10/17/14
 * Time: 5:54 PM
 */
public class QuickSelect3MedianSearcher<T extends Comparable<T>> extends QuickSelectSearcher<T> {

    public QuickSelect3MedianSearcher(T[] input) {
        super(input);
    }

    @Override
    protected void findPivot(T[] input, int low, int high) {
        int median = (low + high) / 2;
        if(input[low].compareTo(input[median]) > 0)     swap(input, low, median);
        if(input[high].compareTo(input[low]) < 0)       swap(input, low, high);
        if(input[high].compareTo(input[median]) < 0)    swap(input, high, median);

        swap(input, low, median);
    }
}
