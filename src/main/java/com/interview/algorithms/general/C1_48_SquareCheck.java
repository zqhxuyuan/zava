package com.interview.algorithms.general;

import com.interview.utils.ConsoleWriter;

/**
 * Created_By: stefanie
 * Date: 14-8-21
 * Time: 下午5:11
 */


public class C1_48_SquareCheck {
    static int[] PRIMES = C1_59_PrimeNumber.generate(100);

    public static int check(int n){
        if(n == 1 || n == 0) return n;
        n = Math.abs(n);
        //ConsoleWriter.printIntArray(PRIMES);
        int sqrt = 1;
        int i = 0;
        while(n > 1 && n >= PRIMES[i] * PRIMES[i]){
            if(n % PRIMES[i] == 0){
                if(n % (PRIMES[i] * PRIMES[i]) == 0) {
                    sqrt *= PRIMES[i];
                    n = n / PRIMES[i] / PRIMES[i];
                }
                else return 0;
            } else {
                i++;
            }
        }
        if(n == 1) return sqrt;
        else return 0;
    }
}
