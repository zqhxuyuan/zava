package com.interview.algorithms.bit;

/**
 * Created_By: stefanie
 * Date: 14-10-20
 * Time: 下午10:29
 */
public class C16_4_ReverseBinaryCode {
    public static int reverse(int number){
        if(number == 0) return 0;
        int end = (number & 1) == 0? 1 : 0;
        return (reverse(number >>> 1) << 1) + end;
    }

    public static int reverseFully(int number){
        return reverseFully(number, 32);
    }

    private static int reverseFully(int number, int offset){
        if(offset == 0) return 0;
        int end = (number & 1) == 0? 1 : 0;
        return (reverseFully(number >>> 1, --offset) << 1) + end;
    }
}
