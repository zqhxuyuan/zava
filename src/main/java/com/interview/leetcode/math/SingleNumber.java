package com.interview.leetcode.math;

/**
 * Created_By: stefanie
 * Date: 14-11-18
 * Time: 上午7:07
 *
 * 1. Given an array of integers, every element appears two times except for one. Find that single one.
 * 2. Given an array of integers, every element appears three times except for one. Find that single one.
 * 3. Given an array of integers, every element appears two times except for two. Find that single one.
 */
public class SingleNumber {
    public static int findOne2(int[] num){
        int xor = 0;
        for(int i = 0; i < num.length; i++) xor ^= num[i];
        return xor;
    }

    public static int findOne3(int[] num){
        int once = 0; int twice = 0;
        for(int i = 0; i < num.length; i++){
            once = (once ^ num[i]) & ~twice;
            twice = (twice ^ num[i]) & ~once;
        }
        return once;
    }

    public static int findOneK(int[] num, int k){
        int[] marker = new int[k-1];
        for(int i = 0; i < num.length; i++){
            for(int j = 0; j < marker.length; j++){
                marker[j] = (marker[j] ^ num[i]) & ~(j == 0? marker[k-2] : marker[j - 1]);
            }
        }
        return marker[k-2];
    }

    public static int[] findTwo2(int[] num){
        int xor = 0;
        for(int i = 0; i < num.length; i++) xor ^= num[i];

        int a = xor;
        int offset = 0;
        while((a & 1) == 0) {    //found the lowest one, and divide num into two sets: 1 in this bit and 0 in this bit
            a = a >> 1;          //the two number will not in the same set
            offset++;
        }

        a = xor;
        for(int i = 0; i < num.length; i++){
            if(((num[i] >> offset) & 1) == 1) a  = a ^ num[i];
        }
        return new int[] {a, xor ^ a};
    }
}
