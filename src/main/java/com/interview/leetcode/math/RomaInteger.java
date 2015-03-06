package com.interview.leetcode.math;

import java.util.HashMap;

/**
 * Created_By: stefanie
 * Date: 14-11-26
 * Time: 下午1:08
 */
public class RomaInteger {
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

    static int[] weights = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
    static String[] tokens = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

    public static int roma2int(String roma) {
        int sum = 0;
        for (int i = 0; i < roma.length(); i++) {
            if (i < roma.length() - 1 && map.get(roma.charAt(i)) < map.get(roma.charAt(i + 1)))
                sum -= map.get(roma.charAt(i));
            else
                sum += map.get(roma.charAt(i));
        }
        return sum;
    }

    public static String int2rome(int num) {
        StringBuilder builder = new StringBuilder();
        int start = 0;
        while (num > 0) {
            for (int i = start; i < 13; i++) {
                if (num >= weights[i]) {
                    num -= weights[i];
                    builder.append(tokens[i]);
                    break;
                }
                start = i + 1; // skip those impossible check, make it faster
            }
        }
        return builder.toString();
    }
}
