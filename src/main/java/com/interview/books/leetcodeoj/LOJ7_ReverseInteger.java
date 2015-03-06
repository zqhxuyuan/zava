package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-18
 * Time: 上午11:33
 */
public class LOJ7_ReverseInteger {
    //1.UPPER_BOUND = Integer.MAX_VALUE/10;
    //2.handle negative case using flag and x = Math.abs(x);
    //3.clarify return what when the number overflow.
    public static int UPPER_BOUND = Integer.MAX_VALUE/10;
    public int reverse(int x) {
        int rev = 0;
        int flag = 1;
        if(x < 0){
            flag = -1;
            x = Math.abs(x);
        }
        while(x > 0){
            if(rev > UPPER_BOUND) return 0;
            else {
                rev = rev * 10 + x % 10;
                x = x / 10;
            }
        }
        return flag * rev;
    }
}
