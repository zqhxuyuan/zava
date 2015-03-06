package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-26
 * Time: 下午2:05
 */
public class LOJ97_InterleavingString {
    //edge case: if(s1.length() + s2.length() != s3.length()) return false;
    //state: interleaving[i][j]: if s3.substring(0, i+j) is interleaving string of s1.substring(0, i) and s2.substring(0, j).
    //initialize:   interleaving[i][0] == true when s3.charAt(i - 1) == s1.charAt(i - 1)
    //              interleaving[0][j] == true when s3.charAt(j - 1) == s2.charAt(j - 1)
    //function:     interleaving[i][j] == true when
    //                  s3.charAt(i + j - 1) == s1.charAt(i - 1) && interleaving[i-1][j]
    //                  s3.charAt(i + j - 1) == s2.charAt(j - 1) && interleaving[i][j-1]
    //result:       interleaving[s1.length()][s2.length()]
    public boolean isInterleave(String s1, String s2, String s3) {
        if(s1.length() + s2.length() != s3.length()) return false;
        boolean[][] interleaving = new boolean[s1.length() + 1][s2.length() + 1];
        interleaving[0][0] = true;
        for(int i = 1; i <= s1.length(); i++){
            if(s1.charAt(i - 1) == s3.charAt(i - 1)) interleaving[i][0] = true;
        }
        for(int j = 1; j <= s2.length(); j++){
            if(s2.charAt(j - 1) == s3.charAt(j - 1)) interleaving[0][j] = true;
        }
        for(int i = 1; i <= s1.length(); i++){
            for(int j = 1; j <= s2.length(); j++){
                if((s1.charAt(i - 1) == s3.charAt(i + j - 1) && interleaving[i - 1][j]) ||
                        (s2.charAt(j - 1) == s3.charAt(i + j - 1) && interleaving[i][j - 1])){
                    interleaving[i][j] = true;
                }
            }
        }
        return interleaving[s1.length()][s2.length()];
    }
}
