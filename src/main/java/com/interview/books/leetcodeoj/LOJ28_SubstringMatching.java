package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-19
 * Time: 上午10:18
 */
public class LOJ28_SubstringMatching {

    //Naive solution: for every position of str, try to check if it can match pattern. O(N^2).
    //KMP: optimize by minimize the backtracing in str, str only go forward, and pattern backtracing to least using next[].
    //     next[] is calculate based on the longest suffix equals to prefix.
    //basic process: match str.charAt(i) and pattern.charAt(j),
    //               if match both move towards, and check if already visit to the end of pattern
    //               if not match, if j == 0, just need move i forward, if j != 0, move j to next[j]
    //calNext:       init next[0] = next[1] = 0;
    //               for other i, init j = next[i-1], and match pattern.charAt(j) and pattern.charAt(i-1)
    //               if match, next[i] = j + 1;
    //               if not match, if j == 0, next[i] = 0; if j != 0, move j to next[j];
    public int strStr(String str, String pattern) {
        if(pattern == null || pattern.length() == 0) return 0;
        if(str == null || str.length() == 0) return -1;
        int[] next = calNext(pattern);
        int i = 0; int j = 0;
        while(i < str.length()){
            if(str.charAt(i) == pattern.charAt(j)){
                i++;
                j++;
                if(j == pattern.length()) return i - j;
            } else if(j == 0) i++;
            else j = next[j];
        }
        return -1;
    }

    private int[] calNext(String pattern){
        int[] next = new int[pattern.length() > 2? pattern.length() : 2];
        next[0] = 0; next[1] = 0;
        for(int i = 2; i < pattern.length(); i++){
            int j = next[i-1];
            while(true){
                if(pattern.charAt(j) == pattern.charAt(i-1)){
                    next[i] = j + 1;
                    break;
                } else if(j == 0){
                    next[j] = 0;
                    break;
                } else j = next[j];
            }
        }
        return next;
    }

    public static void main(String[] args){
        LOJ28_SubstringMatching matcher = new LOJ28_SubstringMatching();
        System.out.println(matcher.strStr("ABABABAC", "ABABAC"));
    }
}
