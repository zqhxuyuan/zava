package com.interview.leetcode.application;

import java.util.Arrays;

/**
 * Created_By: stefanie
 * Date: 14-11-13
 * Time: 下午3:50
 *
 * There are N children standing in a line. Each child is assigned a rating value.
 * You are giving candies to these children subjected to the following requirements:
 *  1. Each child must have at least one candy.
 *  2. Children with a higher rating get more candies than their neighbors.
 * What is the minimum candies you must give?
 *
 * Tricks:
 *  1. 2-way scan the array: forward and backward
 */
public class Candy {
    /**
     * Solution is based on forward scan and backward scan
     * 1. init the candy
     * 2. forward scan: if ith child rating higher than i-1th, give one more than i-1th
     * 3. backward scan: if i-1th child rating higher than ith, and candy is lesser than ith, give one more than ith.
     */
    public static int candy(int[] children) {
        if (children == null || children.length == 0) return 0;

        int[] count = new int[children.length];
        Arrays.fill(count, 1);

        for (int i = 1; i < children.length; i++) {
            if (children[i] > children[i - 1]) {
                count[i] = count[i - 1] + 1;
            }
        }

        int sum = 0;
        for (int i = children.length - 1; i >= 1; i--) {   //check twice to make sure no breaking of rules
            sum += count[i];
            if (children[i - 1] > children[i] && count[i - 1] <= count[i]) {  // prev child rating is higher but candy is lesser
                count[i - 1] = count[i] + 1;
            }
        }
        sum += count[0];
        return sum;
    }
}
