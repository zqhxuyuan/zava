package com.interview.books.ccinterview;

/**
 * Created_By: stefanie
 * Date: 14-12-13
 * Time: 下午8:07
 */
public class CC21_CentCombineWays {
    //ways[n]: the ways to combine n cents by 25, 10, 5, 1 cents.
    //initialize: ways[0] = 1;
    //function: ways[i] = ways[i - 1] + ways[i - 5] + ways[i - 10] + ways[i - 25] in range
    //result: ways[n]
    public int combineWays(int n){
        if(n <= 0) return 0;

        int[] ways = new int[n + 1];
        ways[0] = 1;
        for(int i = 1; i <= n; i++){
            ways[i] = ways[i-1];
            if(i % 5 == 0)  ways[i] += ways[i - 5];
            if(i % 10 == 0) ways[i] += ways[i - 10];
            if(i % 25 == 0) ways[i] += ways[i - 25];
        }
        return ways[n];
    }

    public static void main(String[] args){
        CC21_CentCombineWays combiner = new CC21_CentCombineWays();
        System.out.println(combiner.combineWays(10));
        System.out.println(combiner.combineWays(25));
        System.out.println(combiner.combineWays(100));
    }
}
