package com.interview.algorithms.string;

import java.util.HashMap;
import java.util.Map;

/**
 * Created_By: stefanie
 * Date: 14-10-5
 * Time: 下午10:16
 *
 * Given a string s and an array of smaller string T, design a method to search s for each small string in T
 *
 * Solution:
 *  1. scan all the String[] T, create a trie tree    O(T total length L)
 *  2. scan String s, for each offset find matchChar in trie. O(N)
 *
 */
public class C11_5A_StringScaner {
    static class TrieNode{
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isWord = false;
        int index = -1;

        public static void addWord(TrieNode node, String t, int index){
            char[] chars = t.toCharArray();
            for(int i = 0; i < chars.length; i++){
                TrieNode ch = node.children.get(chars[i]);
                if(ch == null){
                    ch = new TrieNode();
                    node.children.put(chars[i], ch);
                }
                node = ch;
            }
            node.isWord = true;
            node.index = index;
        }
    }
    public static int[] find(String s, String[] T){
        TrieNode root = new TrieNode();
        for(int i = 0; i < T.length; i++) TrieNode.addWord(root, T[i], i);

        char[] chars = s.toCharArray();
        int[] offset = new int[chars.length];
        for(int i = 0; i < chars.length; i++){
            offset[i] = exist(root.children.get(chars[i]), chars, i);
        }
        return offset;
    }

    public static int[] offset(String s, String[] T){
        TrieNode root = new TrieNode();
        for(int i = 0; i < T.length; i++) TrieNode.addWord(root, T[i], i);

        char[] chars = s.toCharArray();
        int[] offset = new int[T.length];
        for(int i = 0; i < offset.length; i++) offset[i] = -1;
        for(int i = 0; i < chars.length; i++){
            int index = find(root.children.get(chars[i]), chars, i);
            if(index != -1) offset[index] = i;
        }
        return offset;
    }

    private static int find(TrieNode node, char[] chars, int offset){
        while(node != null && !node.isWord){
            node = node.children.get(chars[++offset]);
        }
        if(node == null) return -1;
        else return node.index;
    }

    private static int exist(TrieNode node, char[] chars, int offset){
        int length = 1;
        while(node != null && !node.isWord){
            length++;
            node = node.children.get(chars[++offset]);
        }
        if(node == null) return -1;
        else return length;
    }
}
