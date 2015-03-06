package com.interview.algorithms.general;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/15/14
 * Time: 11:05 AM
 *
 * Given a int number, write code to judge the number of all its factor is an even number or an odd number
 *
 * Every number will have some factor pair, such as
 * 24 can be factorize into (1,24),(2,12),(4,6), 6 factor number
 * 36 can be factorize into (1,36),(2,18),(3,12),(4,9),(6,6), only 9 factor numbers since 36 is a square of 6.
 *
 * So we could get this conclusion: if a number is a square of the other number, the factor should be odd number
 *  (since one number appear twice as factor pair), otherwise is even number.
 */
public class C1_56_AllFactorNumber {

    public static boolean isOddFactor(int number){
        double factor = Math.sqrt(number);
        if(factor - Math.floor(factor) == 0) return true;
        else return false;
    }
}
