package com.interview.algorithms.general;

/**
 * Created_By: stefanie
 * Date: 14-9-22
 * Time: 下午4:02
 */
public class C1_26A_ChangeBit {

    public static int find(int a, int b){
        int c = a ^ b;
        return C1_26_BinaryOneCounter.count(c);
    }
}
