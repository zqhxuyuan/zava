package com.interview.algorithms.string;

import com.interview.utils.ArrayUtil;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/15/14
 * Time: 2:58 PM
 */
public class C11_27_RemoveWhiteSpaceAndReverse {

    public static String clean(String str){
        char[] chars = str.toCharArray();
        int i = -1;
        int j = 0;
        for(int k = 0; k < chars.length; k++){
            if(chars[k] != ' ') {
                if(i == -1) i = j;
                chars[j++] = chars[k];
            } else {
                if(k > 0 && chars[k-1] != ' ') {
                    ArrayUtil.swap(chars, i, j-1);
                    i = -1;
                    chars[j++] = ' ';
                }
            }
        }
        if(i != -1) {
            ArrayUtil.swap(chars, i, j-1);
            return String.copyValueOf(chars, 0, j);
        } else {
            return String.copyValueOf(chars, 0, j-1);
        }
    }
}
