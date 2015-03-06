package com.interview.leetcode.strings;

/**
 * Created_By: stefanie
 * Date: 14-11-17
 * Time: 上午12:11
 */
public class MinWindowSubstring {

    public String minWindow(String S, String T) {
        if (S == null || S.length() == 0) return S;
        if (T == null || T.length() == 0) return "";

        int[] tCounter = new int[256];   //count char appear in T
        for (int i = 0; i < T.length(); i++) tCounter[T.charAt(i)]++;

        int[] mCounter = new int[256];  //count char appear in S
        String window = "";
        int count = 0, begin = 0;
        for (int i = 0; i < S.length(); i++) {
            char c = S.charAt(i);
            mCounter[c]++;
            if (tCounter[c] == 0) continue;
            if (mCounter[c] <= tCounter[c]) count ++;  //if not a duplication, count one
            if (count == T.length()) {  //find a substring contains all char, shrink begin
                while (begin < S.length()) {
                    Character ch = S.charAt(begin);
                    if(mCounter[ch] > tCounter[ch]){
                        mCounter[ch]--; //have duplication
                        begin ++;
                    } else break;
                }
                if (window == "" || i - begin + 1 < window.length()) window = S.substring(begin, i + 1);
            }
        }
        return window;
    }
}
