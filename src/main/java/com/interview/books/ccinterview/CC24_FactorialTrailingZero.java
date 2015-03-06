package com.interview.books.ccinterview;

/**
 * Created_By: stefanie
 * Date: 14-12-13
 * Time: 下午10:51
 */
public class CC24_FactorialTrailingZero {
    public int count(int n){
        int count = 0;
        if(n < 0) {
            return -1;
        }
        for(int i = 5; n / i > 0; i *= 5){
            count += n / i;
        }
        return count;
    }

    public static void main(String[] args){
        CC24_FactorialTrailingZero counter = new CC24_FactorialTrailingZero();
        System.out.println(counter.count(10));
        System.out.println(counter.count(20));
        System.out.println(counter.count(25));
    }
}
