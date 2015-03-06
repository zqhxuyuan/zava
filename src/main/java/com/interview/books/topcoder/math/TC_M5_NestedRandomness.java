package com.interview.books.topcoder.math;

import java.util.Arrays;

/**
 * Created_By: stefanie
 * Date: 15-1-18
 * Time: 下午4:46
 *
 * Consider a function randomInt(integer N) that takes an integer N and returns an integer uniformly at random
 * in the range 0 to N-1. If that function is nested, as in randomInt(randomInt(N)), the probability distribution
 * changes, and some numbers are more likely than others. Given an int nestings defining the number of times the
 * function is nested (1 indicates randomInt(N), 2 indicates randomInt(randomInt(N)), and so on), an int N and
 * an int target, return the probability that the result of nestings nested calls to randomInt with the parameter
 * N will result in target.
 */
public class TC_M5_NestedRandomness {
    public double probability(int N, int nestings, int target){
        if(target < 0 || target > N) return 0;
        double[] probabilities = new double[N];
        double[] current = new double[N];

        for(int i = 0; i < N; i++) probabilities[i] = 1.0/N;

        for(int k = 2; k <= nestings; k++){
            Arrays.fill(current, 0);
            for(int i = 0; i < N; i++){
                for (int j = 0; j < i; j++) current[j] += probabilities[i] * (1.0 / i);
            }
            for (int i = 0; i < N; i++)  probabilities[i] = current[i];
        }
        return probabilities[target];
    }

    public static void main(String[] args){
        TC_M5_NestedRandomness randomness = new TC_M5_NestedRandomness();
        System.out.println(randomness.probability(5, 2, 1)); //0.21666666666666667
        System.out.println(randomness.probability(10, 4, 0)); //0.19942680776014104
    }
}
