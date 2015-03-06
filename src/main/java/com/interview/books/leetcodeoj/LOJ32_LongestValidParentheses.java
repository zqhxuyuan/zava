package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-19
 * Time: 下午3:52
 */
public class LOJ32_LongestValidParentheses {

    //State: len[i] the longest valid parentheses end with (i-1)-th char
    //Transfer: if s.charAt(i-1) == '('   len[i] = 0
    //          if s.charAt(i-1) == ')' && i - len[i-1] - 2 >= 0 && S.charAt(i-len[i-1] - 2) == ‘('
    //              len[i] = len[i-1] + 2 + len[i-len[i-1] - 2]
    //Init: len[0] = 0
    //Answer: max of len[*]
    public int longestValidParentheses(String s) {
        int[] len = new int[s.length() + 1];
        len[0] = 0;
        for (int i = 1; i <= s.length(); i++) {
            if (s.charAt(i - 1) == '(') len[i] = 0;
            else if (i - len[i - 1] - 2 >= 0 && s.charAt(i - len[i - 1] - 2) == '(') {
                len[i] = len[i - 1] + 2 + len[i - len[i - 1] - 2];
            }
        }
        int max = 0;
        for (int i = 1; i <= s.length(); i++) max = Math.max(max, len[i]);
        return max;
    }

    public static void main(String[] args){
        LOJ32_LongestValidParentheses finder = new LOJ32_LongestValidParentheses();
        System.out.println(finder.longestValidParentheses(")"));
    }
}
