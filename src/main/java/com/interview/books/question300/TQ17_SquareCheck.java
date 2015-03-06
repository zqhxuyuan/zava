package com.interview.books.question300;

import com.interview.algorithms.general.C1_59_PrimeNumber;

/**
 * Created_By: stefanie
 * Date: 14-8-21
 * Time: 下午5:11
 */


public class TQ17_SquareCheck {
    static int[] PRIMES = C1_59_PrimeNumber.generate(100);

    public static boolean isSquare(int n){
        int low = 1;
        int high = n/2;
        while(low < high){
            int mid = low + (high - low)/2;
            int square = mid * mid;
            if(square == n) return true;
            else if(square < n) low = mid + 1;
            else high = mid - 1;
        }
        return false;
    }

    public static int isSquareWithPrim(int n){
        if(n == 1 || n == 0) return n;
        n = Math.abs(n);
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
