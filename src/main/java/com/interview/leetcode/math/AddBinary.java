package com.interview.leetcode.math;

/**
 * Created_By: stefanie
 * Date: 14-11-13
 * Time: 下午10:29
 *
 * Given two binary strings, return their sum (also a binary string).
 *  For example,    a = "11"    b = "1"     Return "100".
 *
 * Tricks:
 *  1. create a general function to calculate every offset number
 *  2. handle the common part, then do the remain part
 *
 */
public class AddBinary {

    public String addBinary(String a, String b) {
        StringBuilder builder = new StringBuilder();
        int carry = 0;
        int la = a.length() - 1;
        int lb = b.length() - 1;
        int common = Math.min(a.length(), b.length());
        for(int i = 0; i < common; i++){
            carry = sum(a.charAt(la - i), b.charAt(lb - i), carry, builder);
        }
        String remain = a.length() == common? b : a;
        int left = remain.length() - common;
        for(int i = left - 1; i >= 0; i--){
            carry = sum(remain.charAt(i), '0', carry, builder);
        }
        if(carry == 1) builder.insert(0, 1);
        return builder.toString();
    }

    private static int sum(char cha, char chb, int carry, StringBuilder builder){
        if(cha == '1' && chb == '1') {
            builder.insert(0, carry);
            return 1;
        } else if(cha == '0' && chb == '0'){
            builder.insert(0, carry);
            return 0;
        } else {
            if(carry == 1) builder.insert(0, 0);
            else builder.insert(0, 1);
            return carry;
        }
    }
}
