package com.interview.books.ninechapter;

/**
 * Created_By: stefanie
 * Date: 14-12-12
 * Time: 上午11:41
 */
public class NC5_ThrowBalls {
    //times[i][k]: min times throw k ball to find out P in building with i floor.
    //initialize: times[i][1] = i - 1;  //throw from 1 to i - 1. (last floor not need to try, it will break)
    //function: times[i][k] = min(1 + max(times[j][k-1], times[i-j][k] (i != j)) for all j from 1 ~ i)
    //          k-th ball break at floor j
    //result: times[N][K]
    //Time: O(KN^2), Space: O(NK)
    public int minTimes(int N, int K){
        int[][] times = new int[N + 1][K + 1];
        //init
        for(int i = 1; i <= N; i++) times[i][1] = i - 1;
        //function
        for(int k = 2; k <= K; k++){
            for(int i = 1; i <= N; i++){
                times[i][k] = Integer.MAX_VALUE;
                for(int j = 1; j <= i; j++){
                    int tries = times[j][k - 1];
                    if(j < i) tries = Math.max(tries, times[i - j][K]);
                    times[i][k] = Math.min(times[i][k], tries + 1);
                }
            }
        }
        return times[N][K];
    }

    public static void main(String[] args){
        NC5_ThrowBalls experiment = new NC5_ThrowBalls();
        System.out.println(experiment.minTimes(100, 2));  //14
    }
}
