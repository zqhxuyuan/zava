package com.interview.algorithms.string;

import com.interview.basics.model.trie.Trie;
import com.interview.basics.model.trie.TrieNode;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created_By: stefanie
 * Date: 14-10-27
 * Time: 下午10:28
 */
public class C11_35_LongestWordMadeOfOthers {
    public static String find(String[] words){
        Trie trie = new Trie();
        for(String word : words) trie.addWord(word);

        Arrays.sort(words, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if(o1.length() == o2.length()) return 0;
                else if(o1.length() > o2.length()) return -1;
                else return 1;
            }
        });

        for(String word : words){
            if(madeOfOther(word, 0, trie)) return word;
        }
        return null;

    }

    public static boolean madeOfOther(String word, int start, Trie trie){
        if(start == word.length()) return true;
        TrieNode node = trie.root;
        String found = "";
        while(true){
            while(node != null && !node.isWord() && start < word.length()){
                found += word.charAt(start);
                node = node.get(word.charAt(start++));
            }
            if(node != null && node.isWord() && madeOfOther(word, start, trie)) break;
            else if(node == null || start == word.length()) return false;
            found += word.charAt(start);
            node = node.get(word.charAt(start++));
        }
        if(!word.equals(found)) return true;
        else return false;
    }
}
