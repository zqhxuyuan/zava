package com.interview.algorithms.dp;

/**
 * Created_By: stefanie
 * Date: 14-9-29
 * Time: 上午10:24
 */
public class C12_29_SpaceFilling {

    public static int blockNumber(int m, int n){
        if(isOdd(m) && isOdd(n)) return -1;
        else return (m * n)/2;
    }

    private static boolean isOdd(int a){
        if((a & 1) == 0) return false;
        else return true;
    }

    public static int fillingNumber(int m, int n){
        return 0;
    }
}
