package com.interview.leetcode.dp;

/**
 * Created_By: stefanie
 * Date: 14-11-26
 * Time: 下午3:50
 *
/**
 * There are n coins in a line. (Assume n is even). Two players take turns to take a coin from one of the ends
 * of the line until there are no more coins left. The player with the larger amount of money wins.
 * 1. Would you rather go first or second? Does it matter?
 * 2. Assume that you go first, describe an algorithm to compute the maximum amount of money you can win.
 */
public class CoinsInALine {

    //money[i][j = the max money I can get in the coin sequence num[i] ~ num[j]
    //initialize: money[0][*] == 0 money[*][0] = 0;
    //function: money[i][j] = max of
    //              num[i] + min(money[i+2][j], money[i+1][j-1])
    //              num[j] + min(money[i+1][j-1], money[i][j-2])
    //      same as palindrome, need loop on len and start point
    //result: money[0][num.length-1]
    public int maxMoney(int[] num) {
        int N = num.length;
        int[][] money = new int[N][N];
        int a, b, c;
        for (int len = 1; len < N; len++) {
            for (int i = 0, j = len; i < N && j < N; i++, j++) {
                a = ((i+2 <= N-1) ? money[i+2][j] : 0);
                b = ((i+1 <= N-1 && j-1 >= 0) ? money[i+1][j-1] : 0);
                c = ((j-2 >= 0) ? money[i][j-2] : 0);
                money[i][j] = Math.max(num[i] + Math.min(a,b), num[j] + Math.min(b,c));
            }
        }
        return money[0][N-1];
    }
}
