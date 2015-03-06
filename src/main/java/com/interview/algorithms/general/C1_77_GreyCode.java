package com.interview.algorithms.general;

import java.util.LinkedList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-7
 * Time: 下午10:29
 */
public class C1_77_GreyCode {

    public static List<Integer> grayCode(int n) {
        List<Integer> codes = new LinkedList<Integer>();
        for(int i = 0; i < Math.pow(2, n); i++) {
            int i2 = i>>1;
            int x = i^i2;
            codes.add(x);
        }
        return codes;
    }
}
