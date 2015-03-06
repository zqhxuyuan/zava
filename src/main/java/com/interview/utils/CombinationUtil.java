package com.interview.utils;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/17/14
 * Time: 2:49 PM
 */
public class CombinationUtil {

    public static long factorial(int n){
        long factorial = 1;
        for(int i = 2; i <= n; i++) factorial *= i;
        return factorial;
    }

    /**
     * A(n,m)=n(n-1)(n-2)……(n-m+1)= n!/(n-m)!
     */
    public static long permutation(int base, int select){
        return factorial(base) / factorial(base - select);
    }

    /**
     * C(n,m)=A(n,m)/m！= n!/((n-m)! m!)
     */
    public static long combination(int base, int select){
        return factorial(base)/(factorial(base - select) * factorial(select));
    }

    /**
     * C(n,0)+C(n,1)+C(n,2)+...+C(n,n-1)+C(n,n) = 2^n
     */
    public static long fullCombination(int base){
        return (long) Math.pow(2, base);
    }
}
