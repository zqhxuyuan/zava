package com.interview.algorithms.general;

/**
 * Created_By: stefanie
 * Date: 14-10-27
 * Time: 下午9:01
 */
public class C1_37A_CountMFrom0ToN {

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
            } else if (mod == 1) {
                count += mod * full_count;
            }
            N = N / 10;
            low_number = mod * times + low_number;
            full_count = 10 * full_count + times;
            times = times * 10;
        }
        return count;
    }
    public static int answer(int N, int M){
        int count = 0;
        for(int i = 0; i <= N; i++){
            int j = i;
            while(j > 0){
                if(j % 10 == M) count++;
                j = j / 10;
            }
        }
        return count;
    }

}
