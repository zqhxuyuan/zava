package com.interview.books.leetcode;

import java.util.Random;

/**
 * Created_By: stefanie
 * Date: 14-12-11
 * Time: 下午7:43
 */
public class LCS13_RandomGenerator {
    static Random RANDOM = new Random();

    public int randomM(int M){
        return RANDOM.nextInt(M) + 1;
    }

    public int randomN(int M, int N){
        if(M == N){
            return randomM(M);
        } else if(M > N) {
            int rand = Integer.MAX_VALUE;
            while(rand > N) rand = randomM(M);
            return rand;
        } else {
            int prod = Integer.MAX_VALUE;
            int threshold = ((M * M) / N) * N;
            while(prod > threshold){
                prod = (randomM(M) - 1) * M + randomM(M);
            }
            return prod % N + 1;
        }
    }

    public static void test(LCS13_RandomGenerator generator, int M, int N){
        int[] marker1 = new int[M + 1];
        int[] marker2 = new int[N + 1];

        for(int i = 0; i < 1000000; i++){
            int rand = generator.randomM(M);
            marker1[rand]++;
            rand = generator.randomN(M, N);
            marker2[rand]++;
        }
        System.out.println("----------- Test Result ------------");
        System.out.println("Random" + M + ": ");
        for(int i = 1; i <= M; i++){
            System.out.println(marker1[i]);
        }
        System.out.println("Random" + N + ": ");
        for(int i = 1; i <= N; i++){
            System.out.println(marker2[i]);
        }
    }

    public static void main(String[] args) {
        LCS13_RandomGenerator random = new LCS13_RandomGenerator();
        random.test(random, 7, 10);
        random.test(random, 10, 7);
    }
}
