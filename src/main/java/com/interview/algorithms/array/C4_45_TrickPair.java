package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-8-19
 * Time: 下午3:45
 *
 * Given an array with integer, a "trick pair" is called for any 2 numbers is not follow the arrange rule: larger number should be put
 * at the right side of the small number.
 * Given an array list, write code to find how many trick pair exist.
 *
 * Solution:
 * 1. find() gives a O(N^2) solution by check all the previous element created pair is a trick pair or not.
 * 2. findByMerge() O(NlogN): using MergeSort method to count all the switched element
 */
public class C4_45_TrickPair {

    public static int find(Integer[] array) {
        int count = 0;
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = i + 1; j < array.length; j++) {
                if (array[i] > array[j]) {
                    //System.out.println(array[i] + "\t" + array[j]);
                    count++;
                }
            }
        }
        return count;
    }

    public static int findByMerge(Integer[] array){
        Integer[] aux = new Integer[array.length];
        return mergesort(array, aux, 0, array.length - 1);
    }

    private static int mergesort(Integer[] array, Integer[] aux, int low, int high){
        int count = 0;
        if(low < high){
            int mid = (low + high) / 2;
            count += mergesort(array, aux, low, mid);
            count += mergesort(array, aux, mid + 1, high);
            count += merge(array, aux, low, mid, high);
        }
        return count;
    }

    private static int merge(Integer[] array, Integer[] aux, int low, int mid, int high){
        int count = 0;
        for(int i = low; i <= high; i++) aux[i] = array[i];
        int i = low, j = mid + 1;
        for(int k = low; k <= high; k++){
            if(i > mid)     array[k] = aux[j++];
            else if(j > high)    array[k] = aux[i++];
            else if(aux[i] <= aux[j]) array[k] = aux[i++];
            else {
                array[k] = aux[j++];
                count += (mid - i + 1); //find trick pairs
            }
        }
        return count;
    }
}
