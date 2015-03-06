package com.interview.books.leetcodeoj;

import java.util.HashMap;

/**
 * Created_By: stefanie
 * Date: 14-12-27
 * Time: 上午9:56
 */
public class LOJ128_LongestConsecutiveSequence {
    //use HashMap to hold all the num and mark if the num is visited
    //scan num, grow to smaller and larger to get the longest consecutive sequence.
    public int longestConsecutive(int[] num) {
        if(num.length <= 1) return num.length;

        HashMap<Integer, Boolean> map = new HashMap();
        for(int i = 0; i < num.length; i++) map.put(num[i], false);
        int max = 0;
        for(int i = 0; i < num.length; i++){
            if(map.get(num[i])) continue;
            int length = 1;
            int smaller = num[i] - 1;
            while(map.containsKey(smaller)){
                map.put(smaller, true);
                smaller--;
                length++;
            }
            int larger = num[i] + 1;
            while(map.containsKey(larger)){
                map.put(larger, true);
                larger++;
                length++;
            }
            max = Math.max(max, length);
        }
        return max;
    }
}
