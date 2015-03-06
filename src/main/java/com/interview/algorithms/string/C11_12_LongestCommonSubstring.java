package com.interview.algorithms.string;

/**
 * Created_By: stefanie
 * Date: 14-7-7
 * Time: 下午10:18
 */
public class C11_12_LongestCommonSubstring {

    public static String LCS(String str1, String str2){
        int maxLength = 0;
        int maxI = 0;
        int M = str1.length() + 1;
        int N = str2.length() + 1;

        int[][] c = new int[M][N];

        for(int i = 0; i < M; i++)  c[i][0] = 0;
        for(int i = 0; i < N; i++)  c[0][i] = 0;

        for(int i = 1; i < M; i++){
            for(int j = 1; j < N; j++){
                if(str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    c[i][j] = c[i-1][j-1] + 1;
                    if(c[i][j] > maxLength){
                        maxLength = c[i][j];
                        maxI = i;
                    }
                }
                else c[i][j] = 0;
            }
        }
        if(maxLength > 0) {
            char[] chars = new char[maxLength];
            for(int i = maxLength - 1; i >= 0; i--) chars[i] = str1.charAt(--maxI);
            return String.copyValueOf(chars);
        } else {
            return "";
        }
    }
}
