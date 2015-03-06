package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-18
 * Time: 下午12:04
 */
public class LOJ9_PalindromeNumber {
    //1. check overflow when reverse
    //2. clarify return what for negative: just false;
    public static int max = Integer.MAX_VALUE/10;
    public boolean isPalindrome(int x) {
        if(x < 0) return false;
        int rev = reverse(x);
        return rev == x;
    }

    public int reverse(int x){
        int rev = 0;
        while(x > 0){
            if(rev > max) return 0;
            rev = rev * 10 + x % 10;
            x = x / 10;
        }
        return rev;
    }
}
