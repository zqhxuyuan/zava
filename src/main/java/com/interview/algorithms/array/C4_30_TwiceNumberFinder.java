package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-7-21
 * Time: 下午11:35
 */
public class C4_30_TwiceNumberFinder {

    public static int find(int[] numbers, int N){
        int sum = 0;
        for(int i = 0; i < numbers.length; i++)     sum += numbers[i];
        return  sum - N*(N+1)/2;
    }

    public static int findByXOR(int[] numbers, int N){
        int k = numbers[0];
        for (int i=1; i < numbers.length; i++) {
            k ^= numbers[i] ^ i;
            System.out.println(k);
        }
        return k;
    }
}
