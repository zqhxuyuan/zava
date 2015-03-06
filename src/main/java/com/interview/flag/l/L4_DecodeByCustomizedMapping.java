package com.interview.flag.l;

import java.util.HashMap;

/**
 * Created_By: stefanie
 * Date: 15-1-8
 * Time: 下午3:42
 */
public class L4_DecodeByCustomizedMapping {
    public int decodeWays(String number, HashMap<Character, String> mapping){
        HashMap<String, Integer> memo = new HashMap();
        int maxLen = 0;
        for(String str : mapping.values()) {
            maxLen = Math.max(maxLen, str.length());
            updateMemo(memo, str);
        }

        int[] ways = new int[number.length() + 1];
        ways[0] = 1;
        for(int i = 1; i <= number.length(); i++){
            for(int j = i - maxLen < 0? 0 : i - maxLen; j < i; j++){
                String substr = number.substring(j, i);
                if(memo.containsKey(substr)){
                    ways[i] += ways[j] * memo.get(substr);
                }
            }
        }
        return ways[number.length()];
    }

    public void updateMemo(HashMap<String, Integer> map, String key){
        if(!map.containsKey(key)) map.put(key, 1);
        else map.put(key, map.get(key) + 1);
    }

    public static void main(String[] args){
        L4_DecodeByCustomizedMapping decoder = new L4_DecodeByCustomizedMapping();
        HashMap<Character, String> mapping = new HashMap();
        mapping.put('a', "21");
        mapping.put('b', "2");
        mapping.put('c', "54");
        mapping.put('d', "5");
        mapping.put('e', "4");
        mapping.put('f', "1");
        //a - 21 b - 2 c - 54 d - 5 e -4 f-1.
        System.out.println(decoder.decodeWays("2154", mapping));
    }
}
