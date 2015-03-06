package com.interview.books.leetcode;

/**
 * Created_By: stefanie
 * Date: 14-12-11
 * Time: 下午3:13
 */
public class LCS9_DoubleSquareNumber {

    int doubleSquare(int m) {
        double p = Math.sqrt((double)m / 2.0);
        int total = 0;
        for (int i = 0; i <= p; i++) {
            double j = Math.sqrt((double)m - i*i);
            if (j - (int)j == 0.0)   // might have precision issue,
                total++;  // can be resolved using |j-(int)j| == delta
        }
        return total;
    }

    public static void main(String[] args){
        LCS9_DoubleSquareNumber number = new LCS9_DoubleSquareNumber();
        System.out.println(number.doubleSquare(7));
        System.out.println(number.doubleSquare(10));
        System.out.println(number.doubleSquare(25));
    }
}
