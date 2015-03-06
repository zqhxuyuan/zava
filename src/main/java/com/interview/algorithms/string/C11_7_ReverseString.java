package com.interview.algorithms.string;

/**
 * Created_By: stefanie
 * Date: 14-7-3
 * Time: 下午7:30
 */
public class C11_7_ReverseString {
    public static String reverse(String str){
        if(str == null) return null;
        int N = str.length();
        char[] chars = new char[N];
        for(int i = 0; i < str.length(); i++){
            chars[i] = str.charAt(N-i-1);
        }
        return String.copyValueOf(chars);
    }
}

