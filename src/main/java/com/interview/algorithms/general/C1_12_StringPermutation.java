package com.interview.algorithms.general;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenting on 2014/7/1.
 */
public class C1_12_StringPermutation {
    public static List<String> permutation(String str){
        List<String> permutations = new ArrayList<String>();
        List<String> options = new ArrayList<String>();
        options.add("");

        char[] chars = str.toCharArray();
        for(int i = 0; i < chars.length; i++){
            for(String item : options){
                for(int j = 0; j <= item.length(); j++){
                    String newstr = addCharAt(item, j, chars[i]);
                    permutations.add(newstr);
                }
            }
            options.clear();
            options.addAll(permutations);
            permutations.clear();
        }
        return options;
    }

    private static String addCharAt(String str, int index, char c){
        return str.substring(0, index) + c + str.substring(index);
    }
}
