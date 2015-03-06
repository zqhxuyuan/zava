package com.interview.books.leetcodeoj;

import java.util.Set;

/**
 * Created_By: stefanie
 * Date: 14-12-27
 * Time: 下午4:37
 */
public class LOJ139_WordBreak {
    //state: canSegment[i] == true when s.substring(0, i) can be segmented.
    //initialize: canSegment[0] = true;
    //function: canSegment[i] == true when found j (0, i-1) s.substring(j, i) is a word and canSegment[j] == true
    //result: canSegment[s.length()]
    //optimization: find the max length of word in dict, and adjust j based on maxLength(i - j <= maxLength);
    public boolean wordBreak(String s, Set<String> dict) {
        if(s == null || s.length() == 0) return false;
        boolean[] canSegment = new boolean[s.length() + 1];
        int maxLength = getMaxLength(dict);
        canSegment[0] = true;
        for(int i = 1; i <= s.length(); i++){
            for(int j = i - 1; j >= 0 && i - j <= maxLength; j--){
                if(canSegment[j] && dict.contains(s.substring(j, i))){
                    canSegment[i] = true;
                    break;
                }
            }
        }
        return canSegment[s.length()];
    }

    private int getMaxLength(Set<String> dict){
        int max = 0;
        for(String word : dict) max = Math.max(max, word.length());
        return max;
    }
}
