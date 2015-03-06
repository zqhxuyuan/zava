package com.interview.books.topcoder.dp;

/**
 * Created_By: stefanie
 * Date: 15-1-17
 * Time: 下午10:42
 */
public class TCT_DP2_GameOfNimII {

    public boolean canWin(int[] piles){
        int result = 0;
        for(int i = 0; i < piles.length; i++) result = result ^ piles[i];
        return result == 0;
    }

}
