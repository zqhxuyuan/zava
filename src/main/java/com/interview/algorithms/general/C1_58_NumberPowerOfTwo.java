package com.interview.algorithms.general;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/17/14
 * Time: 1:28 PM
 */
public class C1_58_NumberPowerOfTwo {
    public static int find(int N){
        int i = 1;
        while( (i << 1) <= N){
            i = i << 1;
        }
        return i;
    }
}
