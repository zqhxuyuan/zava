package com.interview.algorithms.bit;

/**
 * Created_By: stefanie
 * Date: 14-10-10
 * Time: 下午10:01
 */
public class C16_1_BitCopy {
    public static int copy(int N, int M, int i, int j){
        N = clean(N, i, j);
        return set(N, M, i, j);
    }

    private static int clean(int N, int i, int j){
        int mask = -1 ^ (((1 << (j - i + 1)) - 1) << i);
        return N & mask;
    }

    private static int set(int N, int M, int i, int j){
        int mask = M << i;
        return N | mask;
    }
}
