package com.interview.leetcode.strings;

import java.util.Arrays;

/**
 * Created_By: stefanie
 * Date: 14-11-18
 * Time: 下午5:40
 */
public class LongestSubString {

    /**
     * longest substring have no duplicate char
     */
    public static int nonDuplicate(String s) {
        int max = 0;
        int start = 0;
        int[] marker = new int[256];
        Arrays.fill(marker, -1);
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (marker[ch] >= start) {
                if (i - start > max) max = i - start;
                start = marker[ch] + 1;
            }
            marker[ch] = i;
        }
        if (s.length() - start > max) max = s.length() - start;
        return max;
    }

    /**
     * Longest Substring with At Most Two Distinct Characters
     *
     * The O(N) solution
     * front point to the first char, back point to the second char in backwards which (back + 1 ~ k - 1 is the same char)
     *
     * so when found a k != k - 1(back is assigned) and k != back, find 3rd char
     *      len = k - front
     *    next iteration: front = back + 1;
     *                    back = k - 1;
     */
    public static int twoDistinctChar(String s){
        int max = 0;
        int front = 0; int back = -1;
        for(int k = 1; k < s.length(); k++){
            if(s.charAt(k) == s.charAt(k - 1)) continue;
            if(back >= 0 && s.charAt(k) != s.charAt(back)){
                max = Math.max(max, k - front);
                front = back + 1;
            }
            back = k - 1;
        }
        return Math.max(s.length() - front, max);
    }

    /**
     * Longest Substring with At Most K Distinct Characters
     */
    public static int kDistinctChar(String s, int k){
        int max = 0; int front = 0; int total = 0;
        int[] counter = new int[256];
        for(int i = 0; i < s.length(); i++){
            if(counter[s.charAt(i)] == 0) total++;
            counter[s.charAt(i)]++;
            if(total > k){  //need shrink begin
                counter[s.charAt(front)]--;
                if(counter[s.charAt(front)] == 0) total--;
                front++;
            }
            max = Math.max(max, i - front + 1);
        }
        return max;
    }


}
