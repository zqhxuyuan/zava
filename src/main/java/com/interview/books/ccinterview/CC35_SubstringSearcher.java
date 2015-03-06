package com.interview.books.ccinterview;


import com.interview.utils.ConsoleWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-14
 * Time: 下午2:25
 */
public class CC35_SubstringSearcher {
    class SuffixTreeNode {
        char value;
        HashMap<Character, SuffixTreeNode> children = new HashMap<>();
        List<Integer> indexes = new ArrayList<>();

        public SuffixTreeNode(char ch) {
            this.value = ch;
        }

        public void insertString(String s, int index) {
            if (s == null || s.length() <= 0) return;

            char first = s.charAt(0);
            SuffixTreeNode child = children.get(first);
            if (child == null) {
                child = new SuffixTreeNode(first);
                children.put(first, child);
            }
            String reminder = s.substring(1);
            child.indexes.add(index);
            child.insertString(reminder, index);
        }

        public List<Integer> search(String s) {
            if (s == null || s.length() == 0) return indexes;
            char first = s.charAt(0);
            if (children.containsKey(first)) {
                String reminder = s.substring(1);
                return children.get(first).search(reminder);
            }
            return new ArrayList<>();
        }

    }

    SuffixTreeNode root = new SuffixTreeNode('#');

    public CC35_SubstringSearcher(String s) {
        for (int i = 0; i < s.length(); i++) {
            String suffix = s.substring(i);
            root.insertString(suffix, i);
        }
    }

    public List<Integer> search(String s) {
        return root.search(s);
    }

    public static void main(String[] args) {
        CC35_SubstringSearcher searcher = new CC35_SubstringSearcher("abcdabdefcd");
        String[] T = new String[]{"ab", "abc", "def", "cd", "d"};
        for (String t : T) {
            System.out.print(t + ": ");
            ConsoleWriter.printCollection(searcher.search(t));
        }
    }
}
