package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-23
 * Time: 上午11:08
 */
public class LOJ67_AddBinary {
    //1. loop on the max length, do a.length() - i. i from 1 to max_length
    //2. use int to do bit operation, result = cha ^ chb ^ carry, and carry = (cha & chb) | ((cha ^ chb) & carry);
    //3. after loop add the last carry to result if carry > 0
    public String addBinary(String a, String b) {
        if(a == null || a.length() == 0) return b;
        if(b == null || b.length() == 0) return a;
        StringBuffer buffer = new StringBuffer();
        int length = Math.max(a.length(), b.length());
        int carry = 0;
        for(int i = 1; i <= length; i++){
            int cha = a.length() - i < 0? 0: a.charAt(a.length() - i) - '0';
            int chb = b.length() - i < 0? 0: b.charAt(b.length() - i) - '0';
            carry = add(cha, chb, carry, buffer);
        }
        if(carry > 0) buffer.insert(0, carry);
        return buffer.toString();
    }

    public int add(int cha, int chb, int carry, StringBuffer buffer){
        buffer.insert(0, (cha ^ chb ^ carry));
        return (cha & chb) | ((cha ^ chb) & carry);
    }
}
