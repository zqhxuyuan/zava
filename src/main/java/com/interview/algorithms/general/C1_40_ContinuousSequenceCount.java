package com.interview.algorithms.general;

/**
 * Created_By: stefanie
 * Date: 14-7-29
 * Time: 下午9:03
 */
public class C1_40_ContinuousSequenceCount {

    public static int count(int N) {
        int count = 0;
        int sum = 1;
        int start = 1;
        for (int i = start + 1; i <= (N+1)/2; i++) {
            sum += i;
            while (sum >= N) {
                if (sum == N) count++;
                sum -= start;
                start++;
            }
        }
        return count;
    }
}
