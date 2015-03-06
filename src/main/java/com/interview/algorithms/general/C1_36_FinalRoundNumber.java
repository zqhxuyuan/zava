package com.interview.algorithms.general;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/22/14
 * Time: 5:20 PM
 *
 * N integer from 0 - N-1 form a cycle, start to delete number by visit M step. The process is started at 0.
 *      Given N and M, please write code to calculate which number will last at the final round.
 *
 * Define a function f, f(N,M)+K means, N number start from K and delete Mth.
 * When there is only 1 number, f(0, M) = 0;
 *
 * The first round it will delete Mth number from 0. then the second round, it is N-1 number delete Mth start from M%N
 * So f(N,M) = (f(N-1, M)+M) % N
 */
public class C1_36_FinalRoundNumber {

    public static int find(int N, int M){
        if(N == 1) return 0;
        return (find(N-1, M) + M) % N;
    }
}
