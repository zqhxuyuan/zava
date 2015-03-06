package com.interview.algorithms.string;

/**
 * Created_By: stefanie
 * Date: 14-7-7
 * Time: 下午9:34
 *
 * Write a method to find the longest common sequence (no need to be continuous) of characters.
 */
public class C11_11_LongestCommonSubsequence {
    public static String LCS(String str1, String str2){
        int M = str1.length() + 1;
        int N = str2.length() + 1;
        int[][] c = new int[M][N];
        int[][] b = new int[M][N];

        for(int i = 0; i < M; i++)  c[i][0] = 0;
        for(int i = 0; i < N; i++)  c[0][i] = 0;

        for(int i = 1; i < M; i++){
            for(int j = 1; j < N; j++){
                if(str1.charAt(i - 1) == str2.charAt(j - 1)){
                    c[i][j] = c[i-1][j-1] + 1;
                    b[i][j] = 0;
                } else {
                    if(c[i-1][j] > c[i][j-1]){
                        c[i][j] = c[i-1][j];
                        b[i][j] = -1;
                    } else {
                        c[i][j] = c[i][j-1];
                        b[i][j] = 1;
                    }
                }
            }
        }
        return backtrace(c, b, str1, str2);
    }

    private static String backtrace(int[][] c, int[][] b, String str1, String str2){
        int i = str1.length();
        int j = str2.length();
        int length = c[i][j];
        char[] chars = new char[length];
        while(length != 0){
            if(b[i][j] == 0){
                chars[length - 1] = str1.charAt(i - 1);
                length = c[--i][--j];
            } else if(b[i][j] == 1)  j--;
            else i--;
        }
        return String.copyValueOf(chars);
    }
}
