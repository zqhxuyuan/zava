package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-7-29
 * Time: 下午10:38
 *
 * Given an array contains only 2 number appear only once, the other all appear twice. Write code to find the 2 numbers.
 *
 * Solution:
 *  A XOR B == 0 if A = B
 *
 *  1. XOR all the numbers, get S = A XOR B.
 *  2. Get lowest 1 number in S, that means in this bit(index K), A != B
 *  3. S XOR all the numbers which K offset is 1, so will not XOR A. so the result is A
 *  4. return A and S XOR A (which is B)
 */

class Result{
    int n1;
    int n2;
    public Result(int n1, int n2){
        this.n1 = n1;
        this.n2 = n2;
    }
}

public class C4_41_FindOnceNumber {

    public static Result find(int[] array){
        int s = 0;
        for(int i = 0; i < array.length; i++)  s ^= array[i];

        int temp1 = s;
        int temp2 = s;
        int k = 0;
        while((temp1&1) == 0){        //get lowest 1 number, that means in this bit, n1 != n2
            temp1 = temp1 >> 1;
            k++;
        }
        for(int i=0; i<array.length; i++){  //only XOR number follow the same condition with n1 to find n2
            if(((array[i]>>k)&1) == 1){
                temp2 ^= array[i];
            }
        }
        return new Result(temp2, s^temp2);
    }
}
