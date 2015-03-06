package com.interview.books.svinterview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created_By: stefanie
 * Date: 14-12-8
 * Time: 下午4:43
 */
public class SV15_ConvertWordByRules {
    public void convert(String s, HashMap<Character, ArrayList<Character>> rules){
        HashSet<String> sets = new HashSet<String>();
        sets.add(s);
        convert(s, 0, rules, "", sets);
    }

    public void convert(String s, int offset, HashMap<Character, ArrayList<Character>> rules, String prefix, HashSet<String> sets){
        if(offset == s.length()){
            if(!sets.contains(prefix)){
                System.out.println(prefix);
                sets.add(prefix);
            }
            return;
        }
        convert(s, offset + 1, rules, prefix + s.charAt(offset), sets); //preserve the old char

        ArrayList<Character> mutations = rules.get(s.charAt(offset));
        if(mutations != null){
            for(Character character : mutations){
                convert(s, offset + 1, rules, prefix + character, sets);
            }
        }
    }

    public static void main(String[] args){
        HashMap<Character, ArrayList<Character>> rules = new HashMap<>();
        ArrayList<Character> options = new ArrayList<>();
        options.add('@');
        rules.put('a', options);
        options = new ArrayList<>();
        options.add('3');
        options.add('E');
        rules.put('e', options);

        SV15_ConvertWordByRules converter = new SV15_ConvertWordByRules();
        converter.convert("face", rules);

    }
}
