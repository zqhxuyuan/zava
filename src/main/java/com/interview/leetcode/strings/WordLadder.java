package com.interview.leetcode.strings;

import java.util.*;

/**
 * Created_By: stefanie
 * Date: 14-11-16
 * Time: 下午3:20
 *
 * Given two words (start and end), and a dictionary, find the length of shortest transformation sequence from start to end,
 * Only one letter can be changed at a time, and each intermediate word must exist in the dictionary
 * For example,
 * Given:   start = "hit"   end = "cog"  dict = ["hot","dotProduct","dog","lot","log"]
 * As one shortest transformation is "hit" -> "hot" -> "dotProduct" -> "dog" -> "cog",
 * return its length 5.
 *
 * WordLadderI is to return the min ladder number
 * WordLadderII is to return the path of all the min ladder solution
 *
 * Solution:
 *  1. both are use BSF on word graph
 *  2. WordLadderII use a prev link to store the path.
 */
public class WordLadder {

    static class WordLadderI {
        Set<String> dict;
        Set<String> visited;
        Queue<String> queue;
        String end;

        public int length(String start, String end, Set<String> dict) {
            if(start.equals(end)) return 1;
            //INIT
            this.dict = dict;
            this.end = end;
            visited = new HashSet<String>();
            queue = new LinkedList<String>();

            queue.offer(start);
            visited.add(start);

            int steps = 1;
            //DO BFS
            while(queue.size() > 0){
                int size = queue.size();
                for(int k = 0; k < size; k++){  //visit every layer
                    String word = queue.poll();
                    if(populate(word)) return steps + 1;
                }
                steps++;
            }
            return 0;
        }

        public boolean populate(String word){
            char[] chars = word.toCharArray();
            for(int i = 0; i < chars.length; i++){
                char orignal = chars[i];
                for(char ch = 'a'; ch <= 'z'; ch++){
                    if(ch == orignal) continue;
                    chars[i] = ch;
                    String next = String.valueOf(chars);
                    if(next.equals(end)) return true;
                    if(dict.contains(next) && !visited.contains(next)) {
                        queue.offer(next);
                        visited.add(next);
                    }
                }
                chars[i] = orignal;
            }
            return false;
        }
    }

    static class WordLadderII {
        class Node {
            int depth;
            String value;
            List<Node> prev = new ArrayList();
            Node(String value, int depth) { this.value = value; this.depth = depth; }
            public boolean equals(Object o) {
                if(o == null || ! (o instanceof Node)) return false;
                Node other = (Node) o;
                return this.value.equals(other.value);
            }
        }

        public List<List<String>> findLadders(String start, String end, Set<String> dict) {
            List<List<String>> result = new ArrayList();
            if(start == null || end == null || dict.isEmpty()) return result;
            int wordLength = start.length();
            Node startNode = new Node(start, 0);

            HashMap<String, Node> nodes = new HashMap();
            nodes.put(start, startNode);

            Queue<Node> queue = new LinkedList();
            queue.offer(startNode);

            while(! queue.isEmpty() && nodes.get(end) == null) {
                int levelNodeCount = queue.size();
                for(int index = 0; index < levelNodeCount; index ++) { // visit by level
                    Node current = queue.poll();
                    for(int i = 0; i < wordLength; i ++) {
                        StringBuilder buffer = new StringBuilder(current.value);
                        for(char ch = 'a'; ch <= 'z'; ch ++) {
                            buffer.setCharAt(i, ch);
                            String word = buffer.toString();
                            if(dict.contains(word)) {
                                Node wordNode = nodes.get(word);
                                if(wordNode == null) {
                                    wordNode = new Node(word, current.depth + 1);
                                    wordNode.prev.add(current);
                                    nodes.put(word, wordNode);
                                    queue.add(wordNode);
                                } else if(wordNode.depth == current.depth + 1) {
                                    wordNode.prev.add(current);
                                }
                            }
                        }
                    }
                }
            }

            if(nodes.get(end) != null)
                findShortestPaths(nodes.get(start), nodes.get(end), new ArrayList() , result);

            return result;
        }

        public void findShortestPaths(Node start, Node current, List<String> path, List<List<String>> result) {
            path.add(0, current.value);
            if (start.equals(current)) result.add(path);
            else {
                for (Node next : current.prev)
                    findShortestPaths(start, next, new ArrayList<String>(path), result);
            }
        }
    }

}
