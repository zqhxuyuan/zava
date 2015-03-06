package com.interview.algorithms.general;

/**
 * Created_By: stefanie
 * Date: 14-11-5
 * Time: 下午3:55
 */
public class C1_72_IntegerPalindrome {
    public static boolean isPalindrome(int x) {
        if(x < 0) return false;
        return isPalindrome(x, 0);
    }

    private static boolean isPalindrome(int x, int prev){
        int mod = x % 10;
        prev = prev * 10 + mod;
        if(prev == x) return true; //odd offset, such as 101
        x = x / 10;
        if(prev == x) return true; //even offset, such as 1001
        //when the lower offset contains 0, and higher is not empty, shouldn't have palindrome,
        //since the highest offset can't be 0
        if(prev == 0 && x != 0) return false;
        if(x > 0){
            return isPalindrome(x, prev);
        } else {
            return false;
        }
    }
}
