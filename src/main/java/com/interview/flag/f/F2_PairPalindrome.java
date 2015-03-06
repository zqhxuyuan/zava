package com.interview.flag.f;

import java.util.Set;

/**
 * Created_By: stefanie
 * Date: 14-11-27
 * Time: 下午3:03
 */

/**
 * given a dict of words, find pair of words can concatenate to create a palindrome
 */
public class F2_PairPalindrome {

    public void findPairPalindrome(Set<String> dict) {
        for (String word : dict) {
            findPalindrome(word, dict);
        }
    }

    //for every word
    public void findPalindrome(String s, Set<String> dict) {
        //scan forward
        for (int i = 0; i < s.length(); i++) {
            String prefix = s.substring(0, i);
            if (isPalindrome(prefix)) {
                String target = reverse(s.substring(i));
                if (dict.contains(target)) System.out.println(target + " " + s);
            }
        }
        //scan backward
        for (int i = s.length() - 1; i >= 0; i--) {
            String suffix = s.substring(i);
            if (isPalindrome(suffix)) {
                String target = reverse(s.substring(0, i));
                if (dict.contains(target)) System.out.println(s + " " + target);
            }
        }
    }

    //check if s is a parlindrome
    public boolean isPalindrome(String s) {
        for (int i = 0, j = s.length() - 1; i < j; i++, j--) {
            if (s.charAt(i) != s.charAt(j)) return false;
        }
        return true;
    }

    //reverse a given string
    public String reverse(String s) {
        StringBuilder builder = new StringBuilder();
        for (int i = s.length() - 1; i >= 0; i--) builder.append(s.charAt(i));
        return builder.toString();
    }
}
