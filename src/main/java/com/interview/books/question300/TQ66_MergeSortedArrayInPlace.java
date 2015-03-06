package com.interview.books.question300;

import com.interview.utils.ArrayUtil;
import com.interview.utils.ConsoleWriter;

/**
 * Created_By: stefanie
 * Date: 15-1-25
 * Time: 下午9:08
 */
public class TQ66_MergeSortedArrayInPlace {

    public void merge(int[] array, int a, int b){
        while(a < b && b < array.length){
            int i = a;
            int j = b;
            while(i < b && array[i] <= array[b]) i++;
            while(j < array.length && array[j] < array[i]) j++;
            ArrayUtil.reverse(array, i, j - 1);
            int mid = i + (j - b);
            ArrayUtil.reverse(array, i, mid - 1);
            ArrayUtil.reverse(array, mid, j - 1);
            a = mid;
            b = j;
        }
    }

    public static void main(String[] args){
        TQ66_MergeSortedArrayInPlace merger = new TQ66_MergeSortedArrayInPlace();
        int[] array = new int[]{1,4,10,5,7,8};
        merger.merge(array, 0, 3);
        ConsoleWriter.printIntArray(array);
    }
}
