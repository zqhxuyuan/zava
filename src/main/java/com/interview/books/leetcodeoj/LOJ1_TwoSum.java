package com.interview.books.leetcodeoj;

import java.util.HashMap;

/**
 * Created_By: stefanie
 * Date: 14-12-18
 * Time: 上午8:03
 */
public class LOJ1_TwoSum {

    //index start from 1
    //new int[0] and new int[]{.....}
    //map.containsKey(key);
    public int[] twoSum(int[] numbers, int target) {
        HashMap<Integer, Integer> map = new HashMap();
        for(int i = 0; i < numbers.length; i++){
            int required = target - numbers[i];
            if(map.containsKey(required)){
                return new int[]{map.get(required), i + 1};
            } else {
                map.put(numbers[i], i + 1);
            }
        }
        return new int[0];
    }
}
