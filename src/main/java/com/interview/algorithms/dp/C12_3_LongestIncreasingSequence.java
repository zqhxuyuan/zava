package com.interview.algorithms.dp;

/**
 * Created_By: zouzhile
 * Date: 3/14/14
 * Time: 5:39 PM
 *
 * Given a sequence of N numbers - A[1] , A[2] , ..., A[N] .
 * Find the length of the longest non-decreasing sequence.
 * and print out the sequence
 *
 * optimal[S] = max{ optimal[i] + 1 if A[i] < A[S] } for all 1 - S-1
 */
public class C12_3_LongestIncreasingSequence {

    public int[] getLengthOfLongestIncreasingSequence(int[] values) {
        int max = 0;
        int[] optimal = new int[values.length];
        int[] seq = new int[values.length];

        for(int i = 0; i < optimal.length; i ++) {
            optimal[i] = 1;
            seq[i] = i;
        }

        for(int i = 1; i < optimal.length; i ++) {
            for(int j = 0; j < i; j ++ ) {
                if(values[j] < values[i])
                    if(optimal[j] + 1 > optimal[i]) {
                        optimal[i] = optimal[j] + 1;
                        seq[i] = j;
                        if(optimal[i] > optimal[max]) max = i;
                    }
            }
        }

        //backtrace the result
        int[] result = new int[optimal[max]];
        int current = max;
        for(int i = result.length - 1; i >= 0; i-- ){
            result[i] = values[current];
            current = seq[current];
        }
        return result;
    }
}
