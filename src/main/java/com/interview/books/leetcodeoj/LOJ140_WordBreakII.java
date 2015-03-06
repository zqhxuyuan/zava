package com.interview.books.leetcodeoj;

import java.util.*;

/**
 * Created_By: stefanie
 * Date: 14-12-27
 * Time: 下午5:01
 */
public class LOJ140_WordBreakII {
    //backtracing to get all break solution, using memo to avoid duplication segmentation
    //generate segments for s, by partition it into word,
    //  1. if word is the end of s
    //  2. if word is not end of s, get segments of rest, for each rest add word in front of it as a solution
    //put in memo before return.
    public List<String> wordBreak(String s, Set<String> dict) {
        if(s == null || s.length() == 0) return new ArrayList();
        int maxLen = getMaxLength(dict);
        Map<String, List<String>> memo = new HashMap();
        return wordBreak(s, maxLen, dict, memo);
    }

    public List<String> wordBreak(String s, int maxLen, Set<String> dict, Map<String, List<String>> memo){
        if(memo.containsKey(s)) return memo.get(s);
        List<String> segments = new ArrayList();
        for(int len = 1; len <= maxLen && len <= s.length(); len++){
            String word = s.substring(0, len);
            if(dict.contains(word)){
                if(len == s.length()){
                    segments.add(word);
                } else {
                    List<String> rests = wordBreak(s.substring(len), maxLen, dict, memo);
                    for(String rest : rests) segments.add(word + " " + rest);
                }
            }
        }
        memo.put(s, segments);
        return segments;
    }

    private int getMaxLength(Set<String> dict){
        int max = 0;
        for(String word : dict) max = Math.max(max, word.length());
        return max;
    }
}
