package com.interview.algorithms.string;

/**
 * Created_By: stefanie
 * Date: 14-9-7
 * Time: 下午1:55
 */
public class C11_23_LongestNonduplicateSubstring {

    public static int find(String input){
        if(input == null || input.length() == 0) return 0;
        int max = 0;
        int[] mark = new int[256];
        int start = 1;
        int j = 0;
        for(;j < input.length(); j++){
            char ch = input.charAt(j);
            if(mark[ch] == 0){
                mark[ch] = j + 1;
            } else {
                int len = j + 1 - start;
                if(len > max) max = len;
                start = Math.max(mark[ch] + 1, start);
                mark[ch] = j + 1;
            }
        }
        int len = j + 1 - start;
        if(len > max) max = len;
        return max;
    }
}
