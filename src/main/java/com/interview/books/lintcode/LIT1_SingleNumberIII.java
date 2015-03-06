package com.interview.books.lintcode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 15-1-24
 * Time: 下午8:38
 */
public class LIT1_SingleNumberIII {
    //assume number is A and B, first find the xor of A and B, xor should at least have one bit is 1,
    //find the bit, it is the differentiation of A nd B.
    //1. number != 0 and number >>> 1 as the condition to find the lowest 1, for negative numbers.
    public List<Integer> singleNumberIII(int[] A) {
        List<Integer> numbers = new ArrayList();

        int xor = 0;
        for(int i = 0; i < A.length; i++) xor ^= A[i];

        //find the lowest 1 in the xor
        int number = xor;
        int k = 0;
        while(number != 0){
            if((number & 1) != 0) break;
            number = number >>> 1;
            k++;
        }

        int num1 = 0;
        int mask = 1 << k;
        for(int i = 0; i < A.length; i++){
            if((A[i] & mask) == 0) num1 ^= A[i];
        }
        numbers.add(num1);
        numbers.add(xor ^ num1);
        return numbers;
    }
}
