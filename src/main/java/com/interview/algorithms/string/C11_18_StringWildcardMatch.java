package com.interview.algorithms.string;

/**
 * Created_By: stefanie
 * Date: 14-7-19
 * Time: 下午2:02
 */
public class C11_18_StringWildcardMatch {

    public static boolean match(String expr, String str){
        return match(expr, str, 0, 0);
    }

    private static boolean match(String expr, String str, int i, int j){
        if(i >= expr.length())  return true;
        if(i < expr.length() && j >= str.length()) return false;

        if(expr.charAt(i) == '*'){
            return match(expr, str, i, j+1) || match(expr, str, i+1, j) || match(expr, str, i+1, j+1);
        } else if(expr.charAt(i) == str.charAt(j)){
            return match(expr, str, i+1, j+1);
        } else return false;
    }

    public static boolean isMatch(String pattern, String str){
        return isMatch(pattern, str, 0, 0);
    }

    private static boolean isMatch(String pattern, String str, int i, int j){
        if(pattern.charAt(i) == str.charAt(j)){
            i++;
            j++;
            if(i >= pattern.length() && j >= str.length())  return true;
            if(i < pattern.length() && j < str.length())
                return isMatch(pattern, str, i, j);
            else return false;
        } else {
            if(pattern.charAt(i) == '*'){
                if(++i >= pattern.length()) return true;
                boolean isMatch = false;
                while(++j < str.length() && !isMatch) {
                    isMatch = isMatch(pattern, str, i, j);
                };
                return isMatch;
            } else {
                return false;
            }
        }
    }

}
