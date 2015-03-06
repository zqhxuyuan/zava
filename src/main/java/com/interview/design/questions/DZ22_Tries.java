package com.interview.design.questions;

import com.interview.utils.ConsoleWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created_By: stefanie
 * Date: 15-1-14
 * Time: 上午9:34
 */
public class DZ22_Tries {
    //Space: O(26^L) L is the average length of words
    class TrieNode{
        boolean isWord = false;
        HashMap<Character, TrieNode> children = new HashMap();
    }
    TrieNode root = new TrieNode();

    //Time: O(L)    L is the length of the word
    public void add(String word){
        TrieNode current = root;
        for(int i = 0; i < word.length(); i++){
            char ch = word.charAt(i);
            if(current.children.containsKey(ch)){
                current = current.children.get(ch);
            } else {
                TrieNode node = new TrieNode();
                current.children.put(ch, node);
                current = node;
            }
        }
        current.isWord = true;
    }

    //Time: O(L)
    private TrieNode getNode(String str){
        TrieNode current = root;
        for(int i = 0; i < str.length(); i++){
            char ch = str.charAt(i);
            if(! current.children.containsKey(ch)) return null;
            current = current.children.get(ch);
        }
        return current;
    }

    //Time: O(L)
    public boolean isWord(String word){
        TrieNode node = getNode(word);
        return node != null && node.isWord;
    }

    //Time: O(L)
    public boolean isPrefix(String prefix){
        TrieNode node = getNode(prefix);
        return node != null;
    }
    //Time: O(M), M words having this prefix. M < 26^(the max length - prefix length)
    public List<String> words(String prefix){
        List<String> words = new ArrayList();
        TrieNode node = getNode(prefix);
        visit(node, prefix, words);
        return words;
    }

    private void visit(TrieNode node, String prefix, List<String> words){
        if(node == null) return;
        if(node.isWord) words.add(prefix);
        for(Map.Entry<Character, TrieNode> child : node.children.entrySet()){
            visit(child.getValue(), prefix + child.getKey(), words);
        }
    }

    public List<String> getFuzzyWords(String word, int missingLetters){
        List<String> words = new ArrayList();
        getFuzzyWords(root, word, "", missingLetters, words);
        return words;
    }

    private void getFuzzyWords(TrieNode node, String word, String prefix, int missingLetters, List<String> words){
        if(word.length() == 0) {
            if (node.isWord && !words.contains(prefix)) words.add(prefix);
            if (missingLetters > 0) {   //omit the following missingLetters chars in dict
                for (Map.Entry<Character, TrieNode> child : node.children.entrySet())
                    getFuzzyWords(child.getValue(), word, prefix + child.getKey(), missingLetters - 1, words);
            }
            return;
        }
        char ch = word.charAt(0);
        String suffix = word.substring(1);
        if(node.children.containsKey(ch)) getFuzzyWords(node.children.get(ch), suffix, prefix + ch, missingLetters, words);
        if(missingLetters > 0){
            getFuzzyWords(node, suffix, prefix, missingLetters - 1, words); //omit the first char in word
            for(Map.Entry<Character, TrieNode> child : node.children.entrySet()) //omit the first char in dict
                getFuzzyWords(child.getValue(), word, prefix + child.getKey(), missingLetters - 1, words);
        }
    }

    public static void main(String[] args){
        DZ22_Tries tries = new DZ22_Tries();
        tries.add("English");
        tries.add("French");
        tries.add("Frenchman");
        tries.add("Engineer");
        tries.add("Summer");

        System.out.println(tries.isWord("French"));  //true
        System.out.println(tries.isWord("Fre"));     //false
        System.out.println(tries.isWord("Frencha")); //false
        System.out.println(tries.isPrefix("Frech")); //false
        System.out.println(tries.isPrefix("Fre"));   //true
        List<String> words = tries.words("En");
        ConsoleWriter.printCollection(words);        //English, Engineer
        words = tries.words("Engl");
        ConsoleWriter.printCollection(words);        //English


        ConsoleWriter.printCollection(tries.getFuzzyWords("Frencha", 1));   //French
        ConsoleWriter.printCollection(tries.getFuzzyWords("Fresdncha", 5));   //French  Frenchman
        ConsoleWriter.printCollection(tries.getFuzzyWords("eEfnsglaissh", 5));    //English
        ConsoleWriter.printCollection(tries.getFuzzyWords("ng", 5));    //English
    }
}
