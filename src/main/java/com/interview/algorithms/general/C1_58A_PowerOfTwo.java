package com.interview.algorithms.general;

/**
 * Created_By: stefanie
 * Date: 14-9-22
 * Time: 下午4:40
 */
public class C1_58A_PowerOfTwo {

    public static boolean check(int n){
        if(n > 0 && (n & (n-1)) == 0) return true;
        else return false;
    }
}
