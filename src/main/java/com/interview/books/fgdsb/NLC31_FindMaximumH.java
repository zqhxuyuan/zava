package com.interview.books.fgdsb;

import com.interview.utils.ArrayUtil;

/**
 * Created_By: stefanie
 * Date: 15-2-5
 * Time: 下午2:00
 */
public class NLC31_FindMaximumH {
    public int findMax(int[] array){
        int low = 0;
        int high = array.length - 1;
        while(low < high){
            int partition = parition(array, low, high);
            if(array[partition] >= array.length - partition){
                high = partition;
            } else {
                low = partition + 1;
            }
        }
        return (array[high] <= array.length - high)? array[high] : array.length - high;
        //if array[high] > array.length - high, high is the first element break the rule, so return array.length - high
    }

    private int parition(int[] array, int low, int high){
        int pivot = low;
        for(int j = low + 1; j <= high; j++){
            if(array[j] < array[low]) ArrayUtil.swap(array, ++pivot, j);
        }
        ArrayUtil.swap(array, pivot, low);
        return pivot;
    }

    public static void main(String[] args){
        NLC31_FindMaximumH finder = new NLC31_FindMaximumH();
        System.out.println(finder.findMax(new int[]{3,2,5})); //2       //2 3 5   3 - 1 = 2
        System.out.println(finder.findMax(new int[]{4,2,3,5})); //3     //2 3 4 5
        System.out.println(finder.findMax(new int[]{5,2,4,6})); //3     //2 3 4 5
        System.out.println(finder.findMax(new int[]{8,6,7,5})); //4
        System.out.println(finder.findMax(new int[]{9,7,6,8})); //4
        System.out.println(finder.findMax(new int[]{10,7,9,8})); //4
    }
}
