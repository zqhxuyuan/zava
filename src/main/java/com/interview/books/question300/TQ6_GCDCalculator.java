package com.interview.books.question300;

/**
 * Created_By: stefanie
 * Date: 14-12-15
 * Time: 上午11:33
 */
public class TQ6_GCDCalculator {

    public static int gcd(int i, int j){
        if(j > i) return gcd(j, i);
        while(i % j != 0){
            int mod = i % j;
            i = j;
            j = mod;
        }
        return j;
    }

    /**
     * Avoid to use division and mode, instead using >> or & bit operation.
     * if x, y both are even        f(x,y) = 2 * f(x/2, y/2)
     * if x is even, y is not,      f(x,y) = f(x/2, y)
     * if y is even, x is not,      f(x,y) = f(x, y/2)
     * if x, y both are not even,   f(x,y) = f(x, y - x)
     */
    public static int gcdWithDivisionMod(int x, int y){
        if(x < y)   return gcd(y, x);
        if(y == 0)  return x;
        if(isEven(x)){
            if(isEven(y)) return gcdWithDivisionMod(x >> 1, y >> 1) << 1;
            else          return gcdWithDivisionMod(x >> 1, y);
        } else {
            if(isEven(y)) return gcdWithDivisionMod(x, y >> 1);
            else          return gcdWithDivisionMod(y, x-y);
        }
    }

    private static boolean isEven(int x){
        return (x & 1) == 0;
    }
}
