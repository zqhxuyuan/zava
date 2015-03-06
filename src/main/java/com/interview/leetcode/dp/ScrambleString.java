package com.interview.leetcode.dp;

/**
 * Created_By: stefanie
 * Date: 14-11-17
 * Time: 上午11:40
 *
 * Game: https://oj.leetcode.com/problems/scramble-string/
 *
 * Solution:
 *  State matchChar[len][i][j]: the substr which length is len start from i in s1 and j in s2 is scramble
 *  Init: for len = 1, matchChar[1][i][j] = true if s1.charAt(i) ==  s2.charAt(j)
 *  Produce:  matchChar[len][i][j] = true when we could find a break point k:
 *              matchChar[k][i][j] and matchChar[len-k][i+k-1][j+k-1]
 *        or    matchChar[k][i][j+len-k] and matchChar[len-k][i+k][j]
 *        example s1 -> s11 and s12 and s2 -> s21 and s22 by K
 *              first s11 matches s21 and s12 matches s22
 *              or    s11 matches s22 and s12 matches s12     //be carefully to calculate the substring
 *        since matchChar[*][i][j] depends on matchChar[*][>i][>j] so, scan len from 2 ~ n and i, j from n-len ~ 0
 *  Result: matchChar[n][0][0]
 */
public class ScrambleString {

    public static boolean isScramble(String s1, String s2) {
        if( s1.length() != s2.length()) return false;
        if( s1.length() == 0 || s1.equals(s2)) return true;

        int n = s1.length();
        boolean[][][] scramble = new boolean[n + 1][n][n];
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                if(s1.charAt(i) == s2.charAt(j)) scramble[1][i][j] = true;
            }
        }

        for(int len = 2; len <= n; len++){
            for(int i = 0; i <= n - len; i++){
                for(int j = 0; j <= n - len; j++){
                    boolean found = false;
                    for(int left = 1; left < len && !found; left++){
                        int right = len - left;
                        found = (scramble[left][i][j] && scramble[right][i+left][j+left])
                             || (scramble[left][i][j+right] && scramble[right][i+left][j]);
                    }
                    scramble[len][i][j] = found;
                }
            }
        }
        return scramble[n][0][0];
    }
}
