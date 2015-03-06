package com.interview.leetcode.application;

import com.interview.utils.ArrayUtil;

/**
 * Created_By: stefanie
 * Date: 14-11-13
 * Time: 下午10:20
 *
 * Given an unsorted integer array, find the first missing positive integer.
 *  For example,
 *      Given [1,2,0] return 3,
 *      and [3,4,-1,1] return 2.
 * Your algorithm should run in O(n) time and uses constant space.
 *
 * Tricks:
 *  1. find a range of solution in a problem: [1 ~ num.length + 1]
 *      so can use current array as buffer, if number not in the range, just skip it.
 *  2. use the existing array and do swap to put number in the right place,
 *     then scan the array to check the first number not in the right place.
 */
public class FirstMissingPositive {

    public static int find(int[] num) {
        if (num.length == 0) return 1;
        for (int i = 0; i < num.length; ) {
            int rightPlace = num[i] - 1;  //the right place to put num[i]
            //if meet the all following condition, do the swap
            //1. the right place is in range of array,  >= 0 && < num.length
            //2. the current place is not the right place
            //3. the number in right place is not the right number
            if (rightPlace >= 0 && rightPlace < num.length && rightPlace != i && num[i] != num[rightPlace]) {
                ArrayUtil.swap(num, i, rightPlace);
            } else i++;
        }
        for (int i = 0; i < num.length; i++) {
            if (num[i] != i + 1) return i + 1;
        }
        return num.length + 1;
    }
}
