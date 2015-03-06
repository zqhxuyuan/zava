package com.interview.algorithms.array;

import com.interview.utils.ArrayUtil;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/18/14
 * Time: 3:14 PM
 */
public class C4_61_ShuffleWithoutRandom {
    static int seed = 6;
    public static void shuffle(int[] array){
        seed++;
        for(int i = 0; i < array.length - 1; i++){
            for(int j = array.length - 1; j > i; j--){
                int rj = array[j] % seed;
                int ri = array[j - 1] % seed;
                if(rj > ri){
                    ArrayUtil.swap(array, j - 1, j);
                }
            }
        }
    }

}
