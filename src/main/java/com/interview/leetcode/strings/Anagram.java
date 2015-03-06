package com.interview.leetcode.strings;

import com.interview.leetcode.math.Numbers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-26
 * Time: 下午11:14
 */
public class Anagram {

    static int[] primes = Numbers.generatePrim(26);
    public List<String> anagrams(String[] strs) {
        ArrayList<String> result = new ArrayList<String>();
        HashMap<Integer, ArrayList<String>> map = new HashMap<Integer, ArrayList<String>>();
        for(String s : strs){
            int identity = getIdentity(s);
            ArrayList<String> lists = map.get(identity);
            if(lists == null){
                lists = new ArrayList<String>();
                map.put(identity, lists);
            }
            lists.add(s);
        }

        for(ArrayList<String> lists : map.values()){
            if(lists.size() > 1){
                result.addAll(lists);
            }
        }
        return result;
    }

    public int getIdentity(String s){ //get a identity of the string based on prime number product
        int identity = 1;
        for(char ch : s.toCharArray()) identity *= primes[ch - 'a'];
        return identity;
    }

}
