package com.interview.leetcode.dp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created_By: stefanie
 * Date: 14-11-25
 * Time: 上午10:13
 */
public class MemoDP {

    /**
     * Given a word dict, and a sens, find the ways to segment given sens
     * Memo: HashMap<substring, segment ways>
     */
    static class WordBreak {
        public static List<String> wordBreak(String s, Set<String> dict) {
            HashMap<String, List<String>> memo = new HashMap<>();
            int maxLen = maxLength(dict);
            return wordBreak(s, dict, maxLen, memo);
        }
        public static List<String> wordBreak(String s, Set<String> dict, int maxLen, HashMap<String, List<String>> memo){
            if(memo.containsKey(s)) return memo.get(s);
            List<String> sols = new ArrayList<>();
            for(int len = 1; len <= maxLen && len <= s.length(); len++){
                String word = s.substring(0, len);
                if(dict.contains(word)){
                    if(len == s.length()){
                        sols.add(word);
                    } else {
                        List<String> segments = wordBreak(s.substring(len, s.length()), dict, maxLen, memo);
                        for(String segment : segments) sols.add(word + " " + segment);
                    }
                }
            }
            memo.put(s, sols);
            return sols;
        }
        public static int maxLength(Set<String> dict) {
            int length = 0;
            for (String word : dict) length = Math.max(length, word.length());
            return length;
        }
    }
}
