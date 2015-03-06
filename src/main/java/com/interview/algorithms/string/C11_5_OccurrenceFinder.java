package com.interview.algorithms.string;

import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

/**
 * Created_By: stefanie
 * Date: 14-7-6
 * Time: 下午7:22
 *
 * Given that you have one string of length N and M small strings of length L.
 * How do you efficiently find the occurrence of each small string in the larger one?
 *
 * Solution
 *      1. only one long string N, and several small string M, so build a index of N, store all the character occurrence location
 *      2. for every small string, find the location of the 1st char and check if it's following str is matched or not.
 *
 *      Time: O(N+M*L)  Space: O(N)
 */
public class C11_5_OccurrenceFinder {
    public static int[] find(String base, String[] list){
        Map<Character, List<Integer>> index = buildIndex(base);

        int[] result = new int[list.length];
        for(int i = 0; i < list.length; i++){
            String str = list[i];
            result[i] = find(index, base, str);
        }
        return result;
    }

    private static int find(Map<Character, List<Integer>> index, String base, String str){
        Character ch = str.charAt(0);
        for(Integer offset: index.get(ch)){
            int i = 1;
            int j = offset + 1;
            for(;i < str.length() && j < base.length() && base.charAt(j++) == str.charAt(i++);){}
            if(i == str.length()) return offset;
        }
        return -1;
    }

    private static Map<Character, List<Integer>> buildIndex(String base){
        Map<Character, List<Integer>> index = new HashMap<Character, List<Integer>>();
        for(int i = 0; i < base.length(); i++){
            Character ch = base.charAt(i);
            if(index.get(ch) == null){
                index.put(ch, new ArrayList<Integer>());
            }
            index.get(ch).add(i);
        }
        return index;
    }
}
