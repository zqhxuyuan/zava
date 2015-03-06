package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-8-21
 * Time: 上午9:34
 */
public class C4_47_OccurrenceFinder {
    public static int find(Integer[] array, Integer key){
        int begin = find(array, key, 0, array.length - 1, true);
        int end = find(array, key, 0, array.length - 1, false);
        return end - begin + 1;
    }

    public static int find(Integer[] array, Integer key, int low, int high, boolean isFirst){
        int mid = low + (high - low) / 2;
        if(array[mid] == key){
            if(isFirst){
                if(mid == low || array[mid] != array[mid - 1]){  //first occurrence
                    return mid;
                } else {
                    return find(array, key, low, mid -1, isFirst);
                }
            } else {
                if(mid == high || array[mid] != array[mid + 1]){  //last occurrence
                    return mid;
                } else {
                    return find(array, key, mid + 1, high, isFirst);
                }
            }
        } else if(mid > key) return find(array, key, low, mid - 1, isFirst);
        else return find(array, key, mid + 1, high, isFirst);
    }
}

