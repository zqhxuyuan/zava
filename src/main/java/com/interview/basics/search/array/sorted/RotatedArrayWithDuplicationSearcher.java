package com.interview.basics.search.array.sorted;

import com.interview.basics.search.array.ArraySearcher;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 11/4/14
 * Time: 3:13 PM
 */
public class RotatedArrayWithDuplicationSearcher<T extends Comparable<T>> extends ArraySearcher<T>{
    protected RotatedArrayWithDuplicationSearcher(T[] input) {
        super(input);
    }

    public T min(){
        return min(0, input.length - 1);
    }

    private T min(int low, int high){
        if(low == high) return input[low];
        int mid = (low + high) / 2;
        if(mid > low && input[mid - 1].compareTo(input[mid]) > 0) return input[mid];
        if(input[mid].compareTo(input[high]) > 0) return min(mid + 1, high);
        else if(input[mid].compareTo(input[high]) < 0) return min(low, mid - 1);
        else {
            T leftMin = min(low, mid - 1);
            T rightMin = min(mid + 1, high);
            return leftMin.compareTo(rightMin) < 0? leftMin : rightMin;
        }
    }

    @Override
    public T find(T element) {
        return find(element, 0, input.length - 1);
    }

    private T find(T element, int low, int high){
        if(low > high) return null;
        int mid = (low + high) / 2;
        int cmp = element.compareTo(input[mid]);
        if(cmp == 0) return input[mid];
        else if(cmp < 0){
            while(low < mid && input[low].equals(input[mid])) low++;
            if((low == mid || input[low].compareTo(input[mid]) < 0) && element.compareTo(input[low]) < 0) return find(element, mid + 1, high);
            return find(element, low, mid - 1);
        } else {
            while(high > mid && input[high] == input[mid]) high--;
            if((high == mid || input[high].compareTo(input[mid]) > 0) && element.compareTo(input[high]) > 0) return find(element, low, mid - 1);
            return find(element, mid + 1, high);
        }
    }
}
