package com.interview.algorithms.array;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/17/14
 * Time: 2:30 PM
 */
public class C4_58_IncreasingSubArray {
    public static long find(int[] array){
        int count = 0;
        int[] counts = new int[array.length];
        counts[0] = 0;
        for(int i = 1; i < array.length; i++){
            for(int j = 0; j < i; j++){
                if(array[j] < array[i]) counts[i] += counts[j] + 1;
            }
            count += counts[i];
        }
        return count;
    }


}
