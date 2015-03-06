package com.interview.books.question300;

import com.interview.utils.ConsoleWriter;

/**
 * Created_By: stefanie
 * Date: 14-9-17
 * Time: 下午10:28
 */
public class TQ21_PrimeNumberGenerator {

    public static int[] generate(int N) {
        int[] primes = new int[N];
        int idx = 0;
        int num = 2;
        while(idx < N){
            int j = 0;
            for(; j < idx; j++){
                if(num % primes[j] == 0) break;
            }
            if(j == idx) primes[idx++] = num;
            num++;
        }
        return primes;
    }

    public static void main(String[] args){
        int[] primes = TQ21_PrimeNumberGenerator.generate(16);
        ConsoleWriter.printIntArray(primes);
    }
}
