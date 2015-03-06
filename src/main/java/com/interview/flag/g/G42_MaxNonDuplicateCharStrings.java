package com.interview.flag.g;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by stefanie on 1/27/15.
 */
public class G42_MaxNonDuplicateCharStrings {
    
    public String find(String[] words){
        Comparator<String> comparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.length() - o2.length();
            }
        };
        Arrays.sort(words, comparator);

        int[] identities = new int[words.length];
        for(int i = 0; i < words.length; i++){
            int mark = 0;
            String word = words[i].toLowerCase();
            for(int j = 0; j < word.length(); j++) mark |= (1 << (word.charAt(j) - 'a'));
            identities[i] = mark;
        }

        for(int i = words.length - 2; i >= 0; i--){
            for(int j = words.length - 1; j > i; j--){
                if((identities[i] & identities[j]) == 0) return words[i] + "," + words[j];
            }
        }
        return "";
    }
    
    public static void main(String[] args){
        G42_MaxNonDuplicateCharStrings finder = new G42_MaxNonDuplicateCharStrings();
        String[] words = new String[]{"you","like","work","I","right","place","facebook"};
        System.out.println(finder.find(words));
    }
}
