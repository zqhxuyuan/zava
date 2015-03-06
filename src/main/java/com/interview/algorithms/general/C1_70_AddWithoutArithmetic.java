package com.interview.algorithms.general;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 10/27/14
 * Time: 1:30 PM
 */
public class C1_70_AddWithoutArithmetic {
    public static int add(int a, int b){
        if(b == 0) return a;
        else if(a == 0) return b;
        else {
            int sum = a ^ b;
            int carry = (a & b) << 1;
            return add(sum, carry);
        }
    }
}
