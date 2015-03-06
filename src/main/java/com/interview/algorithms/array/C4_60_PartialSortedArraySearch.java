package com.interview.algorithms.array;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/18/14
 * Time: 2:21 PM
 */
public class C4_60_PartialSortedArraySearch {
    public static int find(int[] array, int key){
        int split = findSplit(array);
        int index = -1;
        if(split != -1){
            index = find(array, key, 0, split - 1);
            if(index == -1) index = find(array, key, split, array.length - 1);
        }
        return index;
    }

    private static int findSplit(int[] array){
        boolean isAsc = array[0] <= array[1];
        for(int i = 0; i < array.length - 1; i++){
            if((isAsc && array[i] > array[i+1]) || (!isAsc && array[i] < array[i-1]))
                return i+1;
        }
        return -1;
    }

    private static int find(int[] array, int key, int low, int high){
        if(low <= high){
            int mid = (low + high) / 2;
            if(key == array[mid]) return mid;
            else if(key > array[mid]) return find(array, key, mid + 1, high);
            else return find(array, key, low, mid - 1);
        }
        return -1;
    }
}
