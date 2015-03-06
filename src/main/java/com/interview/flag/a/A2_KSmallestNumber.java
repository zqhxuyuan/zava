package com.interview.flag.a;

import com.interview.utils.ArrayUtil;
import com.interview.utils.ConsoleWriter;

/**
 * Created_By: stefanie
 * Date: 14-12-4
 * Time: 下午5:42
 */
public class A2_KSmallestNumber {
    public static void kSmallest(int[] array, int K){
        int low = 0;
        int high = array.length - 1;
        while(low < high){
            int pivot = partition(array, low, high);
            if(pivot == K || pivot == K - 1) return;
            else if(pivot < K) low = pivot + 1;
            else high = pivot - 1;
        }
    }

    public static int partition(int[] array, int low, int high){
        int pivot = low;
        for(int j = low + 1; j <= high; j++){
            if(array[j] < array[low]) ArrayUtil.swap(array, ++pivot, j);
        }
        ArrayUtil.swap(array, pivot, low);
        return pivot;
    }

    public static void main(String[] args){
        int[] array = new int[]{9, 5, 1, 4, 13, 6};
        A2_KSmallestNumber.kSmallest(array, 4);
        //1, 4, 5
        ConsoleWriter.printIntArray(array);
    }
}
