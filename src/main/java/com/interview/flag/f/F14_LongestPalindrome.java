package com.interview.flag.f;

import java.util.*;

/**
 * Created_By: stefanie
 * Date: 15-2-2
 * Time: 下午3:07
 */
public class F14_LongestPalindrome {
    public String getLongestPalindrome(HashSet<String> dictData) {
        // we need to process the words from longest to shortest based on length.
        // e.g. for “hhijk” and “kji”, if “kji” is processed first then it will be deleted and “hhijk” won’t find a match.
        Comparator<String> comparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if(o2.length() != o1.length()) return o2.length() - o1.length();
                else return o2.compareTo(o1);
            }
        };

        TreeSet<String> dict = new TreeSet(comparator);
        dict.addAll(dictData);

        HashMap<String, String> palindromes = new HashMap();  // left word -> right word

        String centerPalindrome = "";

        while(! dict.isEmpty()) {
            String word = dict.iterator().next();
            String reversedWord = this.reverse(word);
            // direct reversing match is the longest match for current word
            if(dict.contains(reversedWord) && ! reversedWord.equals(word)) {
                palindromes.put(word, reversedWord);
                dict.remove(word);
                dict.remove(reversedWord);
            } else {
                /*
                try to search palindrome solution with part of the word. E.g. "dcb" is a match for "abcd".
                it's possible the word is like "aabcdd", so we need to two scans from each side to find
                possible palindrome solution. for the partly matched palindrome pair solution,
                it can only be placed in the center of final string.
                */

                String matched = null;
                // scan from left to right
                for(int i = 0; i < word.length() - 1; i ++) {
                    if(i == 0 || word.charAt(i) == word.charAt(i-1)) {
                        String subWord = word.substring(i + 1);
                        String reversedSubWord = this.reverse(subWord);
                        if(dict.contains(reversedSubWord)) {
                            int length = word.length() + reversedWord.length();
                            if(centerPalindrome.length() < length) {
                                centerPalindrome = reversedSubWord + word;
                                matched = reversedSubWord;
                            }
                            break; // following solutions must be shorter than current one.
                        }
                    }
                }

                // scan from right to left
                for(int i = word.length() - 1; i > 0; i --) {
                    if(i == word.length() - 1 || word.charAt(i) == word.charAt(i+1)) {
                        String subWord = word.substring(0, i);
                        String reversedSubWord = this.reverse(subWord);
                        if(dict.contains(reversedSubWord)) {
                            int length = word.length() + reversedSubWord.length();
                            if(centerPalindrome.length() < length) {
                                centerPalindrome = word + reversedSubWord;
                                matched = reversedSubWord;
                            }
                            break;
                        }
                    }
                }

                if(matched == null && isPalindrome(word)) {
                    // check whether the word itself is a palindrome
                    // this is the shortest possible palindrome substring with current word included
                    if(centerPalindrome.length() < word.length())
                        centerPalindrome= word;
                }

                if(matched != null) dict.remove(matched);
                dict.remove(word);
            }
        }

        StringBuilder builder = new StringBuilder(centerPalindrome);
        for(Map.Entry<String, String> entry : palindromes.entrySet()) {
            builder.insert(0, entry.getKey());
            builder.append(entry.getValue());
        }

        return builder.toString();
    }

    private boolean isPalindrome(String s) {
        for(int i = 0, j = s.length() - 1; i < j; i ++, j --) {
            if(s.charAt(i) != s.charAt(j)) return false;
        }
        return true;
    }

    private String reverse(String word) {
        StringBuilder builder = new StringBuilder();
        for(int i = word.length() - 1; i >= 0; i --) builder.append(word.charAt(i));
        return builder.toString();
    }

    public static void main(String[] args) {
        HashSet<String> dict = new HashSet();
        dict.add("acbca");
        dict.add("xxyy");
        dict.add("yyxx");
        dict.add("hhijk");
        dict.add("kji");
        F14_LongestPalindrome sol = new F14_LongestPalindrome();
        System.out.println(sol.getLongestPalindrome(dict));
    }
}
