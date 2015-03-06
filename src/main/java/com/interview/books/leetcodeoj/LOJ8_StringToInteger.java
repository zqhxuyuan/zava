package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-18
 * Time: 上午11:53
 */
public class LOJ8_StringToInteger {
    //try to parse the str and build an integer
    //1.remember to handle negative case: parse sign
    //2.use method in Character to make clean code: Character.isWhitespace(), Character.isDigit(),Character.getNumericValue();
    //3.remember to check overflow, num == max && digit >= 8. (INT_MAX (2147483647) or INT_MIN (-2147483648))
    //4.return MAX_VALUE or MIN_VALUE based on sign: sign == 1? Integer.MAX_VALUE : Integer.MIN_VALUE;
    public static int max = Integer.MAX_VALUE / 10;
    public int atoi(String str) {
        int i = 0; int sign = 1; int n = str.length(); int num = 0;
        while (i < n && Character.isWhitespace(str.charAt(i))) i++;
        if(i < n && str.charAt(i) == '+') i++;
        else if(i < n && str.charAt(i) == '-'){
            sign = -1;
            i++;
        }
        while(i < n && Character.isDigit(str.charAt(i))){
            int digit = Character.getNumericValue(str.charAt(i));
            if(num > max || num == max && digit >= 8)
                return sign == 1? Integer.MAX_VALUE : Integer.MIN_VALUE;
            num = num * 10 + digit;
            i++;
        }
        return sign * num;
    }
}
