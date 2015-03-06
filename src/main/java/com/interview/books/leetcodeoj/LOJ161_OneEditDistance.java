package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-31
 * Time: 下午4:15
 */
public class LOJ161_OneEditDistance {
    //make sure s is the longer one
    //edge case: if s.length() - t.length() > 1, return false
    //go through t, offset = 0 and shift = m - n;
    //while(offset < n && s.charAt(offset) == t.charAt(offset)) offset++;
    //if offset == n, go util the end, check if m - n == 1;
    //if m == n, so both t and s need go one step forward, if m - n == 1, only s go one step forward
    //so if(shift == 0) offset++;
    //while(offset < n && s.charAt(offset + shift) == t.charAt(offset)) offset++;
    //return offset == n; scan till the end
    public boolean isOneEditDistance(String s, String t) {
        int m = s.length(); int n = t.length();
        if(m < n) return isOneEditDistance(t, s);
        if(m - n > 1) return false;
        int offset = 0, shift = m - n;
        while(offset < n && s.charAt(offset) == t.charAt(offset)) offset++;
        if(offset == n) return shift == 1;
        if(shift == 0) offset++;
        while(offset < n && s.charAt(offset + shift) == t.charAt(offset)) offset++;
        return offset == n;
    }
}
