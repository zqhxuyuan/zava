package com.interview.books.ccinterview;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created_By: stefanie
 * Date: 14-12-14
 * Time: 下午2:00
 */
public class CC34_LongestWordMadeOfOthers {
    public String find(String[] words){
        Comparator<String> comparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.length() - o1.length();
            }
        };
        Arrays.sort(words, comparator);

        HashMap<String, Boolean> memo = new HashMap<>();
        for(String word : words) memo.put(word, true);

        for(String word : words){
            if(canBuildWord(word, true, memo)){
                return word;
            }
        }
        return "";
    }

    private boolean canBuildWord(String word, boolean isOriginal, HashMap<String, Boolean> memo) {
        if(memo.containsKey(word) && !isOriginal) return memo.get(word);
        boolean canBuilt = false;
        for(int i = 1; i < word.length(); i++){
            String left = word.substring(0, i);
            String right = word.substring(i);
            if(memo.containsKey(left) && memo.get(left) == true && canBuildWord(right, false, memo)){
                canBuilt = true;
                break;
            }
        }
        memo.put(word, canBuilt);
        return canBuilt;
    }

    public static void main(String[] args){
        CC34_LongestWordMadeOfOthers finder = new CC34_LongestWordMadeOfOthers();
        String[] words = new String[]{"banana", "dog", "nana", "walk", "ba", "walker", "dogwalkers"};
        System.out.println(finder.find(words));
    }
}
