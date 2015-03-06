package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-23
 * Time: 下午9:55
 */
public class LOJ87_ScrambleString {
    //state: scramble[len][i][j], whether substring of length len start from i in s1 and j in s2 are scramble.
    //initialize: scramble[1][i][j] = true, if s1.charAt(i) == s2.charAt(j)
    //function: scramble[len][i][j] = true for any cutting point k from 1 to len - 1 meeting one of the following conditions:
    //          1) scramble[k][i][j] and scramble[len-k][i+k][j+k]
    //          2) scramble[k][i][j+len-k] and scramble[len-k][i+k][j]
    //result: scramble[n][0][0]
    public boolean isScramble(String s1, String s2) {
        if(s1.length() != s2.length()) return false;
        if(s1.length() == 0 || s1.equals(s2)) return true;

        int n = s1.length();
        boolean[][][] scramble = new boolean[n + 1][n][n];

        for(int i = 0; i < s1.length(); i++){
            for(int j = 0; j < s2.length(); j++){
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
