package com.interview.books.ccinterview;

/**
 * Created_By: stefanie
 * Date: 14-12-1
 * Time: 下午4:29
 */
public class CC33_CountMInRangeN {
    public static int count(int N, int M) {
        int count = 0;
        int times = 1;
        int low_number = 0;
        int full_count = 0;
        while (N > 0) {
            int mod = N % 10;
            if (mod > M) {
                count += times + mod * full_count;
            } else if (mod == M) {
                count += low_number + 1 + M * full_count;
            } else if (mod == 1) {   //for the case M != 1
                count += mod * full_count;
            }
            N = N / 10;
            low_number += mod * times;
            full_count = 10 * full_count + times;
            times *= 10;
        }
        return count;
    }
}
