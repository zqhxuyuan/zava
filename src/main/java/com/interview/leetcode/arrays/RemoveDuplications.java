package com.interview.leetcode.arrays;

/**
 * Created_By: stefanie
 * Date: 14-11-15
 * Time: 上午10:10
 *
 * Given a array, remove the duplication on it.
 * 1. if the array is sorted, remove the duplicated element, make each element at most appear K times.
 * 2. if the array is sorted, remove the element which have duplications
 *
 * Tricks:
 *  1. tracking occurence(int) of a given element.
 */
public class RemoveDuplications {

    /**
     * check the occurences of an element, if occurences >= K, discard it. otherwise occurence++ and copy to num[length++]
     */
    public static int removeMoreThanKTime(int[] num, int K) {
        if(num.length <= 0) return 0;
        int occurences = 1; // each number it self occure once even no duplicate
        int length = 1;
        for(int j = 1; j < num.length; j++){
            if(num[j] == num[j - 1] && occurences >= K) continue;
            // current element appeared less than twice

            // update occurrences
            if(num[j] == num[j - 1]) occurences += 1;
            else occurences = 1;

            // copy data
            num[length++] = num[j];
        }
        return length;
    }

    /**
     * also tracking the occurence, only copy element when it occurence is one.
     */
    public static int deduplicate(int[] num) {
        if(num == null) return 0;
        if(num.length < 2) return num.length;
        int occurences = 1;
        int length = 0;
        for(int i = 0; i < num.length; i ++) {
            while(i + 1 < num.length && num[i] == num[i+1]) {
                i ++;
                occurences ++;
            }
            if(occurences == 1) num[length ++ ] = num[i];
            else occurences = 1; // i + 1 is not equal to i
        }
        return length;
    }
}
