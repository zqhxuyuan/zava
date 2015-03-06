package com.interview.books.leetcodeoj;

import java.util.HashMap;

/**
 * Created_By: stefanie
 * Date: 14-12-30
 * Time: 下午3:50
 */
public class LOJ166_FractionRecurringDecimal {
    //use HashMap to store index of division of numerator in StringBuffer, if find existed numerator, make recurring
    //1. numerator and denominator can be negative, need tracking flag and change using Math.abs()
    //2. numerator and denominator can be out of range when do abs(), so need use Long
    public String fractionToDecimal(int numerator, int denominator) {
        if(numerator == 0) return "0";

        boolean isPositive = (numerator > 0 && denominator > 0) || (numerator < 0 && denominator < 0);

        long numeratorL = Math.abs((long) numerator);
        long denominatorL = Math.abs((long) denominator);

        StringBuffer buffer = new StringBuffer();
        HashMap<Long, Integer> offsets = new HashMap();

        buffer.append(numeratorL / denominatorL);
        numeratorL = numeratorL % denominatorL;

        if(numeratorL != 0) buffer.append(".");
        while(numeratorL != 0){
            if(offsets.containsKey(numeratorL)){
                buffer.insert(offsets.get(numeratorL), "(");
                buffer.append(")");
                break;
            }
            offsets.put(numeratorL, buffer.length());
            numeratorL = numeratorL * 10;
            buffer.append(numeratorL / denominatorL);
            numeratorL = numeratorL % denominatorL;
        }
        return isPositive? buffer.toString() : "-" + buffer.toString();
    }

    public static void main(String[] args){
        LOJ166_FractionRecurringDecimal finder = new LOJ166_FractionRecurringDecimal();
//        System.out.println(finder.fractionToDecimal(1, 2));
//        System.out.println(finder.fractionToDecimal(2, 1));
//        System.out.println(finder.fractionToDecimal(2, 3));
//        System.out.println(finder.fractionToDecimal(1, 6));
        System.out.println(finder.fractionToDecimal(1, 99));
        System.out.println(finder.fractionToDecimal(0, -5));
        System.out.println(finder.fractionToDecimal(-1, -2147483648));


    }
}
