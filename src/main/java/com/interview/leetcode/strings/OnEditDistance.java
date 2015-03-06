package com.interview.leetcode.strings;

/**
 * Created_By: stefanie
 * Date: 14-12-2
 * Time: 上午11:43
 *
 * Given two strings S and T, determine if they are both one edit distance apart.
 * S: abc     T: adc return true
 * S: abcd    T: acd return true
 * S: abcd    T: acc return false
 * Time: O(N), Space: O(1)
 */
public class OnEditDistance {

    public boolean isOneEditDistance(String s, String t) {
        int m = s.length(), n = t.length();
        if (m < n) return isOneEditDistance(t, s);  //ensure s is longer than t
        if (m - n > 1) return false;
        int offset = 0, shift = m - n;
        while (offset < n && s.charAt(offset) == t.charAt(offset)) offset++;
        if (offset == n) return shift > 0;
        if (shift == 0) offset++;
        while (offset < n && s.charAt(offset + shift) == t.charAt(offset)) offset++;
        return offset == n;
    }
}
