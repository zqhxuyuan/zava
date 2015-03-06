package com.interview.basics.search.array.sorted;

import com.interview.basics.search.array.ArraySearcher;

import java.util.Arrays;

/**
 * Created_By: stefanie
 * Date: 14-11-2
 * Time: 下午3:13
 */
public class BinaryArraySearcher<T extends Comparable<T>> extends ArraySearcher<T>{

    protected BinaryArraySearcher(T[] input) {
        super(input);
        Arrays.sort(input);
    }

    @Override
    public T find(T element) {
        return find(element, 0, input.length - 1);
    }

    private T find(T element, int low, int high){
        if(low > high) return null;
        int mid = (high + low) / 2;
        int cmp = element.compareTo(input[mid]);
        if(cmp == 0) return input[mid];
        else if(cmp < 0) return find(element, low, mid - 1);
        else return find(element, mid + 1, high);
    }
}
