package com.interview.basics.search.array.sorted;

import com.interview.basics.search.array.ArraySearcher;

/**
 * Created_By: stefanie
 * Date: 14-11-2
 * Time: 下午12:00
 */
public class RotatedArraySearcher<T extends Comparable<T>> extends ArraySearcher<T>{

    protected RotatedArraySearcher(T[] input) {
        super(input);
    }

    public T min(){
        return min(0, input.length - 1);
    }

    private T min(int low, int high){
        if(low == high) return input[low];
        int mid = (low + high) / 2;
        if(mid > low && input[mid - 1].compareTo(input[mid]) > 0) return input[mid];
        if(input[mid].compareTo(input[high]) >= 0) return min(mid + 1, high);
        else return min(low, mid - 1);
    }

    @Override
    public T find(T element) {
        return find(element, 0, input.length - 1);
    }

    private T find(T element, int low, int high){
        if(low > high) return null;
        int mid = (low + high)/2;
        int cmp = element.compareTo(input[mid]);
        if(cmp == 0) return input[mid];
        else if(cmp < 0){
            if(input[low].compareTo(input[mid]) <= 0 && element.compareTo(input[low]) < 0)
                return find(element, mid + 1, high);
            return find(element, low, mid - 1);
        } else {
            if(input[high].compareTo(input[mid]) >= 0 && element.compareTo(input[high]) > 0)
                return find(element, low, mid - 1);
            return find(element, mid + 1, high);
        }
    }
}
