package com.interview.flag.g;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created_By: stefanie
 * Date: 15-1-2
 * Time: 下午12:30
 */
public class G16_AbbreviationOptimized {

    public String abbreviation(String target, List<String> dict){
        if(dict == null || dict.size() == 0) return target.length() + "";
        //mark[i] is a set of words can be different by i-th char
        Set<Integer>[] mark = new Set[target.length()];
        for(int i = 0; i < target.length(); i++) mark[i] = new HashSet();
        for(int i = 0; i < dict.size(); i++){
            String word = dict.get(i);
            if(word.length() != target.length()) continue;
            for(int j = 0; j < word.length(); j++){
                if(word.charAt(j) != target.charAt(j)) mark[j].add(i);
            }
        }
        for(int k = 1; k < target.length(); k++){
            List<String> abbrs = KAbbreviation(target, mark, k, dict.size());
            if(abbrs.size() == 0) continue;
            String shortest = abbrs.get(0);
            for(String abbr : abbrs){
                if(abbr.length() < shortest.length()) shortest = abbr;
            }
            return shortest;
        }
        return target;
    }

    public List<String> KAbbreviation(String target, Set<Integer>[] mark, int K, int wordCount){
        List<String> abbrs = new ArrayList();
        boolean[] current = new boolean[target.length()];
        KAbbreviation(target, current, mark, K, wordCount, abbrs);
        return abbrs;
    }

    public void KAbbreviation(String target, boolean[] current, Set<Integer>[] mark, int K, int wordCount, List<String> abbrs){
        if(K == 0){
            Set<Integer> differents = new HashSet();
            StringBuffer buffer = new StringBuffer();
            int prev = 0;
            for(int i = 0; i < current.length; i++){
                if(!current[i]) prev++;
                else {
                    if(prev != 0) buffer.append(prev);
                    prev = 0;
                    buffer.append(target.charAt(i));
                    differents.addAll(mark[i]);
                }
            }
            if(prev != 0) buffer.append(prev);
            if(differents.size() == wordCount) abbrs.add(buffer.toString());
        } else {
            for(int i = 0; i < target.length(); i++){
                current[i] = true;
                KAbbreviation(target, current, mark, K - 1, wordCount, abbrs);
                current[i] = false;
            }
        }
    }

    public static void main(String[] args){
        G16_AbbreviationOptimized finder = new G16_AbbreviationOptimized();
        G16_Abbreviation checker = new G16_Abbreviation();
        //System.out.println(finder.isMatch("apple", "5"));
        //System.out.println(finder.isMatch("internationalization", "i5a11o1"));
        List<String> words = new ArrayList();
        //"plain”, “amber”, “blade”
        words.add("plain");
        words.add("amber");
        words.add("blade");
        words.add("spain");
        words.add("loped");
        words.add("hallo");
        words.add("aplan");
        String abbr = finder.abbreviation("apple", words);
        System.out.println(abbr);
        System.out.println(checker.isMatch("apple", abbr));
        for(int i = 0; i < words.size(); i++){
            System.out.println(checker.isMatch(words.get(i), abbr));
        }
    }
}
