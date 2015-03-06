package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-11-6
 * Time: 下午11:09
 *
 * Given an array of integers, every element appears three times except for one. gFind that single one.
 * First time number appear -> save it in "ones"
 * Second time -> clear "ones" but save it in "twos" for later check
 * Third time -> try to save in "ones" but value saved in "twos" clear it.
 */
public class C4_41A_NonThreeTimeNumber {

    public static int singleNumber(int[] num) {
        int ones = 0, twos = 0;
        for(int i = 0; i < num.length; i++){
            ones = (ones ^ num[i]) & ~twos;
            twos = (twos ^ num[i]) & ~ones;
        }
        return ones;
    }

}
