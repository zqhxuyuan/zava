package com.interview.algorithms.general;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 8/4/14
 * Time: 5:23 PM
 */
public class C1_45_PowerNumber {
    public static double power(int base, int exp){
        double i = 1.0;
        for(int k = 1; k <= exp; k++){
            i = i * base;
        }
        return i;
    }

    public static double power2(int base, int exp){
        if (exp == 1) return base;
        double half = power(base, exp >> 1);
        return (((exp & 1) == 1) ? base : 1.0) * half * half;
    }
}
