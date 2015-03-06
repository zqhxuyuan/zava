package com.interview.flag.g;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 15-1-1
 * Time: 下午7:39
 */
public class G16_Abbreviation {

    public String abbreviation(String target, List<String> dict){
        if(dict == null || dict.size() == 0) return target.length() + "";
        for(int k = 0; k < target.length(); k++){
            List<String> abbrs = KAbbreviation(target, k, dict);
            if(abbrs.size() == 0) continue;
            String shortest = abbrs.get(0);
            for(String abbr : abbrs){
                if(abbr.length() < shortest.length()) shortest = abbr;
            }
            return shortest;
        }
        return target;
    }

    public List<String> KAbbreviation(String target, int K, List<String> dict){
        List<String> abbrs = new ArrayList();
        boolean[] current = new boolean[target.length()];  //true if the char stays, false to encode it to number.
        KAbbreviation(target, current, K, dict, abbrs);
        return abbrs;
    }

    public void KAbbreviation(String target, boolean[] current, int K, List<String> dict, List<String> abbrs){
        if(K == 0){
            StringBuffer buffer = new StringBuffer();
            int prev = 0;
            for(int i = 0; i < current.length; i++){
                if(!current[i]) prev++;
                else {
                    if(prev != 0) buffer.append(prev);
                    prev = 0;
                    buffer.append(target.charAt(i));
                }
            }
            if(prev != 0) buffer.append(prev);
            String abbr = buffer.toString();
            for(String word : dict){
                if(isMatch(word, abbr)) return;
            }
            abbrs.add(abbr);
        } else {
            for(int i = 0; i < target.length(); i++){    //permutation which char will stay
                current[i] = true;
                KAbbreviation(target, current, K - 1, dict, abbrs);
                current[i] = false;
            }
        }
    }

    public boolean isMatch(String word, String abbr){
        int i = 0, j = 0;
        while(i < word.length() && j < abbr.length()){
            if(Character.isDigit(abbr.charAt(j))){
                int shift = Character.getNumericValue(abbr.charAt(j++));
                while(j < abbr.length() && Character.isDigit(abbr.charAt(j))){
                    shift = shift * 10 + Character.getNumericValue(abbr.charAt(j++));
                }
                i += shift;
            } else {
                if(word.charAt(i++) != abbr.charAt(j++)) return false;
            }
        }
        return i == word.length() && j == abbr.length();
    }

    public static void main(String[] args){
        G16_Abbreviation finder = new G16_Abbreviation();
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
        System.out.println(finder.isMatch("apple", abbr));
        for(int i = 0; i < words.size(); i++){
            System.out.println(finder.isMatch(words.get(i), abbr));
        }
    }
}
