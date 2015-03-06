package com.interview.basics.model.trie;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/21/14
 * Time: 4:52 PM
 */
class ChineseTrieNode{
    Map<Character, ChineseTrieNode> children = new HashMap<Character, ChineseTrieNode>();
    boolean isWord = false;

    ChineseTrieNode(boolean isWord){
        this.isWord = isWord;
    }
}

public class ChineseTrie {
    ChineseTrieNode root = new ChineseTrieNode(false);

    public ChineseTrie(String path) throws IOException {
        FileInputStream f = new FileInputStream(path);
        BufferedReader dr=new BufferedReader(new InputStreamReader(f, "UTF-8"));
        String line = dr.readLine();
        while(line != null) {
            addAWord(line.trim());
            line = dr.readLine();
        }
    }

    private void addAWord(String word){
        ChineseTrieNode current = root;
        for(int i = 0; i < word.length(); i++){
            ChineseTrieNode node = current.children.get(word.charAt(i));
            if(node == null) {
                node = new ChineseTrieNode(false);
                current.children.put(word.charAt(i), node);
            }
            if(i == word.length() - 1) node.isWord = true;
            current = node;
        }
    }

    public boolean isWord(String word){
        if(word.length() == 0) return false;
        ChineseTrieNode node = root;
        for(char ch : word.toCharArray()){
            node = node.children.get(ch);
            if(node == null) return false;
        }
        return node.isWord;
    }

    public boolean partialMatch(String word, boolean partial){
        if(word.length() == 0) return false;
        ChineseTrieNode node = root;
        for(char ch : word.toCharArray()){
            node = node.children.get(ch);
            if(node == null) return false;
        }
        return partial || node.isWord;
    }
}