package com.interview.leetcode.backtracing;

/**
 * Created_By: stefanie
 * Date: 14-11-17
 * Time: 上午10:16
 * Implement wildcard pattern matching with support for '?' and '*'.
 *      '?' Matches any single character.
 *      '*' Matches any sequence of characters (including the empty sequence).
 *
 *  The basic idea is to have one pointer for the string and one pointer for the pattern.
 *    case 1: when p is '?' or matched to s, both p and s go one step
 *    case 2: when p is '*', mark p position to starIdx, and s position to s, and only p move one step
 *    case 3: when case 1 and case 2 failed, backtrace if previous '*' is not correctly matched.  (starIdx != -1 means have previous '*')
 *              move p to starIdx + 1, and s to ++matchChar
 *            in case 3, have this back-tracing, so can't guarantee to O(M + N), in worst case will be O(M*N)
 *
 *  The complexity of the algorithm is O(M*N), where N and M are the lengths of the pattern and input strings. An example of such a worst-case input is:
 *    input: bbbbbbbbbbbb pattern: *bbbb
 *
 *  Tricks:
 *     1. back-tracing the last matched position in s and last '*' position in p, when matchChar using '*'
 *        so when can't find a solution, could back-tracing to next position in s and p to restart to matchChar
 *        and it's only back-tracing to the last visited '*'
 */
public class WildcardMatching {
    public static boolean isMatch(String str, String pattern) {
        int s = 0, p = 0, match = 0, starIdx = -1;
        while (s < str.length()){
            // advancing both pointers: when p is '?' or matched char
            if (p < pattern.length()  && (pattern.charAt(p) == '?' || str.charAt(s) == pattern.charAt(p))){
                s++;
                p++;
            }
            // * found, only advancing pattern pointer
            else if (p < pattern.length() && pattern.charAt(p) == '*'){
                starIdx = p;
                match = s;
                p++;
            }
            // last pattern pointer was *, advancing string pointer
            else if (starIdx != -1){
                p = starIdx + 1;
                s = ++match;
            }
            //current pattern pointer is not star, last patter pointer was not *
            //characters do not matchChar
            else return false;
        }

        //check for remaining characters in pattern
        while (p < pattern.length() && pattern.charAt(p) == '*')
            p++;

        return p == pattern.length();
    }
}
