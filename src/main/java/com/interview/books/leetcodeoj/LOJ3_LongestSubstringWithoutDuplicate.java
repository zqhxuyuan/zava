package com.interview.books.leetcodeoj;

import java.util.Arrays;

/**
 * Created_By: stefanie
 * Date: 14-12-18
 * Time: 上午10:16
 */
public class LOJ3_LongestSubstringWithoutDuplicate {
    //mark the prev offset of a given char, and tracking the max length without duplicate chars.
    //1.fill indexes with -1;
    //2.no dup when indexes[s.charAt(i)] < start
    //3.update maxLen everytime if no dup: maxLen = Math.max(maxLen, i - start + 1);
    public int lengthOfLongestSubstring(String s) {
        int maxLen = 0;
        int start = 0;
        int[] indexes = new int[256];
        Arrays.fill(indexes, -1);
        for(int i = 0; i < s.length(); i++){
            if(indexes[s.charAt(i)] == -1 || indexes[s.charAt(i)] < start){
                maxLen = Math.max(maxLen, i - start + 1);
            } else {//find a duplication
                start = indexes[s.charAt(i)] + 1;
            }
            indexes[s.charAt(i)] = i;
        }
        return maxLen;
    }
}
