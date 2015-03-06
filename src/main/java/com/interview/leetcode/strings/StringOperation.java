package com.interview.leetcode.strings;

/**
 * Created_By: stefanie
 * Date: 14-11-17
 * Time: 下午7:02
 */
public class StringOperation {
    /**
     * since s contains more ' ', can't do it in place, so using StringBuilder will simplify the solution
     */
    public static String reverseWords(String s) {
        StringBuilder reversed = new StringBuilder();
        int end = s.length();
        for(int i = s.length() - 1; i >= 0; i--){
            if(s.charAt(i) == ' ') end = i;
            else if(i == 0 || s.charAt(i - 1) == ' '){
                if(reversed.length() > 0) reversed.append(' ');
                reversed.append(s.substring(i, end));
            }
        }
        return reversed.toString();
    }

    /**
     * in place reverse: first reverse the sens, then reverse every word
     */
    public static void reverseWords(char[] s) {
        reverse(s, 0, s.length - 1);
        for(int i = 0, j = 0; j <= s.length; j++){
            if(j == s.length || s[j] == ' ') {
                reverse(s, i, j - 1);
                i = j + 1;   //don't swap ' '
            }
        }
    }

    public static void reverse(char[] str, int begin, int end){
        for(int i = 0; i < (end - begin + 1) / 2; i++){
            char temp = str[begin + i];
            str[begin + i] = str[end - i];
            str[end - i] = temp;
        }
    }

    /**
     * ZigZag sequence
     */
    public String zigzagConvert(String s, int nRows) {
        if(nRows == 1) return s;
        StringBuilder[] builders = new StringBuilder[nRows];
        for(int i = 0; i < nRows; i++) builders[i] = new StringBuilder();
        int row = -1;
        boolean down = true;
        for(int i = 0; i < s.length(); i++){
            if(down) builders[++row].append(s.charAt(i));
            else builders[--row].append(s.charAt(i));
            if(row == nRows - 1) down = false;
            else if(row == 0) down = true;
        }
        for(int i = 1; i < nRows; i++){
            builders[0].append(builders[i].toString());
        }
        return builders[0].toString();
    }
}
