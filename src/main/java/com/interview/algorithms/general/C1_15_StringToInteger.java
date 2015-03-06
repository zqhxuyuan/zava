package com.interview.algorithms.general;

/**
 * Created_By: stefanie
 * Date: 14-7-9
 * Time: 下午9:17
 *
 * In the transform you need consider the following 4 aspects:
 *   1. no invalid char, input char is digtal
 *   2. positive or negitive integer by - or +
 *   3. overflow of integer range
 *   4. null or empty str
 */
public class C1_15_StringToInteger {
    public static int transfer(String str){
        if(str == null || str.length() == 0) return 0;

        char[] chars = str.toCharArray();
        int start = 0;
        if(chars[0] == '+' || chars[0] == '-')  start++;
        if(chars.length > 10 + start)   return 0;    //out of integer range

        long number = 0;
        while(start < chars.length){
            if(isDigtal(chars[start]))     number = number * 10 + (chars[start++]-'0');
            else                           return 0;
        }

        if(number > Integer.MAX_VALUE) return 0;
        else if (chars[0] == '-')      return 0 - (int) number;
        else                           return (int) number;
    }

    private static boolean isDigtal(char ch){
        return ch >= '0' && ch <= '9';
    }
}
