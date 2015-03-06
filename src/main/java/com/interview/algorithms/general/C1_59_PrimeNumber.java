package com.interview.algorithms.general;

/**
 * Created_By: stefanie
 * Date: 14-9-17
 * Time: 下午10:28
 */
public class C1_59_PrimeNumber {
    public static int[] generate(int N) {
        int[] primes = new int[N];
        int k = 0;
        int i = 2;
        while(k < N){
            int j = 0;
            for(; j < k; j++){
                if(i % primes[j] == 0) break;
            }
            if(j == k) primes[k++] = i;
            i++;
        }
        //ConsoleWriter.printIntArray(primes);
        return primes;
    }
}
