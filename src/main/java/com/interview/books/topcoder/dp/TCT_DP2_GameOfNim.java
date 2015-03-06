package com.interview.books.topcoder.dp;

/**
 * Created_By: stefanie
 * Date: 15-1-17
 * Time: 下午6:17
 */
public class TCT_DP2_GameOfNim {
    public boolean canWin(int N){
        boolean[] win = new boolean[N + 1];
        win[1] = true;
        win[3] = true;
        win[4] = true;
        for(int i = 5; i <= N; i++){
            win[i] = !win[i-1] || !win[i-3] || !win[i-4];
        }
        return win[N];
    }

    public static void main(String[] args){
        TCT_DP2_GameOfNim game = new TCT_DP2_GameOfNim();
        for(int i = 5; i <= 15; i++) System.out.println(i + " win: " + game.canWin(i));
    }
}
