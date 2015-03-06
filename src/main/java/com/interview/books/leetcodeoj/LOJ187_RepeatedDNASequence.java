package com.interview.books.leetcodeoj;

import com.interview.utils.ConsoleWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 15-2-8
 * Time: 下午7:37
 */
public class LOJ187_RepeatedDNASequence {
    //use Integer to present char sequence, 00 for A, 01 for C, 10 for G, 11 for T, need 20 bit. (Integer)
    //scan string, generate new key by ((prev << 2) & 0x000FFFFF) + map.get(current);
    //count using HashMap
    static HashMap<Character, Integer> map = new HashMap();
    static {
        map.put('A', 0);
        map.put('C', 1);
        map.put('G', 2);
        map.put('T', 3);
    }
    public List<String> findRepeatedDnaSequences(String s) {
        List<String> repeated = new ArrayList();
        if(s == null || s.length() <= 10) return repeated;
        HashMap<Integer, Integer> sequences = new HashMap();
        Integer key = 0;
        for(int i = 0; i < s.length(); i++){
            key = getKey(key, s.charAt(i));
            if(i < 9) continue;
            int count = sequences.containsKey(key)? sequences.get(key) : 0;
            if(count == 1) repeated.add(s.substring(i - 9, i + 1));
            sequences.put(key, count + 1);
        }
        return repeated;
    }

    public Integer getKey(Integer prev, char current){
        return ((prev << 2) & 0x000FFFFF) + map.get(current);
    }

    public static void main(String[] args){
        LOJ187_RepeatedDNASequence finder = new LOJ187_RepeatedDNASequence();
        List<String> repeated = finder.findRepeatedDnaSequences("CCGGCCGGCCGGCC");
        ConsoleWriter.printCollection(repeated);
    }
}
