package com.interview.basics.search.array.sorted;

import com.interview.basics.search.array.ArraySearcher;

/**
 * Created_By: stefanie
 * Date: 14-11-2
 * Time: 下午12:18
 */
public class VArraySearcher<T extends Comparable<T>> extends ArraySearcher<T> {

    protected VArraySearcher(T[] input) {
        super(input);
    }

    public T min(){
        return min(0, input.length - 1);
    }

    private T min(int low, int high){
        if(low == high) return input[low];
        int mid = (low + high)/2;
        int cmp1 = mid > low? input[mid].compareTo(input[mid - 1]) : -1;
        int cmp2 = mid < high? input[mid].compareTo(input[mid + 1]) : -1;
        if(cmp1 < 0 && cmp2 < 0) return input[mid];
        else if(cmp1 > 0){
            return min(low, mid - 1);
        } else if(cmp2 > 0){
            return min(mid + 1, high);
        } else {
//            T left = min(low, mid - 1);
//            T right = min(mid + 1, high);
//            return left.compareTo(right) < 0? left : right;
            return min(low + 1, high);
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
            int cmp1 = mid > low? input[mid].compareTo(input[mid - 1]) : -1;
            int cmp2 = mid < high? input[mid].compareTo(input[mid + 1]) : -1;
            if(cmp1 < 0 && cmp2 < 0) return null;
            else if(cmp1 > 0) return find(element, low, mid - 1);
            else if(cmp2 > 0) return find(element, mid + 1, high);
            else return find(element, low + 1, high);
        } else {
            T found = find(element, low, mid - 1);
            if(found == null) found = find(element, mid + 1, high);
            return found;
        }
    }
}
