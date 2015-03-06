package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-26
 * Time: 下午5:11
 */
public class LOJ115_DistinctSubsequence {
    //state: count[i][j]: is the distinct subsequence count of S.substring(0, i) and T.substring(0, j)
    //initialize: count[0][j] = 0 and count[i][0] = 1
    //function: if S.charAt(i - 1) != T.charAt(j - 1) count[i][j] = count[i-1][j]
    //          if S.charAt(i - 1) == T.charAt(j - 1) count[i][j] = count[i-1][j] + count[i-1][j-1]
    //result: count[S.length()][T.length()]
    public int numDistinct(String S, String T) {
        int[][] count = new int[S.length() + 1][T.length() + 1];
        for(int i = 0; i <= S.length(); i++) count[i][0] = 1;
        for(int i = 1; i <= S.length(); i++){
            for(int j = 1; j <= T.length(); j++){
                count[i][j] = count[i-1][j];
                if(S.charAt(i - 1) == T.charAt(j - 1)) count[i][j] += count[i-1][j-1];
            }
        }
        return count[S.length()][T.length()];
    }
}
