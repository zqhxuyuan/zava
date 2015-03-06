package com.interview.books.leetcodeoj;

import java.util.HashMap;

/**
 * Created_By: stefanie
 * Date: 14-12-18
 * Time: 下午2:37
 */
public class LOJ13_RomeToInteger {
    static HashMap<Character, Integer> map = new HashMap<Character, Integer>();
    static {
        map.put('M', 1000);
        map.put('D', 500);
        map.put('C', 100);
        map.put('L', 50);
        map.put('X', 10);
        map.put('V', 5);
        map.put('I', 1);
    }
    public int romanToInt(String roma) {
        int sum = 0;
        for (int i = 0; i < roma.length(); i++) {
            if (i < roma.length() - 1 && map.get(roma.charAt(i)) < map.get(roma.charAt(i + 1)))
                sum -= map.get(roma.charAt(i));
            else
                sum += map.get(roma.charAt(i));
        }
        return sum;
    }
}
