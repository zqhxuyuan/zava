package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-10-19
 * Time: 下午3:29
 */
public class C4_67_ElementSearchInVArray {

    public static int find(int[] array, int key){
        return find(array, key, 0, array.length - 1);
    }

    private static int find(int[] array, int key, int low, int high) {
        if(low > high) return -1;
        int mid = (low + high) / 2;
        if(array[mid] == key) return mid;
        int index = -1;
        if(array[mid] < key) {
            if(array[low] >= key) index = find(array, key, low, mid - 1);
            if(index == -1 && array[high] >= key) index = find(array, key, mid + 1, high);
            return index;
        } else {
            if(array[low] <= key) index = find(array, key, low, mid - 1);
            if(index == -1 && array[high] <= key) index = find(array, key, mid + 1, high);
            return index;
        }
    }
}
