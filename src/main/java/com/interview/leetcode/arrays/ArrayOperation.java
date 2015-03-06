package com.interview.leetcode.arrays;

import com.interview.utils.ArrayUtil;

/**
 * Created_By: stefanie
 * Date: 14-11-17
 * Time: 下午7:52
 */
public class ArrayOperation {

    public static void rotateKStep(int[] array, int k){
        if(array.length <= 1) return;
        k = k % array.length;

        reverse(array, 0, k - 1);
        reverse(array, k, array.length - 1);
        reverse(array, 0, array.length - 1);
    }

    public static void reverse(int[] array, int begin, int end){
        for(int i = 0; i < (end - begin + 1)/ 2; i++){
            int temp = array[begin + i];
            array[begin + i] = array[end - i];
            array[end - i] = temp;
        }
    }

    public static void reverseByLength(int[] array, int begin, int length){
        int end = begin + length - 1;
        for(int i = 0; i < length / 2; i++){
            int temp = array[begin + i];
            array[begin + i] = array[end - i];
            array[end - i] = temp;
        }
    }

    public static int partitionArray(int[] nums, int k) {
        //write your code here
        int i = -1;
        for(int j = 0; j < nums.length; j++){
            if(nums[j] < k) ArrayUtil.swap(nums, ++i, j);
        }
        return i + 1;
    }


    // use k scan array, and keep 2 more pointers i and j
    // element in [0,i] < key
    // element in (i,j] == key
    // element in (j,k] > key
    public static void partition3Way(int[] A, int key){
        int i = -1;
        int j = -1;
        for(int k = 0; k < A.length; k++){
            if(A[k] > key) continue;
            else if(A[k] == key) ArrayUtil.swap(A, k, ++j);
            else {
                if(j == i) j = i + 1;  //no elements equals to key
                ArrayUtil.swap(A, k, ++i);
                if(A[k] == key) ArrayUtil.swap(A, k, ++j);  //swap back if prev swap put equal element to k
            }
        }
    }
}
