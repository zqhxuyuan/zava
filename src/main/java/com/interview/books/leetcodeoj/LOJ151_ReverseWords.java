package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-28
 * Time: 下午4:18
 */
public class LOJ151_ReverseWords {
    //scan s backward, tracking the end of word and find the begin
    //if s.charAt(i) == ' ', update end to i
    //begin is i == 0 || s.charAt(i - 1) == ' ', buffer.append(s.substring(i, end));
    public static String reverseWords(String s) {
        if(s == null) return null;
        StringBuffer buffer = new StringBuffer();
        int end = s.length();
        for(int i = s.length() - 1; i >= 0; i--){
            if(s.charAt(i) == ' ') end = i;
            else if(i == 0 || s.charAt(i - 1) == ' '){
                if(buffer.length() > 0) buffer.append(' ');
                buffer.append(s.substring(i, end));
            }
        }
        return buffer.toString();
    }
}
