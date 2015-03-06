package com.interview.algorithms.general;

import java.util.Arrays;

/**
 * Created_By: stefanie
 * Date: 14-11-5
 * Time: 下午9:39
 * Implement next permutation, which rearranges numbers into the lexicographically next greater permutation of numbers.
 * If such arrangement is not possible, it must rearrange it as the lowest possible order (ie, sorted in ascending order).
 * The replacement must be in-place, do not allocate extra memory.
 *
 * Here are some examples. Inputs are in the left-hand column and its corresponding outputs are in the right-hand column.
 * 1,2,3 → 1,3,2
 * 3,2,1 → 1,2,3
 * 1,1,5 → 1,5,1
 */
public class C1_73B_NextPermutation {
    public static void nextPermutation(int[] num) {
        for(int i = num.length - 2; i >= 0; i--){
            int min = -1;
            for(int j = i + 1; j < num.length; j++){
                if(num[j] > num[i]) {
                    if (min == -1 || num[j] < num[min]) min = j;
                }
            }
            if(min != -1){
                swap(num, i, min);
                Arrays.sort(num, i + 1, num.length);
                return;
            }
        }
        Arrays.sort(num, 0, num.length);
    }

    private static void swap(int[] num, int i, int j){
        int tmp = num[i];
        num[i] = num[j];
        num[j] = tmp;
    }
}
