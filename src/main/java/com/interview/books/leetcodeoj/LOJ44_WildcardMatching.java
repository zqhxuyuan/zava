package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-21
 * Time: 下午6:31
 */
public class LOJ44_WildcardMatching {
    //keeping the last position of star and matched position in str for backtracing
    //1. remember to check p < pattern.length()
    //2. when pattern.charAt(p) == '*', starIdx = p, matched = s, p++;
    //3. when not match and not '*', and starIdx != -1, p = starIdx + 1, s = ++matched;
    //4. remember to go through the end '*' and return p == pattern.length();
    public boolean isMatch(String str, String pattern) {
        int s = 0, p = 0, starIdx = -1, matched = -1;
        while(s < str.length()){
            if(p < pattern.length() && (str.charAt(s) == pattern.charAt(p) || pattern.charAt(p) == '?')){
                s++;
                p++;
            } else if(p < pattern.length() && pattern.charAt(p) == '*'){
                starIdx = p;
                matched = s;
                p++;
            } else if(starIdx != -1){
                p = starIdx + 1;
                s = ++matched;
            } else {
                return false;
            }
        }
        while(p < pattern.length() && pattern.charAt(p) == '*') p++;
        return p == pattern.length();
    }
}
