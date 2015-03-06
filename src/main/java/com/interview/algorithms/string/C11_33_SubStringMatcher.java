package com.interview.algorithms.string;

import com.interview.algorithms.general.C1_59_PrimeNumber;

/**
 * Created_By: stefanie
 * Date: 14-10-24
 * Time: 下午1:46
 */
public class C11_33_SubStringMatcher {
    static int[] PRIMS;
    static {
        PRIMS = C1_59_PrimeNumber.generate(26);
    }
    public static boolean containsAllChar(String a, String b){
        b = b.toLowerCase();
        a = a.toLowerCase();
        if(b.length() > a.length()) return false;
        long pro = 1;
        for(int i = 0; i < a.length(); i++) pro *= PRIMS[(a.charAt(i) - 'a')];
        for(int i = 0; i < b.length(); i++){
            if(pro % PRIMS[(b.charAt(i) - 'a')] == 0) pro /= PRIMS[(b.charAt(i) - 'a')];
            else return false;
        }
        return true;
    }
}
