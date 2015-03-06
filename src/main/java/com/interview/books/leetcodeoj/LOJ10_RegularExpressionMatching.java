package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-18
 * Time: 下午12:19
 */
public class LOJ10_RegularExpressionMatching {

    //match[i][j]: if s.substring(0,i) can matchChar p.substring(0,j);
    //init: match[0][0] = true, if(p.charAt(j - 1) == '*') match[0][j] = match[0][j-2]
    //function: match[i][j] = true if
    //     match[i-1][j-1] && matchChar(i, j)
    //     if(p.charAt(j - 1) == '*'
    //           match[i][j-2]    //""  matching "a*"
    //           match[i][j-1]    //"a" matching "a*"
    //           matchChar(i, j - 1) && match[i-1][j] //"aa..a" matching "a*"
    //     the third case: match[i-1][j] not match[i-1][j-1], need include * in the matched p
    //result: match[s.length()][p.length()]
    //matchChar(i, j): means (p.charAt(j - 1) == '.' or s.charAt(i - 1) == p.charAt(j - 1)
    public static boolean isMatch(String s, String p) {
        boolean[][] match = new boolean[s.length() + 1][p.length() + 1];
        match[0][0] = true;
        for(int j = 2; j <= p.length(); j++){
            if(p.charAt(j - 1) == '*') match[0][j] = match[0][j-2];
        }
        for(int i = 1; i <= s.length(); i++){
            for(int j = 1; j <= p.length(); j++){
                if(match[i-1][j-1] && matchChar(s, p, i, j)) match[i][j] = true;
                else if(p.charAt(j-1) == '*'){
                    match[i][j] = match[i][j-2] || match[i][j-1]
                            || matchChar(s, p, i, j - 1) && match[i-1][j];
                }
            }
        }
        return match[s.length()][p.length()];
    }

    public static boolean matchChar(String s, String p, int i, int j){
        return p.charAt(j-1) == '.' || p.charAt(j-1) == s.charAt(i-1);
    }

    public static void main(String[] args){
        LOJ10_RegularExpressionMatching matcher = new LOJ10_RegularExpressionMatching();
//        System.out.println(matcher.isMatch("bac", "bac*"));
//        System.out.println(matcher.isMatch("bc", "bca*"));
//        System.out.println(matcher.isMatch("ba", "ba*"));
//        System.out.println(matcher.isMatch("bac", "ba*"));
//        System.out.println(matcher.isMatch("bc", "ba*c"));
        System.out.println(matcher.isMatch("aaa", ".*"));
    }
}
