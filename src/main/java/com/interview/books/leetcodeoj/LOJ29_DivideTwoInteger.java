package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-18
 * Time: 下午4:47
 */
public class LOJ29_DivideTwoInteger {
    //use minus to do division, check how many divisor, given dividend can minus.
    //optmized solution is: try to minus most 2^i divisor (can be calculated by left shift).
    //1.clarify the edge case handling
    //2.carefully about negative dividend and divisor
    //3.use long to do the shift calculation to avoid overflow.
    //4.while condition a >= b
    //5.shift start from 0
    //6.update:a -= b << (shift - 1); and answer += (1 << (shift - 1));
    public int divide(int dividend, int divisor) {
        if(divisor == 1) return dividend;
        else if(divisor == -1){
            if(dividend == Integer.MIN_VALUE) return Integer.MAX_VALUE;
            else return -dividend;
        }
        boolean negative = (dividend < 0 && divisor > 0) || (dividend > 0 && divisor < 0);
        long a = Math.abs((long)dividend);
        long b = Math.abs((long)divisor);
        int answer = 0;
        while(a >= b){
            int shift = 0;
            while(a >= (b << shift))
                shift++;
            a -= b << (shift - 1);
            answer += (1 << (shift - 1));
        }
        return negative? -answer : answer;
    }

    public static void main(String[] args){
        LOJ29_DivideTwoInteger divider = new LOJ29_DivideTwoInteger();
        System.out.println(divider.divide(-2147483648, -1));
    }
}
