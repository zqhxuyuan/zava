package com.interview.books.svinterview;

/**
 * Created_By: stefanie
 * Date: 14-12-14
 * Time: 下午5:02
 */
public class SV24_MinMoveToTop {

    public int minMove(int n){
        int move = 0;
        while(n > 0){
            n = n & (n - 1);
            move++;
        }
        return move;
    }

    public static void main(String[] args){
        SV24_MinMoveToTop mover = new SV24_MinMoveToTop();
        System.out.println(Integer.toBinaryString(100));
        System.out.println(mover.minMove(100));
    }
}
