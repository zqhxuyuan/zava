package com.interview.books.leetcodeoj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-23
 * Time: 下午10:10
 */
public class LOJ89_GreyCode {
    // i ^ (i >> 1)
    public List<Integer> grayCode(int n) {
        List<Integer> codes = new ArrayList<>();
        for(int i = 0; i < Math.pow(2, n); i++) {
            codes.add(i ^ (i >> 1));
        }
        return codes;
    }
}
