package com.interview.books.ccinterview;

/**
 * Created_By: stefanie
 * Date: 14-12-14
 * Time: 下午12:31
 */
public class CC30_AddNumber {

    public static int add(int a, int b){
        if(b == 0) return a;
        int sum = a ^ b;
        int carry = (a & b) << 1;
        return add(sum, carry);
    }
}
